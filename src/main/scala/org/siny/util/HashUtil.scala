package org.siny.util

/**
 * HashUtil
 * Created by chengpohi on 7/2/15.
 */
object HashUtil {
  def hashUserPassword(password: String): String = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = password.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }
}
