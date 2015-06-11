package org.siny.file

import org.scalatest.FlatSpec

/**
 * Created by xiachen on 4/19/15.
 */
class FileUtilsTest extends FlatSpec {
  "file util" should "get File" in {
    assert(FileUtils.getFile("/index.html").exists)
  }

  "file util" should "append default html" in {
    assert(FileUtils.getFile("/").exists)
    assert(FileUtils.getFile("/").getPath.endsWith("index.html"))
  }

  "file util" should "get File failed" in {
    assert(!FileUtils.getFile("/test").exists)
  }
}
