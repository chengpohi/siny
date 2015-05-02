package org.siny.web.elastic.index

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.StringDocumentSource
import org.siny.web.elastic.ElasticClientConnector
import org.siny.web.model.User
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.parsing.json.JSONObject
import scala.util.{Failure, Success}

/**
 * Created by xiachen on 3/1/15.
 */
object ElasticIndexer {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  lazy val client = ElasticClientConnector.client

  def createUser(user: User): Unit = {
    val state = client execute {

      index into "siny" -> user.name doc StringDocumentSource(JSONObject(Map("marks", user.tabs)).toString())
    }

    state onComplete {
      case Success(t) => LOG.info("Index Url: " + user.name + " Success")
      case Failure(t) => LOG.error("A Index Error Occurrence: " + t.getMessage)
    }
  }

}
