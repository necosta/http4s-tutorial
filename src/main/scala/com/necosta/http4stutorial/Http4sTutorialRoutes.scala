package com.necosta.http4stutorial

import cats._
import cats.effect._
import cats.implicits._
import com.necosta.http4stutorial.Http4sTutorialUtils._
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
        maybeYear match {
          case Some(y) =>
            y.fold(
              e => {
                implicit val showError: Show[ParseFailure] = pf => s"${pf.message}"
                BadRequest(s"The given year is not valid. Error: ${e.show}")
              },
              { year => findMoviesByDirectorAndYear(director, year) match {
                  case Some(moviesByDirAndYear) => Ok(moviesByDirAndYear)
                  case None => NotFound(s"No movies found for director $director and year $year")
                }
              }
            )
          case None =>
            findMoviesByDirector(director) match {
              case Some(moviesByDir) => Ok(moviesByDir)
              case None => NotFound(s"No movies found for director $director")
            }
        }
    }
  }

  def directorRoutes[F[_] : Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    import Directors._

    HttpRoutes.of[F] {
      case GET -> Root / "directors" / Directors(director) =>
        findDirector(director.toString.toLowerCase) match {
          case Some(dir) => Ok(dir)
          case _ => NotFound(s"No director called ${director.toString} found")
        }
    }
  }
}