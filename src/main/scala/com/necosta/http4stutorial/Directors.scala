package com.necosta.http4stutorial

import cats.effect.Concurrent
import com.necosta.http4stutorial.Http4sTutorial.{Actor, Director}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

import scala.collection.mutable
import scala.util.Try

object Directors {

  val directors: mutable.Map[Actor, Director] =
    mutable.Map("Zack Snyder" -> Director("Zack", "Snyder"),
      "John Doe" -> Director("John", "Doe"))

  def unapply(str: String): Option[Director] = {
    if (str.nonEmpty && str.matches(".* .*")) {
      Try {
        val splitStr = str.split(' ')
        Director(splitStr(0), splitStr(1))
      }.toOption
    } else None
  }

  implicit val directorDecoder: Decoder[Director] = deriveDecoder[Director]
  implicit def directorEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Director] =
    jsonOf
  implicit val directorEncoder: Encoder[Director] = deriveEncoder[Director]
  implicit def directorEntityEncoder[F[_]]: EntityEncoder[F, Director] =
    jsonEncoderOf
}
