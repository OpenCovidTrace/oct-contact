import io.sentry.Sentry
import javax.inject.Inject
import services.ConfigService

import scala.concurrent.ExecutionContext

trait StartupComponent

class StartupComponentImpl @Inject()(configService: ConfigService)
                                    (implicit ec: ExecutionContext) extends StartupComponent {

  Sentry.init(configService.getString("sentry.url"))

}
