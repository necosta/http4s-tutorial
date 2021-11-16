package com.necosta.http4stutorial

import cats._
import cats.effect._
import cats.implicits._
import com.necosta.http4stutorial.Http4sTutorialUtils.{DirectorQueryParamMatcher, YearQueryParamMatcher}
import org.http4s._
import org.http4s.dsl.Http4sDsl

object Http4sTutorialRoutes {

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }

  def movieRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    import Movies._

    HttpRoutes.of[F] {
      case GET -> Root / "movies" :? DirectorQueryParamMatcher(director) +& YearQueryParamMatcher(maybeYear) =>
        val movieByDirector = findMoviesByDirector(director)
        (movieByDirector, maybeYear) match {
          case (Some(nelMoviesByDirector), Some(y)) =>
            y.fold(
              _ => BadRequest("The given year is not valid"),
              { year =>
                val moviesByDirAndYear = nelMoviesByDirector.filter(_.year.getValue == year.getValue)
                Ok(moviesByDirAndYear)
              }
            )
          case (Some(nelMoviesByDirector), None) => Ok(nelMoviesByDirector)
          case (None, _) => NotFound(s"No movies found for director $director")
        }
    }
  }

  def directorRoutes[F[_] : Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    import Directors._

    HttpRoutes.of[F] {
      case GET -> Root / "directors" / Directors(director) =>
        allDirectors.get(director.toString) match {
          case Some(dir) => Ok(dir)
          case _ => NotFound(s"No director called $director found")
        }
    }
  }
}