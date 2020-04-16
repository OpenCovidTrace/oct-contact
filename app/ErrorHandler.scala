import java.util.Locale

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(implicit messagesApi: MessagesApi) extends HttpErrorHandler {

  private val LOG = Logger(this.getClass)

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    implicit val lang: Lang = request.acceptLanguages.headOption.getOrElse(Lang(Locale.ENGLISH))

    if (statusCode == play.api.http.Status.NOT_FOUND) {
      Future.successful(NotFound(views.html.error("Page not found.")))
    } else if (statusCode == play.api.http.Status.BAD_REQUEST) {
      Future.successful(BadRequest(views.html.error("Bad request.")))
    } else {
      Future.successful(new Status(statusCode)(views.html.error("Something went wrong...")))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    LOG.error(s"Error in request [${request.method} ${request.uri}]", exception)

    Future.successful(InternalServerError(views.html.error("Server error.")))
  }

}
