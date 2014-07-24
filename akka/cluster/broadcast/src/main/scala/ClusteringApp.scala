package me.amutake.clustering

import akka.actor._
import akka.cluster._

object ClusteringApp {
  def main(args: Array[String]) {
    val system = ActorSystem("cluster")
    val immArgs = collection.immutable.Seq[String](args:_*)
    val seedNodes = immArgs.map(AddressFromURIString(_))
    val worker = system.actorOf(Props[Worker], name = "worker")
    system.actorOf(Props(classOf[ClusterListener], seedNodes, worker), name = "listener")
    sys.addShutdownHook(system.shutdown())
  }
}
