package models.tables

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class DbUser(
  userID: String,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarUrl: Option[String],
  activated: Boolean
)

case class DbLoginInfo(
  id: Option[Long],
  providerId: String,
  providerKey: String
)

case class DbUserLoginInfo(
  userId: String,
  loginInfoId: Long
)

case class DbPasswordInfo(
  hasher: String,
  password: String,
  salt: Option[String],
  loginInfoId: Long
)


class UserTable(tag: Tag) extends Table[DbUser](tag, "users") {

  def userID: Rep[String] = column[String]("user_id", O.PrimaryKey)

  def firstName: Rep[Option[String]] = column[Option[String]]("first_name")

  def lastName: Rep[Option[String]] = column[Option[String]]("last_name")

  def fullName: Rep[Option[String]] = column[Option[String]]("full_name")

  def email: Rep[Option[String]] = column[Option[String]]("email")

  def avatarURL: Rep[Option[String]] = column[Option[String]]("avatar_url")

  def activated: Rep[Boolean] = column[Boolean]("activated")

  override def * : ProvenShape[DbUser] = (userID, firstName, lastName, fullName, email, avatarURL, activated) <> (DbUser.tupled, DbUser.unapply)

}

class LoginInfoTable(tag: Tag) extends Table[DbLoginInfo](tag, "login_info") {

  def id: Rep[Option[Long]] = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  def providerId: Rep[String] = column[String]("provider_id")

  def providerKey: Rep[String] = column[String]("provider_key")

  override def * : ProvenShape[DbLoginInfo] = (id, providerId, providerKey) <> (DbLoginInfo.tupled, DbLoginInfo.unapply)

}

class UserLoginInfoTable(tag: Tag) extends Table[DbUserLoginInfo](tag, "user_login_info") {

  def userId: Rep[String] = column[String]("user_id")

  def loginInfoId: Rep[Long] = column[Long]("login_info_id")

  def * : ProvenShape[DbUserLoginInfo] = (userId, loginInfoId) <> (DbUserLoginInfo.tupled, DbUserLoginInfo.unapply)

}

class PasswordInfoTable(tag: Tag) extends Table[DbPasswordInfo](tag, "password_info") {

  def hasher: Rep[String] = column[String]("hasher")

  def password: Rep[String] = column[String]("password")

  def salt: Rep[Option[String]] = column[Option[String]]("salt")

  def loginInfoId: Rep[Long] = column[Long]("login_info_id")

  def * : ProvenShape[DbPasswordInfo] = (hasher, password, salt, loginInfoId) <> (DbPasswordInfo.tupled, DbPasswordInfo.unapply)

}
