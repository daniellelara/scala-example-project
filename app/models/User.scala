package model

import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsObject, _}
import play.api.libs.functional.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Writes._
import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import scala.concurrent._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.http.HeaderNames._



case class User(id: Long, firstName: String, lastName: String, mobile: Long, email: String)


case class UserFormData(firstName: String, lastName: String, mobile: Long, email: String)



class UserTableDef(tag: Tag) extends Table[User](tag, "user") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def mobile = column[Long]("mobile")
  def email = column[String]("email")

  override def * =
    (id, firstName, lastName, mobile, email) <> (User.tupled, User.unapply)
}




object UserForm {

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobile" -> longNumber,
      "email" -> email
    )(UserFormData.apply)(UserFormData.unapply)
  )
}


object Protocol {

  implicit val userFormat = Json.format[User]
  implicit val userReads = Json.reads[User]


}


object Users {





  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val users = TableQuery[UserTableDef]


  def add(user: User): Future[String] = {
    dbConfig.db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.id === id).result.headOption)
  }



  def listAll: Future[Seq[User]] = {
    dbConfig.db.run(users.result)

  }
}