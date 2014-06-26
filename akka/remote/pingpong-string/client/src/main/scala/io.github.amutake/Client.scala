package io.github.amutake

import akka.actor._

object Client extends App {
  val system = ActorSystem("client")
  val client = system.actorOf(Props[ClientActor], name = "client")
  client ! "start"
}

class ClientActor extends Actor {
  val server = context.actorSelection("akka.tcp://server@127.0.0.1:2552/user/server")
  def receive = {
    case "start" => server ! "ping"
    case "pong" => {
      println("Client received pong message")
      Thread.sleep(1000)
      self ! "start"
    }
  }
}
