package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import models.Role.NormalUser
import play.api.mvc._
import play.api.mvc.Results._
import reflect._
import models._
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import dao.AccountDAO

trait AuthConfigImpl extends AuthConfig {
    type Id = Long
    type Authority = Role
    type User = Account

    val idTag = classTag[Id]
    val sessionTimeoutInSeconds: Int = 3600

    val accountdao = new AccountDAO

    def resolveUser(id: Id)(implicit ctx: ExecutionContext) = Future.successful( accountdao.findById(id) )
    def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful( Redirect( routes.Application.index ) )
    def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful( Redirect( routes.Application.index ) )
    def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful( Redirect( routes.Application.index ) )
    def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful( Redirect( routes.Application.index ).flashing( "error" -> "no_permission" ) )

    def authorize( user: User, authority: Authority )( implicit ctx: ExecutionContext ) =
        Future.successful( ( user.role, authority ) match {
            case ( "Administrator", _ ) => true
            case ( "NormalUser", NormalUser ) => true
            case _ => false
        } )

    /**
     * Whether use the secure option or not use it in the cookie.
     * However default is false, I strongly recommend using true in a production.
     */
    override lazy val cookieSecureOption: Boolean = play.api.Play.isProd(play.api.Play.current)
}
