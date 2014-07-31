package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.contrib.pattern._

class Worker(val client: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case msg: String => {
      Thread.sleep(1000)
      println(msg)
      client ! ClusterClient.Send("/user/worker", "hi", localAffinity = true)
    }
  }
}
