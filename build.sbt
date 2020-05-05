name := """aero-ui"""
maintainer := "Jean Wisser"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
libraryDependencies += "com.aerospike" % "aerospike-client" % "4.4.7"

dockerExposedPorts in Docker := (for(i <- 3000 to 4000) yield i)
