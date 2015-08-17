package org.siny.web.cache

import com.secer.elastic.model.User

import scala.collection._
import scala.collection.convert.decorateAsScala._
import java.util.concurrent.ConcurrentHashMap


/**
 * BookMark model
 * Created by chengpohi on 7/31/15.
 */
object LoginUserCache {
  val LOGINED_USER: concurrent.Map[String, User] = new ConcurrentHashMap[String, User]().asScala
}
