
name := "RESTful Adder Service"

version := "0.1"

scalaVersion := "2.10.4"

resolvers ++= Seq(
		"maven repo1" at "http://repo1.maven.org/maven2",
		"spray repo" at "http://repo.spray.io",
		"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
		"scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools/",
		"codahale" at "http://repo.codahale.com",
		"Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
	"org.apache.spark" %% "spark-core" % "1.1.0" % "provided",
	"org.apache.spark" %% "spark-sql" % "1.1.0" % "provided",
	"com.datastax.spark" %% "spark-cassandra-connector" % "1.1.0" % "provided",
	"io.spray" % "spray-can" % "1.2.2",
	"io.spray" % "spray-routing" % "1.2.2",
	"com.typesafe.akka" %% "akka-actor" % "2.2.4" % "provided"
)

autoCompilerPlugins := true

libraryDependencies <<= (scalaVersion, libraryDependencies) { (ver, deps) =>
                deps :+ compilerPlugin("org.scala-lang.plugins" % "continuations" % ver)
}

scalacOptions ++= Seq("-unchecked", "-deprecation", "-P:continuations:enable")

jarName in assembly := "http-spark-endpoint.jar"

mainClass in assembly := Some("org.kavanaghj.services.HttpBootstrap")

