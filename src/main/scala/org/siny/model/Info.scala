package org.siny.model

import com.sksamuel.elastic4s.source.DocumentMap
import org.siny.util.HashUtil

/**
 * Info model
 * Created by chengpohi on 7/2/15.
 */
case class Info(password: String) extends DocumentMap{
  override def map: Map[String, Any] = Map("password" -> HashUtil.hashUserPassword(password))
}
