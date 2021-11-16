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

import java.time.Year
import java.util.UUID

trait Movies[F[_]]{
  def get: F[Movies.Movie]
}

object Movies {
  def apply[F[_]](implicit ev: Movies[F]): Movies[F] = ev

  type Actor = String
  final case class Movie(id: String, title: String, year: Year, actors: List[Actor], director: String)
  final case class MovieError(e: Throwable) extends RuntimeException

  def impl[F[_]: Concurrent](C: Client[F]): Movies[F] = new Movies[F]{
    val dsl: Http4sClientDsl[F] = new Http4sClientDsl[F]{}
    import dsl._
    def get: F[Movies.Movie] = {
      C.expect[Movie](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError{ case t => MovieError(t)} // Prevent Client Json Decoding Failure Leaking
    }
  }

  // ToDo: Move hardcoded data
  val allMovies: Movie = Movie(
    "6bcbca1e-efd3-411d-9f7c-14b872444fce",
    "Zack Snyder's Justice League",
    java.time.Year.of(2021),
    List("Henry Cavill", "Gal Godot", "Ezra Miller", "Ben Affleck", "Ray Fisher", "Jason Momoa"),
    "Zack Snyder"
  )

  val movies: Map[String, Movie] = Map(allMovies.id -> allMovies)

  def findMovieById(movieId: UUID): Option[Movie] =
    movies.get(movieId.toString)

  def findMoviesByDirectorAndYear(director: String, year: Year): Option[List[Movie]] =
    listToOption(movies.values.filter(m => m.director == director && m.year.getValue == year.getValue))

  def findMoviesByDirector(director: String): Option[List[Movie]] =
    listToOption(movies.values.filter(_.director == director))

  // ToDo: Move to Utils file?
  def listToOption[T](list: Iterable[T]): Option[List[T]] =
    Option(list.toList).filter(_.nonEmpty).map(List[T])

  implicit val movieDecoder: Decoder[Movie] = deriveDecoder[Movie]
  implicit val movieEncoder: Encoder[Movie] = deriveEncoder[Movie]

  implicit def movieEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Movie] = jsonOf
  implicit def movieEntityEncoder[F[_]]: EntityEncoder[F, Movie] = jsonEncoderOf

  implicit def moviesEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, List[Movie]] = jsonOf
  implicit def moviesEntityEncoder[F[_]]: EntityEncoder[F, List[Movie]] = jsonEncoderOf

}
