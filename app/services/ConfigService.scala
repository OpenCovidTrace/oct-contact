package services

import javax.inject.{Inject, Provider, Singleton}
import play.api.Application

@Singleton
class ConfigService @Inject()(appProvider: Provider[Application]) {


  lazy val appAndroidId: String = getString("app.android.id")
  lazy val appAndroidFingerprint: String = getString("app.android.fingerprint")
  lazy val appIosId: String = getString("app.ios.id")
  lazy val appName: String = getString("app.name")
  lazy val appUrl: String = getString("app.url")

  def getString(param: String): String = config.get[String](param)

  private def config = appProvider.get.configuration

}
