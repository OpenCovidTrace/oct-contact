package controllers


import au.com.flyingkite.mobiledetect.UAgentInfo
import javax.inject._
import play.api.Logging
import play.api.libs.json.JsValue
import play.api.mvc._
import services.{ConfigService, NotificationService}

import scala.concurrent.ExecutionContext

@Singleton
class ContactController @Inject()(val controllerComponents: ControllerComponents,
                                  configService: ConfigService,
                                  notificationService: NotificationService)
                                 (implicit ec: ExecutionContext) extends BaseController with Logging {

  def makeContact: Action[JsValue] = ApiAction {
    Action(parse.json) { implicit req: Request[JsValue] =>
      val token = (req.body \ "token").as[String]
      val platform = (req.body \ "platform").as[String]
      val secret = (req.body \ "secret").as[String]
      val tst = (req.body \ "tst").as[Long]

      notificationService.sendMakeContact(token, platform == "i", secret, tst)

      Ok
    }
  }

  def makeContactPage(): Action[AnyContent] = Action { implicit req =>
    Ok(views.html.makeContact(getDevice(), configService.appName, configService.appUrl))
  }

  private def getHeader[A](name: String)(implicit request: Request[Any]): Option[String] = {
    request.headers.get(name)
  }

  private def getUserAgent[A](implicit request: Request[A]): Option[String] = {
    getHeader("User-Agent")
  }

  private def getDevice[A]()(implicit request: Request[A]): DisplayDevice = getUserAgent.fold {
    DisplayDevice()
  } { userAgent =>
    val agentInfo = new UAgentInfo(userAgent, getHeader("Accept").orNull)

    DisplayDevice(agentInfo.detectAndroid(), agentInfo.detectIos())
  }
}

case class DisplayDevice(android: Boolean = false, ios: Boolean = false)
