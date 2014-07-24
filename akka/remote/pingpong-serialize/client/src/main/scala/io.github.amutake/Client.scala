package io.github.amutake

import akka.actor._

sealed trait Msg
case class Ping(msg: String, n: Int) extends Msg
case class Pong(msg: String, n: Int) extends Msg

object Client extends App {
  val system = ActorSystem("client")
  val client = system.actorOf(Props[ClientActor], name = "client")
  client ! Pong("hi", 0)
}

class ClientActor extends Actor {
  val server = context.actorSelection("akka.tcp://server@127.0.0.1:2552/user/server")
  def receive = {
    case Pong(msg, n) => {
      println("Client received pong message: " ++ n.toString ++ " " ++ msg)
      Thread.sleep(1000)
      server ! Ping(msg, n + 1)
    }
  }
}
