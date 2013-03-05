package org.kavanaghj.services

import spray.can.server.SprayCanHttpServerApp
import akka.actor.Props

/**
 * Created: 27/02/13
 * Time: 14:45
 */
object HttpBootstrap extends App with SprayCanHttpServerApp {

  val BINDTO_HOST_PARAM = "bindHost"
  val DEFAULT_BIND_HOST = "localhost"
  val BINDTO_PORT_PARAM = "bindPort"
  //Specified as a string so that implicit conversion work on the
  //can be used in getBindPort method
  val DEFAULT_BIND_PORT = "8080"

  val argMap = parseArgs(args)

  val handler = system.actorOf(Props(new HttpResfulAdderRoutingActor), name = "adder-service")
  // create a new HttpServer using our handler and tell it where to bind to
  newHttpServer(handler) ! Bind(interface = getBindHost(argMap), port = getBindPort(argMap))

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
