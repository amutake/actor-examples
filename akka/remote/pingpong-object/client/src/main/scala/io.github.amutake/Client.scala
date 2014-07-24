package io.github.amutake

import akka.actor._
import io.github.amutake.types._

object Client extends App {
  val system = ActorSystem("client")
  val client = system.actorOf(Props[ClientActor], name = "client")
  client ! Start
}

class ClientActor extends Actor {
  val server = context.actorSelection("akka.tcp://server@127.0.0.1:2552/user/server")
  def receive = {
    case Start => server ! Ping("hogheoge")
    case Pong(msg) => {
      println("Client received pong message: " ++ msg)
      Thread.sleep(1000)
      self ! Start
    }
  }
}
