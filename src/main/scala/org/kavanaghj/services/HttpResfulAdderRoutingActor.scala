package org.kavanaghj.services

import spray.routing.HttpServiceActor
import spray.http.StatusCodes._
import akka.actor.Actor
import akka.event.Logging

/**
 * Actor class that implements a simple RESTful service that
 * returns the result of a number supplied multiplied by 42
 */
class HttpResfulAdderRoutingActor extends Actor with HttpServiceActor {

  def receive = runRoute {
    path("add42" / IntNumber) {
      num : Int =>
        get {
          complete {
            (42+num).toString
          }
        }
    }
  }

}
