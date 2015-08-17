package org.siny.web.file

import java.io.File
import java.nio.file.Paths

import com.typesafe.config.ConfigFactory

/**
 * Created by xiachen on 4/19/15.
 */
object FileUtils {
  lazy val config = ConfigFactory.load()
  lazy val ROOT_DIRECTORY = config.getString("www.root")

  def getWebFile(uri: String): File = {
    val file = Paths.get(ROOT_DIRECTORY + "/" + uri).toFile
    file.isDirectory match {
      case true => new File(ROOT_DIRECTORY + "/" + uri + "/index.html")
      case false => file
    }
  }
}
