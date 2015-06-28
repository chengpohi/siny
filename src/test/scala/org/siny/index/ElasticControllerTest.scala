package org.siny.index

import org.scalatest.FlatSpec
import org.siny.elastic.controller.ElasticController
import org.siny.model.{Tab, BookMark, User, Field}

/**
 * BookMark model
 * Created by xiachen on 5/16/15.
 */
class ElasticControllerTest extends FlatSpec {
  val user = User("chengpohi")
  val tab = Tab(Option(""), "tino")
  val bookMark = BookMark(Option(""), "jack", "http://www.baidu.com")

  "ElasticController " should " index user data" in {
    //ElasticController.create(null)
    ElasticController.getBookMarksWithJson(user) match {
      case null => ElasticController.createBookMark(user, bookMark)
      case s: String if s == "[]" => ElasticController.createBookMark(user, bookMark)
      case _ => println("USER HAVE EXISTED NOT NEED CREATE.")
    }
  }

  "ElasticController " should " create user tab" in {
    val resultId = ElasticController.createTab(user, tab)
    assert( resultId != null)
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

  "ElasticController" should "add field name" in {
    ElasticController.addField(user, ElasticController.BOOKMARK_TYPE, Field("test", "Hello"))
  }

  "ElasticController" should "delete bookMark by id" in {
    /*val id = ElasticController.getBookMarksWithObject(user)(0).getId
    ElasticController.deleteBookMarkById(id, user)*/
  }

}
