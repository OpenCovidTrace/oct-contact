package filters

import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.{Logger, Logging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AccessLoggingFilter @Inject()(implicit val mat: Materializer) extends Filter with Logging {

  private val accessLogger = Logger("access")

  logger.info("Initialized.")

  def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    val resultFuture = next(request)

    resultFuture.foreach(result => {
      val msg = s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}" +
        s" status=${result.header.status}"
      accessLogger.info(msg)
    })

    resultFuture
  }

}
