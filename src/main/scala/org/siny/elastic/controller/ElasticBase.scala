package org.siny.elastic.controller

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, StringType}
import com.sksamuel.elastic4s.source.DocumentMap
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.sort.SortOrder.ASC
import org.elasticsearch.transport.RemoteTransportException
import org.siny.elastic.ElasticClientConnector
import org.siny.model.{Field, Tab}

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

  def createMap(indexName: String, indexType: String, docuemntMap: DocumentMap): String = {
    val resp = client.execute {
      index into indexName / indexType doc docuemntMap
    }.await
    resp.getId
  }

  def indexMapById(indexName: String, indexType: String, specifiedId: Integer, info: DocumentMap): IndexResponse = {
    val resp = client.execute {
      index into indexName / indexType doc info id specifiedId
    }.await
    resp
  }

  def addField(indexName: String, indexType: String, field: Field): Unit = {
    try {
      val name = field.name
      val value = field.value
      getAllTypeData(indexName, indexType).getHits.getHits.map(hit => {
        client execute {
          update id hit.id in indexName + "/" + indexType script s"ctx._source.$name = '$value'"
        }
      })
    } catch {
      case ime: RemoteTransportException => {
        ime.printStackTrace()
      }
      case e: Exception => throw e
    }
  }

  def removeField(indexName: String, indexType: String, field: Field): Unit = {
    try {
      val name = field.name
      val value = field.value
      getAllTypeData(indexName, indexType).getHits.getHits.map(hit => {
        client execute {
          update id hit.id in indexName + "/" + indexType script s"ctx._source.remove('$name')"
        }
      })
    } catch {
      case ime: RemoteTransportException => {
        ime.printStackTrace()
      }
      case e: Exception => throw e
    }
  }

  def getAllTypeData(indexName: String, indexType: String): SearchResponse = client.execute {
    search in indexName / indexType query filteredQuery postFilter matchAllFilter start 0 limit Integer.MAX_VALUE sort (
      by field "created_at" ignoreUnmapped true order ASC
      )
  }.await
}
