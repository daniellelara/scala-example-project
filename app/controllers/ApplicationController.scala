package controllers

import model._
import model.Protocol._
import play.api.mvc._
import services.UserService
import scala.concurrent.Future
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Writes._



class ApplicationController extends Controller {


  def getInJson = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      println(users)
      val json = Json.toJson(users)
      Ok(json).withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
        ACCESS_CONTROL_ALLOW_METHODS -> "Get",
        ACCESS_CONTROL_MAX_AGE -> "300",
        ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent")
    }
  }

  def index = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      println(users)
      Ok(views.html.index(UserForm.form, users))
    }
  }

  def addUser() = Action.async { implicit request =>
    UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.index(errorForm, Seq.empty[User]))),
      data => {
        val newUser = User(0, data.firstName, data.lastName, data.mobile, data.email)
        UserService.addUser(newUser).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      })
  }

  def addUserJson = Action(BodyParsers.parse.json) { implicit request =>
    val addUser = request.body.validate[User]
    addUser.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors)))
      },
      user => {
        UserService.addUser(user)
        Ok(Json.obj("status" -> "OK", "message" -> ("'user" + user.firstName + "' saved."))).withHeaders(
            ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
            ACCESS_CONTROL_ALLOW_METHODS -> "POST, OPTIONS, GET",
            ACCESS_CONTROL_MAX_AGE -> "300",
            ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent")
      }
    )
  }

  def checkPreFlight = Action {
    Ok("...").withHeaders(
      ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
      ACCESS_CONTROL_ALLOW_METHODS -> "POST, OPTIONS",
      ACCESS_CONTROL_MAX_AGE -> "300",
      ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent")
  }




  def deleteUser(id: Long) = Action.async { implicit request =>
    UserService.deleteUser(id) map { res =>
      Ok(Json.obj("status" -> "OK", "message" -> ("'user deleted"))).withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
        ACCESS_CONTROL_ALLOW_METHODS -> "POST, OPTIONS, GET",
        ACCESS_CONTROL_MAX_AGE -> "300",
        ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent")
    }

  }

}

