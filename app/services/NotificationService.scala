package services

import java.io.File

import com.turo.pushy.apns.util.concurrent.PushNotificationFuture
import com.turo.pushy.apns.util.{ApnsPayloadBuilder, SimpleApnsPushNotification}
import com.turo.pushy.apns.{ApnsClient, ApnsClientBuilder, PushNotificationResponse}
import io.netty.util
import io.netty.util.concurrent.GenericFutureListener
import javax.inject.{Inject, Singleton}
import org.riversun.fcm.FcmClient
import org.riversun.fcm.model.EntityMessage
import play.api.Logger

import scala.concurrent.Future

object NotificationService {

  val MAX_TRIES = 3

}

@Singleton
class NotificationService @Inject()(configService: ConfigService)
                                   (implicit serviceExecutionContext: ServiceExecutionContext) {

  import NotificationService._

  private val log = Logger(this.getClass)

  private lazy val fcmClient = {
    val client = new FcmClient

    client.setAPIKey(configService.getString("firebase.api.key"))

    client
  }

  private lazy val apnBundleId = configService.getString("apple.apn.bundle.id")
  private lazy val apnCertPath = configService.getString("apple.apn.cert.path")
  private lazy val apnCertPass = configService.getString("apple.apn.cert.pass")

  private lazy val sandboxApnsClient = new ApnsClientBuilder()
    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
    .setClientCredentials(new File(apnCertPath), apnCertPass)
    .build()

  private lazy val prodApnsClient = new ApnsClientBuilder()
    .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
    .setClientCredentials(new File(apnCertPath), apnCertPass)
    .build()

    def sendMakeContact(token: String, ios: Boolean, secret: String, tst: Long): Unit = {
      log.info(s"Sending make contact PUSH.")

      sendMessage(
        token,
        ios,
        msg => {
          msg.putStringData("type", "MAKE_CONTACT")
          msg.putStringData("secret", secret)
          msg.putStringData("tst", tst.toString)
        }, builder => {
          builder.setCategoryName("MAKE_CONTACT")
          builder.setLocalizedAlertTitle("MAKE_CONTACT")
          builder.addCustomProperty("secret", secret)
          builder.addCustomProperty("tst", tst)
        })
    }

  private def sendMessage(token: String,
                          ios: Boolean,
                          fillAndroid: EntityMessage => Unit,
                          fillIos: ApnsPayloadBuilder => Unit): Unit = {
    try {
      if (ios) {
        val builder = new ApnsPayloadBuilder()

        fillIos(builder)

        builder.setSound("default")
        builder.setMutableContent(true)

        val payload = builder.buildWithDefaultMaximumLength()

        val pushNotification = new SimpleApnsPushNotification(token, apnBundleId, payload)

        sendResilientAPN(prodApnsClient, pushNotification, token)
        sendResilientAPN(sandboxApnsClient, pushNotification, token)
      } else {
        val msg = new EntityMessage

        msg.addRegistrationToken(token)

        fillAndroid(msg)

        Future {
          sendResilientFCM(msg, token)
        }(serviceExecutionContext)
      }
    } catch {
      case e: Exception =>
        log.error(s"Exception while sending notification: ${e.getMessage}", e)
    }
  }

  private def sendResilientAPN(client: ApnsClient, apn: SimpleApnsPushNotification, token: String, tries: Int = 1): Unit = {
    val notificationListener: GenericFutureListener[_ <: util.concurrent.Future[_ >: PushNotificationResponse[SimpleApnsPushNotification]]] =
      (future: PushNotificationFuture[SimpleApnsPushNotification,
        PushNotificationResponse[SimpleApnsPushNotification]]) => {
        if (future.isSuccess) {
          val response = future.getNow

          if (!response.isAccepted && response.getTokenInvalidationTimestamp != null) {
            log.error(s"The token $token is invalid as of " + response.getTokenInvalidationTimestamp)
          }
        } else {
          if (tries > MAX_TRIES) {
            log.error(s"Failed to send APN for $token after $tries tries!", future.cause())
          } else {
            log.warn(s"Failed to send APN for $token: ${future.cause.getMessage} tries: $tries", future.cause())

            sendResilientAPN(client, apn, token, tries + 1)
          }
        }
      }

    client.sendNotification(apn).addListener(notificationListener)
  }

  @scala.annotation.tailrec
  private def sendResilientFCM(msg: EntityMessage, token: String, tries: Int = 1): Unit = {
    try {
      fcmClient.pushToEntities(msg).getResult.forEach(result => {
        if (result.getError != null) {
          if (result.getError == "NotRegistered") {
            log.error(s"Removed not registered device token: $token")
          } else {
            log.warn(s"Failed to send FCM for $token: ${result.getError}")
          }
        }
      })
    } catch {
      case exception: Exception =>
        if (tries > MAX_TRIES) {
          log.error(s"Failed to send FCM for $token after $tries tries!", exception)
        } else {
          log.warn(s"Exception when trying to send FCM for $token tries: $tries", exception)

          sendResilientFCM(msg, token, tries + 1)
        }
    }
  }

}
