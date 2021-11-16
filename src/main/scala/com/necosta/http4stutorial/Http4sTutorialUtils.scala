package com.necosta.http4stutorial

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.{OptionalValidatingQueryParamDecoderMatcher, QueryParamDecoderMatcher}

import java.time.Year

object Http4sTutorialUtils {

  object DirectorQueryParamMatcher extends QueryParamDecoderMatcher[String]("director")

  implicit val yearQueryParamDecoder: QueryParamDecoder[Year] =
    QueryParamDecoder[Int].map(yearInt => Year.of(yearInt))

  object YearQueryParamMatcher extends OptionalValidatingQueryParamDecoderMatcher[Year]("year")
}
