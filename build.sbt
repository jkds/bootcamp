import AssemblyKeys._

assemblySettings

name := "RESTful Adder Service"

version := "0.1"

scalaVersion := "2.9.1"

resolvers ++= Seq(
		"maven repo1" at "http://repo1.maven.org/maven2",
		"spray repo" at "http://repo.spray.io",
		"typesafe-repo" at "http://repo.typesafe.com/typesafe/releases/",
		"scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools/",
		"codahale" at "http://repo.codahale.com"
)

libraryDependencies ++= Seq(
		"io.spray" % "spray-can" % "1.0-M7",
		"io.spray" % "spray-routing" % "1.0-M7",
		"com.typesafe.akka" % "akka-actor" % "2.0.4"
)

autoCompilerPlugins := true

libraryDependencies <<= (scalaVersion, libraryDependencies) { (ver, deps) =>
                deps :+ compilerPlugin("org.scala-lang.plugins" % "continuations" % ver)
}

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Ydependent-method-types", "-P:continuations:enable")

jarName in assembly := "http-adder-service.jar"

mainClass in assembly := Some("org.kavanaghj.services.HttpBootstrap")

