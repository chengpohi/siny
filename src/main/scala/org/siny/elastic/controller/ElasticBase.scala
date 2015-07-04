package org.siny.elastic.controller

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, StringType}
import com.sksamuel.elastic4s.source.DocumentMap
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.sort.SortOrder.ASC
import org.elasticsearch.transport.RemoteTransportException
import org.siny.elastic.ElasticClientConnector
import org.siny.model.{Tab, Info, Field, User}

/**
 * ElasticBase function
 * Created by chengpohi on 6/28/15.
 */
class ElasticBase {
  lazy val client = ElasticClientConnector.client

  val BOOKMARK_TYPE: String = "bookmark"
  val TAB_TYPE: String = "tab"
  val INFO_TYPE: String = "info"

  def createIndex(indexName: String): Unit = {
    client.execute {
      create index indexName mappings(
        "bookmark" as(
          "name" typed StringType,
          "url" typed StringType,
          "_tab_id" typed StringType,
          "created_at" typed DateType
          ),
        "tab" as(
          "name" typed StringType,
          "created_at" typed DateType
          ),
        "info" as(
          "password" typed StringType,
          "created_at" typed DateType
          )
        )
    }.await

    val tab: Tab = Tab(Option(""), "All")
    client.execute {
      index into indexName / TAB_TYPE doc tab id 1
    }.await
  }

  def createMap(user: User, indexType: String, docuemntMap: DocumentMap): String = {
    val resp = client.execute {
      index into user.name / indexType doc docuemntMap
    }.await
    resp.getId
  }

  def createMapWithId(user: User, indexType: String, specifiedId: Integer): String = {
    createIndex(user.name)
    val info: Info = Info(user.password.get)
    val resp = client.execute {
      index into user.name / indexType doc info id specifiedId
    }.await
    resp.getId
  }

  def addField(user: User, indexType: String, field: Field): Unit = {
    try {
      val name = field.name
      val value = field.value
      getAllTypeData(user, indexType).getHits.getHits.map(hit => {
        client execute {
          update id hit.id in user.name + "/" + indexType script s"ctx._source.$name = '$value'"
        }
      })
    } catch {
      case ime: RemoteTransportException => {
        ime.printStackTrace()
      }
      case e: Exception => throw e
    }
  }

  def removeField(user: User, indexType: String, field: Field): Unit = {
    try {
      val name = field.name
      val value = field.value
      getAllTypeData(user, indexType).getHits.getHits.map(hit => {
        client execute {
          update id hit.id in user.name + "/" + indexType script s"ctx._source.remove('$name')"
        }
      })
    } catch {
      case ime: RemoteTransportException => {
        ime.printStackTrace()
      }
      case e: Exception => throw e
    }
  }

  def getAllTypeData(user: User, indexType: String): SearchResponse = client.execute {
    search in user.name / indexType query "*" start 0 limit Integer.MAX_VALUE sort (
      by field "created_at" ignoreUnmapped true order ASC
      )
  }.await
}
