package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.JdbcProfile
import slick.lifted.{ProvenShape, QueryBase}
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {

  protected val driver: JdbcProfile

  import driver.api._

  case class DBUser(
                     userID: String,
                     firstName: Option[String],
                     lastName: Option[String],
                     fullName: Option[String],
                     email: Option[String],
                     avatarURL: Option[String]
                   )

  class Users(tag: Tag) extends Table[DBUser](tag, "user") {
    def id: Rep[String] = column[String]("userID", O.PrimaryKey)

    def firstName: Rep[Option[String]] = column[Option[String]]("firstName")

    def lastName: Rep[Option[String]] = column[Option[String]]("lastName")

    def fullName: Rep[Option[String]] = column[Option[String]]("fullName")

    def email: Rep[Option[String]] = column[Option[String]]("email")

    def avatarURL: Rep[Option[String]] = column[Option[String]]("avatarURL")

    def * : ProvenShape[DBUser] = (id, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo(
                          id: Option[Long],
                          providerID: String,
                          providerKey: String
                        )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def providerID: Rep[String] = column[String]("providerID")

    def providerKey: Rep[String] = column[String]("providerKey")

    def * : ProvenShape[DBLoginInfo] = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(
                              userID: String,
                              loginInfoId: Long
                            )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
    def userID: Rep[String] = column[String]("userID")

    def loginInfoId: Rep[Long] = column[Long]("loginInfoId")

    def * : ProvenShape[DBUserLoginInfo] = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(
                             hasher: String,
                             password: String,
                             salt: Option[String],
                             loginInfoId: Long
                           )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher: Rep[String] = column[String]("hasher")

    def password: Rep[String] = column[String]("password")

    def salt: Rep[Option[String]] = column[Option[String]]("salt")

    def loginInfoId: Rep[Long] = column[Long]("loginInfoId")

    def * : ProvenShape[DBPasswordInfo] = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info(
                           id: Option[Long],
                           token: String,
                           secret: String,
                           loginInfoId: Long
                         )

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "oauth1info") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def token: Rep[String] = column[String]("token")

    def secret: Rep[String] = column[String]("secret")

    def loginInfoId: Rep[Long] = column[Long]("loginInfoId")

    def * : ProvenShape[DBOAuth1Info] = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info(
                           id: Option[Long],
                           accessToken: String,
                           tokenType: Option[String],
                           expiresIn: Option[Int],
                           refreshToken: Option[String],
                           loginInfoId: Long
                         )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2info") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def accessToken: Rep[String] = column[String]("accesstoken")

    def tokenType: Rep[Option[String]] = column[Option[String]]("tokentype")

    def expiresIn: Rep[Option[Int]] = column[Option[Int]]("expiresin")

    def refreshToken: Rep[Option[String]] = column[Option[String]]("refreshtoken")

    def loginInfoId: Rep[Long] = column[Long]("logininfoid")

    def * : ProvenShape[DBOAuth2Info] = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  case class DBOpenIDInfo(
                           id: String,
                           loginInfoId: Long
                         )

  class OpenIDInfos(tag: Tag) extends Table[DBOpenIDInfo](tag, "openidinfo") {
    def id: Rep[String] = column[String]("id", O.PrimaryKey)

    def loginInfoId: Rep[Long] = column[Long]("logininfoid")

    def * : ProvenShape[DBOpenIDInfo] = (id, loginInfoId) <> (DBOpenIDInfo.tupled, DBOpenIDInfo.unapply)
  }

  case class DBOpenIDAttribute(
                                id: String,
                                key: String,
                                value: String
                              )

  class OpenIDAttributes(tag: Tag) extends Table[DBOpenIDAttribute](tag, "openidattributes") {
    def id: Rep[String] = column[String]("id")

    def key: Rep[String] = column[String]("key")

    def value: Rep[String] = column[String]("value")

    def * : ProvenShape[DBOpenIDAttribute] = (id, key, value) <> (DBOpenIDAttribute.tupled, DBOpenIDAttribute.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickOpenIDInfos = TableQuery[OpenIDInfos]
  val slickOpenIDAttributes = TableQuery[OpenIDAttributes]

  def loginInfoQuery(loginInfo: LoginInfo): Query[LoginInfos, DBLoginInfo, Seq] =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
