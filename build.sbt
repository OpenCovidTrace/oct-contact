name := """oct-contact"""
organization := "org.opencovidtrace"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// https://github.com/relayrides/pushy
libraryDependencies += "com.turo" % "pushy" % "0.13.10"
// https://mvnrepository.com/artifact/org.riversun/fcm
libraryDependencies += "org.riversun" % "fcm" % "0.2.0"

libraryDependencies += "au.com.flyingkite" % "mobiledetect" % "1.1.1"

// https://mvnrepository.com/artifact/io.sentry/sentry-logback
libraryDependencies += "io.sentry" % "sentry-logback" % "1.7.30"
