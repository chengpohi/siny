package org.siny.index

import org.scalatest.FlatSpec
import org.siny.elastic.index.ElasticController
import org.siny.model.{BookMark, User}

/**
 * BookMark model
 * Created by xiachen on 5/16/15.
 */
class ElasticControllerTest extends FlatSpec {
  val user = User("chengpohi")
  val bookMark = BookMark(Option(""), "jack", "http://www.baidu.com")
  "ElasticController " should " index user data" in {
    //ElasticController.create(null)
    ElasticController.getBookMarksWithJson(user) match {
      case null => ElasticController.create(user, bookMark)
      case s: String if s == "[]" => ElasticController.create(user, bookMark)
      case _ => println("USER HAVE EXISTED NOT NEED CREATE.")
    }
  }

  "ElasticController" should "get user info" in {
    println(ElasticController.getBookMarksWithJson(user))
    assert(ElasticController.getBookMarksWithJson(user) != null)
  }

  "ElasticController" should "get all user tabs" in {
    ElasticController.getTabsWithObject(user).map(s => println(s.getSource))
    assert(ElasticController.getTabsWithObject(user) != null)
  }

  "ElasticController" should "update bookMark by id" in {
    Thread.sleep(1000)
    val id = ElasticController.getBookMarksWithObject(user)(0).getId
    ElasticController.updateBookMarkById(user, BookMark(Option(id), "rose", "http://www.google.com"))
  }

  "ElasticController" should "delete bookMark by id" in {
    /*val id = ElasticController.getBookMarksWithObject(user)(0).getId
    ElasticController.deleteBookMarkById(id, user)*/
  }

}
