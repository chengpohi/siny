package org.siny.model

import com.sksamuel.elastic4s.source.DocumentMap

/**
 * BookMark model
 * Created by xiachen on 5/2/15.
 */
case class Tab (id: Option[String] = None, name: String) extends DocumentMap {
  override def map: Map[String, Any] = Map("name" -> name, "created_at" -> System.currentTimeMillis())
}
