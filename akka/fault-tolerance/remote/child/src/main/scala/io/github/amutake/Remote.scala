package io.github.amutake

import akka.actor._

class Child extends Actor {
  def receive = {
    case "ping" => {
      println("ping received")
      sender ! "pong"
    }
    case e: Exception => {
      println(e.toString)
      context.stop(self)
    }
  }
}

object Remote extends App {
  val system = ActorSystem("remote")
  system.actorOf(Props[Child], "child")
}
