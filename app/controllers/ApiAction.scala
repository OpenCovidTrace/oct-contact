package controllers

import play.api.Logging
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * The purpose of this wrapper is to avoid ErrorHandler (and returning web friendly error) by catching exceptions.
  *
  * @param action
  * @tparam A
  */
case class ApiAction[A](action: Action[A]) extends Action[A] with Logging with Results {

  override def apply(request: Request[A]): Future[Result] = {
    action(request).recover {
      case se: SecurityException =>
        logger.error(s"Security exception [${request.method} ${request.uri}]: ${se.getMessage}", se)

        InternalServerError
      case e: Exception =>
        logger.error(s"Unexpected exception [${request.method} ${request.uri}]: ${e.getMessage}", e)

        InternalServerError
    }(executionContext)
  }

  override def parser: BodyParser[A] = action.parser

  override def executionContext: ExecutionContext = action.executionContext

}
