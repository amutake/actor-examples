package io.github.amutake

import akka.actor._

sealed trait Msg
case class Ping(msg: String, n: Int) extends Msg
case class Pong(msg: String, n: Int) extends Msg

object Server extends App {
  val system = ActorSystem("server")
  system.actorOf(Props[ServerActor], name = "server")
}

class ServerActor extends Actor {
  def receive = {
    case Ping(msg, n) => {
      println("Server received ping message: " ++ n.toString ++ " " ++ msg)
      sender ! Pong(msg, n + 1)
    }
  }
}
