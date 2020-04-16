package services

import javax.inject.{Inject, Provider, Singleton}
import play.api.Application

@Singleton
class ConfigService @Inject()(appProvider: Provider[Application]) {

  lazy val dev: Boolean = config.getOptional[Boolean]("dev").getOrElse(false)

  lazy val tracksApiToken: String = getString("tracks.api.token")
  lazy val tracksApiUrl: String = getString("tracks.api.url")

  def getString(param: String): String = config.get[String](param)

  private def config = appProvider.get.configuration

}
