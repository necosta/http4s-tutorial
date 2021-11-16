package com.necosta.http4stutorial

import java.time.Year

object Http4sTutorial {
  //movie database
  type Actor = String

  case class Movie(id: String, title: String, year: Year, actors: List[Actor], director: String)

  case class Director(firstName: String, lastName: String) {
    override def toString = s"$firstName $lastName"
  }
}
