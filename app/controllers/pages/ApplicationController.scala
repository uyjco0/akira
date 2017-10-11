package controllers.pages

import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ LogoutEvent, HandlerResult, Silhouette }

import controllers.{ pages }
import models.User
import actors.WsActor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.stream.Materializer

import play.api.i18n.{ I18nSupport, Messages }
import play.api.mvc.{ AnyContentAsEmpty, AbstractController, AnyContent, ControllerComponents, Action, Request, WebSocket }
import play.api.libs.streams.ActorFlow
import play.filters.headers.SecurityHeadersFilter
import utils.auth.DefaultEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The basic application controller.
 *
 * @param components The Play controller components.
 * @param silhouette The Silhouette stack.
 */
class ApplicationController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]) (implicit system: ActorSystem, materializer: Materializer, ex: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(views.html.home(request.identity)).withHeaders(SecurityHeadersFilter.CONTENT_SECURITY_POLICY_HEADER -> s"connect-src 'self' ws://$request.host http://ipinfo.io/;"))
  }

  def wsSocket = WebSocket.acceptOrResult[String, String] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Right(ActorFlow.actorRef(WsActor.props(user)))
      case HandlerResult(r, None) => Left(r)
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val result = Redirect(pages.routes.ApplicationController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
