name := """language-learner"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4.1208",
  "org.scalatest" % "scalatest_2.11" % "3.0.0-M15",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.20",
  "org.seleniumhq.selenium" % "selenium-java" % "2.49.0",
  "com.h2database" % "h2" % "1.4.191"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

javaOptions in Test += "-Dconfig.file=conf/application_test.conf"
scalacOptions in Test ++= Seq("-Yrangepos")
