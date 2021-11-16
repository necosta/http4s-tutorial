package com.necosta.http4stutorial

import cats.Applicative
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait HelloWorld[F[_]]{
  def hello(n: HelloWorld.Name): F[HelloWorld.Greeting]
}

object HelloWorld {
  implicit def apply[F[_]](implicit ev: HelloWorld[F]): HelloWorld[F] = ev

  final case class Name(name: String)
  final case class Greeting(greeting: String)

  def impl[F[_]: Applicative]: HelloWorld[F] = (n: HelloWorld.Name) => Greeting("Hello, " + n.name).pure[F]

  implicit val greetingEncoder: Encoder[Greeting] = (a: Greeting) => Json.obj(
    ("message", Json.fromString(a.greeting)),
  )
  implicit def greetingEntityEncoder[F[_]]: EntityEncoder[F, Greeting] = jsonEncoderOf[F, Greeting]
}
