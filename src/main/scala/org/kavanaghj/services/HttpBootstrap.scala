package org.kavanaghj.services

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http.Bind
import scala.concurrent.duration._


/**
 * Created: 27/02/13
 * Time: 14:45
 */
object HttpBootstrap extends App {

  val BINDTO_HOST_PARAM = "bindHost"
  val DEFAULT_BIND_HOST = "localhost"
  val BINDTO_PORT_PARAM = "bindPort"
  //Specified as a string so that implicit conversion works when
  //calling the getBindPort method
  val DEFAULT_BIND_PORT = "8089"

  val argMap = parseArgs(args)

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("karj")

  // create and start our service actor
  val service = system.actorOf(Props[SparkServiceActor], "spark-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = getBindHost(argMap), port = getBindPort(argMap))

  private def parseArgs(args: Array[String]): Map[String, String] = {
    args.filter(_.contains("=")).map { arg =>
      val kv = arg.split("=")
      (kv(0), kv(1))
    }.toMap
  }

  private def getBindHost(args: Map[String, String]): String = {
    args.getOrElse(BINDTO_HOST_PARAM, DEFAULT_BIND_HOST)
  }

  private def getBindPort(args: Map[String,String]) : Int = {
    args.getOrElse(BINDTO_PORT_PARAM, DEFAULT_BIND_PORT).toInt
  }

}
