import sbt._

object Dependencies {

  private val Http4sVersion = "1.0.0-M29"
  private val CirceVersion = "0.14.1"
  private val MunitVersion = "0.7.29"
  private val LogbackVersion = "1.2.6"
  private val MunitCatsEffectVersion = "1.0.6"

  val libs = Seq(
    "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
    "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
    "org.http4s"      %% "http4s-circe"        % Http4sVersion,
    "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
    "io.circe"        %% "circe-generic"       % CirceVersion,
    "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
  )

  val testLibs = Seq(
    "org.scalameta"   %% "munit"               % MunitVersion           % Test,
    "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test
  )

}
