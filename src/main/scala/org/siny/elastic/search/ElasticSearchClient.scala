package org.siny.elastic.search

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit
import org.siny.elastic.ElasticClientConnector


/**
 * Created by chengpohi on 3/19/15.
 */
object ElasticSearchClient {
  lazy val client = ElasticClientConnector.client
  lazy val m = java.security.MessageDigest.getInstance("MD5")

  def hash(s: String): String = {
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }

  def searchCall(indexName: String, indexType: String, field: String, key: String): SearchResponse = ???

  def comparePage(indexName: String, indexType: String, html: String, url: String): SearchResponse = ???

  def searchUrl(indexName: String, indexType: String, key: String): SearchHit = ???
}
