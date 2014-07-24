package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.contrib.pattern._

object ClusteringApp {
  def main(args: Array[String]) {
    val system = ActorSystem("cluster")
    val immArgs = collection.immutable.Seq[String](args:_*)
    val seedNodes = immArgs.map(AddressFromURIString(_))
    val worker = system.actorOf(Props[Worker], name = "worker")
    sys.addShutdownHook(system.shutdown())
    val listener = system.actorOf(Props(classOf[ClusterListener], seedNodes), name = "listener")
    ClusterReceptionistExtension(system).registerService(worker)
  }
}
