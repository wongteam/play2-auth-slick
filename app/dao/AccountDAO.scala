package dao

import scala.concurrent.Future

import models.Account
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

class AccountDAO extends HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

  private val Accounts = TableQuery[AccountsTable]

  def findById(id: Long): Future[Option[Account]] =
    db.run(Accounts.filter(_.id === id).result.headOption)

  def all(): Future[List[Account]] = db.run(Accounts.result).map(_.toList)

  def insert(account: Account): Future[Unit] = db.run(Accounts += account).map(_ => ())

  private class AccountsTable(tag: Tag) extends Table[Account](tag, "account") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def password = column[String]("password")
    def name = column[String]("name")
    def role = column[String]("role")

    def * = (id.?, email, password, name, role) <> (Account.tupled, Account.unapply _)
  }
}