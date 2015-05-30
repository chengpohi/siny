package org.siny.web.elastic.index


import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.SearchHit
import org.elasticsearch.transport.RemoteTransportException
import org.siny.web.elastic.ElasticClientConnector
import org.siny.web.model.{BookMark, User}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.parsing.json.JSONObject

/**
 * BookMark model
 * Created by chengpohi on 3/1/15.
 */
object ElasticController {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  lazy val client = ElasticClientConnector.client

  def create(user: User, bookMark: BookMark): Unit = {
    client.execute {
      index into user.name / "bookMarks" doc bookMark
    }.await
  }

  def getBookMarksWithJson(user: User): String = {
    try {
      val resp = client.execute {
        search in user.name / "bookMarks" query "*" start 0 limit Integer.MAX_VALUE
      }.await
      resp.getHits.getHits
    } catch {
      case ime: RemoteTransportException => null
      case e: Exception => throw e
    }
  }

  def getBookMarksWithObject(user: User): Array[SearchHit]= {
    try {
      val resp = client.execute {
        search in user.name / "bookMarks" query "*"
      }.await
      resp.getHits.getHits
    } catch {
      case ime: RemoteTransportException => null
      case e: Exception => throw e
    }
  }

  def deleteBookMarkById(_id: String, user: User): Unit = {
    client execute {
      delete id _id from user.name + "/bookMarks"
    }
  }

  def updateBookMarkById(user: User, bookMark: BookMark): Unit = {
    client execute {
      update id bookMark.id.get in user.name + "/bookMarks" doc bookMark.map
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
