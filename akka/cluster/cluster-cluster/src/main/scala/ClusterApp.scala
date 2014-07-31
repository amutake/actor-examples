package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.contrib.pattern._

object ClusterApp {

  def main(args: Array[String]) {
    val system = ActorSystem("cluster")
    sys.addShutdownHook(system.shutdown)
    val immArgs = collection.immutable.Seq[String](args:_*)
    val (seedNode, initialContacts): (Option[Address], Set[ActorSelection]) = if (immArgs.length >= 2) {
      (immArgs.headOption.map(AddressFromURIString(_)), immArgs.tail.toSet.map { addr: String =>
        system.actorSelection(addr ++ "/user/receptionist")
      })
    } else if (immArgs.length == 1) {
      (None, immArgs.headOption.toSet.map { addr: String =>
        system.actorSelection(addr ++ "/user/worker")
      })
    } else {
      (None, Set())
    }
    println(initialContacts)
    val client = system.actorOf(ClusterClient.props(initialContacts), name = "client")

    val worker = system.actorOf(Props(classOf[Worker], client), name = "worker")
    val listener = system.actorOf(Props(classOf[ClusterListener], seedNode, worker), name = "listener")
    ClusterReceptionistExtension(system).registerService(worker)
    worker ! "go"
    client ! ClusterClient.SendToAll("/user/worker", "hoge")
  }
}
