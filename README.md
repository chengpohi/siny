# Siny
The ***Siny*** web server is written by ***Scala***.

#install

```
libraryDependencies ++= {
  Seq(
    "org.siny.web" % "siny_2.11" % "1.0"
  )
}
```

#Configuration

##Configure web root

```
www {
  root = "www/public/coolmarks/"
}
```

##Configure IP port

```
http {
  interface = "0.0.0.0"
  port = 9000
}
```

#App Core

app class need to extend `Siny`, and `override` `registerPath` to `bind` `path` with method.

##Extend App

```
object App extends Siny {
  def main(args: Array[String]): Unit = {
    this.initialize()
  }

  override def registerPath(): Unit = {
    registerHandler("POST", "/register.html", registerUser)
    registerHandler("POST", "/login.html", userLogin)
    registerHandler("GET", "/user.html", userInfo)

    registerHandler("GET", "/bookmark", BookMarkController.getBookMarks)
    registerHandler("POST", "/bookmark", BookMarkController.postBookMark)
    registerHandler("DELETE", "/bookmark", BookMarkController.deleteBookMark)

    registerHandler("POST", "/tab", TabController.postBookMark)
  }
}
```

##Define Action

`Action` extends from `RestAction`, for the register method, it needs to have `HttpSession` parameter
and `return` `HttpResponse` parameter

Example:

```
object UserController extends RestAction{
  def registerUser(httpSession: HttpSession): HttpResponse = {
    HttpResponse(register(httpSession.user), OK)
  }
}
```
