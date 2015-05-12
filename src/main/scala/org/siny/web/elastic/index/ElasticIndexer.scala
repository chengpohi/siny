package org.siny.web.elastic.index

import org.siny.web.elastic.ElasticClientConnector
import org.siny.web.model.User
import org.slf4j.LoggerFactory

/**
 * Created by xiachen on 3/1/15.
 */
object ElasticIndexer {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  lazy val client = ElasticClientConnector.client

  def createUser(user: User): Unit = {
  }

}
