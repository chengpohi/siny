package org.siny.web.server.rest.controller

import org.json4s.DefaultFormats

/**
 * siny
 * Created by chengpohi on 8/14/15.
 */
trait RestAction{
  implicit val formats = DefaultFormats
}
