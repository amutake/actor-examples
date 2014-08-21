package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.contrib.pattern._

object App {
  def main(args: Array[String]) {
    val system = ActorSystem("cluster")
    sys.addShutdownHook(system.shutdown)
    val seedNodes = collection.immutable.Seq[String](args:_*).map(AddressFromURIString(_))
    val publisher = system.actorOf(Props[Publisher], name = "publisher")
    val subscriber = system.actorOf(Props[Subscriber], name = "subscriber")
    val listener = system.actorOf(Props(classOf[Listener], seedNodes), name = "listener")
    publisher ! "go"
  }
}
