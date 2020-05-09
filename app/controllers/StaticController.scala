package controllers

import javax.inject._
import play.api.mvc._
import services.ConfigService

@Singleton
class StaticController @Inject()(val controllerComponents: ControllerComponents,
                                 configService: ConfigService,
                                 val assets: Assets) extends BaseController {

  def apns(): Action[AnyContent] = Action {
    Ok(
      s"""
         |{
         |  "applinks": {
         |    "apps": [],
         |    "details": [
         |      {
         |        "appID": "${configService.appId}",
         |        "paths": [
         |          "/app/*"
         |        ]
         |      }
         |    ]
         |  }
         |}
         |""".stripMargin).withHeaders(
      "Content-Type" -> "text/plain",
      "Content-Disposition" -> "attachment; filename=apple-app-site-association"
    )
  }

}
