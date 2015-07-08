package controllers

import dao.{AccountDAO, CatDAO}
import models.Cat
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import jp.t2v.lab.play2.auth.LoginLogout
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import views.html

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application extends Controller with LoginLogout with AuthConfigImpl {

    def dao = new CatDAO
    def accountDAO = new AccountDAO

    def index = Action.async {
        dao.all().map(c => Ok(views.html.index(c)))
    }

    private val catForm = Form(
        mapping(
            "name" -> text(),
            "color" -> text()
        )(Cat.apply)(Cat.unapply)
    )

    def insert = Action.async { implicit request =>
        val cat: Cat = catForm.bindFromRequest.get
        dao.insert(cat).map(_ => Redirect(routes.Application.index))
    }

    def loginForm = Action { implicit request =>
        Ok( views.html.application.login( dataForm ) )
    }

    def login = Action.async { implicit request =>
        dataForm.bindFromRequest.fold(
            formWithErrors => Future.successful( BadRequest( views.html.application.login( formWithErrors ) ) ),
            user           => gotoLoginSucceeded( user.get.id )
        )
    }

    def logout = Action.async { implicit request =>
        gotoLogoutSucceeded
    }

    /*
    val dataForm = Form{
        tuple(
            "email" -> email,
            "password" -> nonEmptyText
        )
    }
    */
    val dataForm = Form {
        mapping("email" -> email, "password" -> text)(accountDAO.authenticate)(_.map(u => (u.email, "")))
            .verifying("Invalid email or password", result => result.isDefined)
    }

    /** Your application's login form.  Alter it to fit your application */
    /*
    val loginForm = Form {
        mapping("email" -> email, "password" -> text)(accountDAO.authenticate)(_.map(u => (u.email, "")))
            .verifying("Invalid email or password", result => result.isDefined)
    }
    */

    /** Alter the login page action to suit your application. */
    /*
    def login = Action { implicit request =>
        Ok(html.login(loginForm))
    }
    */

    /**
     * Return the `gotoLogoutSucceeded` method's result in the logout action.
     *
     * Since the `gotoLogoutSucceeded` returns `Future[Result]`,
     * you can add a procedure like the following.
     *
     *   gotoLogoutSucceeded.map(_.flashing(
     *     "success" -> "You've been logged out"
     *   ))
     */
    /*
    def logout = Action.async { implicit request =>
        // do something...
        gotoLogoutSucceeded
    }
    */

    /**
     * Return the `gotoLoginSucceeded` method's result in the login action.
     *
     * Since the `gotoLoginSucceeded` returns `Future[Result]`,
     * you can add a procedure like the `gotoLogoutSucceeded`.
     */
    /*
    def authenticate = Action.async { implicit request =>
        loginForm.bindFromRequest.fold(
            formWithErrors => Future.successful(BadRequest(html.login(formWithErrors))),
            user => gotoLoginSucceeded(user.get.id)
        )
    }
    */
}