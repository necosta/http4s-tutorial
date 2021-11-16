package com.necosta.http4stutorial

import munit.CatsEffectSuite

class DirectorsSpec extends CatsEffectSuite{
  test("Unknown director should not be found") {
    assert(Directors.findDirector("").isEmpty, "List should be empty")
  }
  test("Know director should be returned") {
    assert(Directors.findDirector("John doe").isDefined, "List should not be empty")
  }
}
