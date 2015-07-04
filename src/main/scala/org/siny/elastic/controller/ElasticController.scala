package org.siny.elastic.controller


import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.sort.SortOrder.ASC
import org.elasticsearch.transport.RemoteTransportException
import org.siny.model.{Info, BookMark, Tab, User}
import org.siny.util.HashUtil
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.parsing.json.JSONObject

/**
 * Elastic Controller
 * Created by chengpohi on 3/1/15.
 */
object ElasticController extends ElasticBase {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)


  def createBookMark(user: User, bookMark: BookMark): String = createMap(user.name, BOOKMARK_TYPE, bookMark)

  def createTab(user: User, tab: Tab): String = createMap(user.name, TAB_TYPE, tab)

  def createUserInfo(user: User): String = {
    createIndex(user.name)
    val info: Info = Info(user.password.get)
    indexMapById(user.name, INFO_TYPE, 1, info).getId
  }

  def checkUserAccess(user: User): Boolean = {
    val resp = client.execute {
      get id 1 from user.name -> INFO_TYPE
    }.await
    resp.getSource.get("password").equals(
      HashUtil.hashUserPassword(user.password.get)
    )
  }


  def getBookMarksWithJson(user: User): String = {
    val result = for (hit <- getTabsWithObject(user)) yield {
      try {
        val resp = client.execute {
          search in user.name / BOOKMARK_TYPE query termQuery("_tab_id", hit.getId.toLowerCase) start 0 limit Integer.MAX_VALUE sort (
            by field "created_at" ignoreUnmapped true order ASC
            )
        }.await
        "\"" + hit.getSource.get("name") + "\" : { \"marks\": " + searchHitsToJSONString(resp.getHits.getHits) + ", \"id\": \"" + hit.getId + "\"}"
      } catch {
        case ime: RemoteTransportException => {
          ime.printStackTrace()
          null
        }
        case e: Exception => throw e
      }
    }

    result.mkString("{", ",", "}")
  }

  def getTabsWithObject(user: User): Array[SearchHit] = {
    try {
      getAllTypeData(user.name, TAB_TYPE).getHits.getHits
    } catch {
      case ime: RemoteTransportException => {
        ime.printStackTrace()
        null
      }
      case e: Exception => throw e
    }
  }

  def getBookMarksWithObject(user: User): Array[SearchHit] = {
    try {
      getAllTypeData(user.name, BOOKMARK_TYPE).getHits.getHits
    } catch {
      case ime: RemoteTransportException => null
      case e: Exception => throw e
    }
  }

  def deleteBookMarkById(_id: String, user: User): Unit = {
    client execute {
      delete id _id from user.name + "/" + BOOKMARK_TYPE
    }
  }

  def deleteIndexByIndexName(indexName: String): Unit = {
    client.execute {
      delete index indexName
    }.await
  }

  def updateBookMarkById(user: User, bookMark: BookMark): Unit = {
    client execute {
      update id bookMark.id.get in user.name + "/" + BOOKMARK_TYPE doc bookMark.map
    }
  }

  implicit def searchHitsToJSONString(hits: Array[SearchHit]): String = {
    val result = for (hit <- hits) yield {
      hit.getSource.put("id", hit.getId)
      JSONObject(hit.getSource.asScala.toMap).toString()
    }
    result.mkString("[", ",", "]")
  }
}
