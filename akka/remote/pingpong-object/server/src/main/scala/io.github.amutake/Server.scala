package io.github.amutake

import akka.actor._
import io.github.amutake.types._

object Server extends App {
  val system = ActorSystem("server")
  system.actorOf(Props[ServerActor], name = "server")
}

class ServerActor extends Actor {
  def receive = {
    case Ping(msg, n) => {
      println("Server received ping message: " ++ n.toString ++ " " ++ msg)
      sender ! Pong("heyehyehey", n + 1)
    }
  }
}
