package org.siny.file

import org.scalatest.FlatSpec
import org.siny.web.file.FileUtils

/**
 * Created by xiachen on 4/19/15.
 */
class FileUtilsTest extends FlatSpec {
  "file util" should "get File" in {
    assert(FileUtils.getWebFile("/index.html").exists)
  }

  "file util" should "append default html" in {
    assert(FileUtils.getWebFile("/").exists)
    assert(FileUtils.getWebFile("/").getPath.endsWith("index.html"))
  }

  "file util" should "get File failed" in {
    assert(!FileUtils.getWebFile("/test").exists)
  }
}
