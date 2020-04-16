package services

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.CustomExecutionContext

@Singleton
class ServiceExecutionContext @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "service-execution-context")
