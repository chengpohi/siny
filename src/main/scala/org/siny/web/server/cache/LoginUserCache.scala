package org.siny.web.server.cache

import com.secer.elastic.model.User

import scala.collection.mutable.ListBuffer

/**
 * BookMark model
 * Created by chengpohi on 7/31/15.
 */
object LoginUserCache {
  var LOGINED_USER = ListBuffer[User]()
}
