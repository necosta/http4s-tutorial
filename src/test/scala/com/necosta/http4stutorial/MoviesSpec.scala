package com.necosta.http4stutorial

import munit.CatsEffectSuite

class MoviesSpec extends CatsEffectSuite {
  test("Unknown director should not be found") {
    assert(Movies.findMoviesByDirector("").isEmpty, "List should be empty")
  }

  test("Know director should return value") {
    assert(Movies.findMoviesByDirector("Zack Snyder").isDefined, "List should not be empty")
  }
}
