package org.siny.elastic.shell

import org.siny.elastic.controller.ElasticController
import org.siny.model.{User, Field}

import scala.io.StdIn.readLine

/**
 * BookMark model
 * Created by chengpohi on 6/28/15.
 */
object ElasticShell {
  def main(args: Array[String]): Unit = {
    while (true) {
      println("Input Command:")
      val line = readLine()
      line match {
        case "af" => {
          println("Add a Field:")
          println("Input User Name:")
          val user = User(readLine())
          println("Input Type Name:")
          val indexType = readLine()
          println("Input Field Name:")
          val fieldName = readLine()
          println("Input Field Value:")
          val fieldValue = readLine()
          ElasticController.addField(user, indexType, Field(fieldName, fieldValue))
        }
        case "rf" => {
          println("Remove a Field:")
          println("Input User Name:")
          val user = User(readLine())
          println("Input Type Name:")
          val indexType = readLine()
          println("Input Field Name:")
          val fieldName = readLine()
          ElasticController.removeField(user, indexType, Field(fieldName, ""))
          println("remove finished ~~~")
        }
        case _ =>
      }
    }
  }
}
