package io.github.amutake

import akka.actor._
import akka.remote.RemoteScope

object Master extends App {
  val system = ActorSystem("master")
  val address = AddressFromURIString("akka.tcp://server@127.0.0.1:2552")
  system.actorOf(
    Props[ServerActor].withDeploy(Deploy(scope = RemoteScope(address))),
    name = "server"
  )
}

class ServerActor extends Actor {
  def receive = {
    case "ping" => {
      println("Server received ping message")
      sender ! "pong"
    }
  }
}
