package me.amutake.cluster.event

import akka.actor._
import akka.cluster._
import akka.contrib.pattern._

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("cluster")
    val seedNodes = args.map(port => Address("akka.tcp", "cluster", "127.0.0.1", port.toInt))
    val listener = system.actorOf(Props(classOf[Listener], seedNodes), name = "listener")
    sys.addShutdownHook(system.shutdown)
  }
}
