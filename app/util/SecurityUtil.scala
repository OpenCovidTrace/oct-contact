package util

import java.security.spec.{InvalidKeySpecException, X509EncodedKeySpec}
import java.security.{KeyFactory, MessageDigest, Signature}
import java.util.Base64

import play.api.Logging


object SecurityUtil extends Logging {

  private val secureDigest = MessageDigest.getInstance("SHA-256")

  def hash(value: String): String = {
    Base64.getEncoder.encodeToString(secureDigest.digest(value.getBytes("UTF-8")))
  }

  def verifySignature(message: String,
                      signature: String,
                      publicKeyEncoded: String): Boolean = {
    try {
      val rsaVerify = Signature.getInstance("SHA256withECDSA")

      val publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder.decode(publicKeyEncoded))

      val keyFactory = KeyFactory.getInstance("EC")
      val publicKey = keyFactory.generatePublic(publicKeySpec)

      rsaVerify.initVerify(publicKey)
      rsaVerify.update(message.getBytes("UTF-8"))
      rsaVerify.verify(Base64.getDecoder.decode(signature))
    } catch {
      case e: InvalidKeySpecException =>
        logger.error(e.getMessage, e)

        false
    }
  }

}
