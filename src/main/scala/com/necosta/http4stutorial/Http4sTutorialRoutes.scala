package com.necosta.http4stutorial

import cats._
import cats.effect._
import cats.implicits._
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

  /*def movieRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "movies" :? DirectorQueryParamMatcher(director) +& YearQueryParamMatcher(maybeYear) => ???
      case GET -> Root / "movies" / UUIDVar(movieId) / "actors" => ???
    }
  }*/

  def directorRoutes[F[_] : Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    import Directors._

    HttpRoutes.of[F] {
      case GET -> Root / "directors" / Directors(director) => {
        directors.get(director.toString) match {
          case Some(dir) => Ok(dir)
          case _ => NotFound(s"No director called $director found")
        }
      }
    }
  }
}