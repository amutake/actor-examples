package io.github.amutake

import akka.actor._

object Server extends App {
  val system = ActorSystem("server")
  system.actorOf(Props[ServerActor], name = "server")
}

class ServerActor extends Actor {
  def receive = {
    case "ping" => {
      println("Server received ping message")
      sender ! "pong"
    }
  }
}
