name := """akira"""
version := "1.0.1"
scalaVersion := "2.12.2"

resolvers ++= Seq(
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" %% "akka-stream" % "2.5.6",
  "com.mohiva" %% "play-silhouette" % "5.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.0",
  "com.mohiva" %% "play-silhouette-cas" % "5.0.0",
  "org.webjars" %% "webjars-play" % "2.6.2",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "ficus" % "1.4.2",
  "org.postgresql" % "postgresql" % "42.1.4",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
  "joda-time" % "joda-time" % "2.9.9",
  "org.joda" % "joda-convert" % "1.9.2",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  ehcache,
  jdbc,
  guice,
  filters
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
pipelineStages := Seq(digest)

routesGenerator := InjectedRoutesGenerator

routesImport += "utils.route.Binders._"
