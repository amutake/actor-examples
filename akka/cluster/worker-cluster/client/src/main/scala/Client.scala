package me.amutake.client

import akka.actor._
import akka.contrib.pattern._

object ClientApp {
  def main(args: Array[String]) {
    val system = ActorSystem("client")
    val immArgs = collection.immutable.Seq[String](args:_*)
    val initialContacts: Set[ActorSelection] = immArgs.toSet.map { addr: String =>
      system.actorSelection(addr ++ "/user/receptionist")
    }
    println(initialContacts.mkString(", "))
    val c = system.actorOf(ClusterClient.props(initialContacts))
    val client = system.actorOf(Props[Client])
    client ! c
  }

  class Client extends Actor {
    def receive = {
      case c: ActorRef => {
        (30 to 40).foreach { n: Int =>
          val uuid = java.util.UUID.randomUUID.toString
          c ! ClusterClient.SendToAll("/user/worker", (uuid, n))
        }
      }
      case n: Int => {
        println(n)
      }
    }
  }
}
