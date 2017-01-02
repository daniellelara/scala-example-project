package services

/**
  * Created by daniellegourgey1 on 27/12/2016.
  */

import model.{User, Users}
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.functional.syntax._


object UserService {



  def addUser(user: User): Future[String] = {
    Users.add(user)
  }

  def deleteUser(id: Long): Future[Int] = {
    Users.delete(id)
  }

  def getUser(id: Long): Future[Option[User]] = {
    Users.get(id)
  }

  def listAllUsers: Future[Seq[User]] = {
    Users.listAll
  }
}