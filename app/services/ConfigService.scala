package services

import javax.inject.{Inject, Provider, Singleton}
import play.api.Application

@Singleton
class ConfigService @Inject()(appProvider: Provider[Application]) {


  lazy val appId: String = getString("app.id")
  lazy val appName: String = getString("app.name")
  lazy val appUrl: String = getString("app.url")

  def getString(param: String): String = config.get[String](param)

  private def config = appProvider.get.configuration

}
