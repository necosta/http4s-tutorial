package com.necosta.http4stutorial

import cats.effect.Concurrent
import cats.implicits.catsSyntaxMonadError
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.Method.GET
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits.http4sLiteralsSyntax

trait Movies[F[_]]{
  def get: F[Movies.Movie]
}

object Movies {
  def apply[F[_]](implicit ev: Movies[F]): Movies[F] = ev

  final case class Movie(movie: String) extends AnyVal

  object Movie {
    implicit val movieDecoder: Decoder[Movie] = deriveDecoder[Movie]
    implicit def movieEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Movie] =
      jsonOf
    implicit val movieEncoder: Encoder[Movie] = deriveEncoder[Movie]
    implicit def movieEntityEncoder[F[_]]: EntityEncoder[F, Movie] =
      jsonEncoderOf
  }

  final case class MovieError(e: Throwable) extends RuntimeException

  def impl[F[_]: Concurrent](C: Client[F]): Movies[F] = new Movies[F]{
    val dsl = new Http4sClientDsl[F]{}
    import dsl._
    def get: F[Movies.Movie] = {
      C.expect[Movie](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError{ case t => MovieError(t)} // Prevent Client Json Decoding Failure Leaking
    }
  }
}
