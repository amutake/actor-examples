package me.amutake.clustering

import akka.actor._

class Worker extends Actor with ActorLogging {
  def receive = {
    case addr: Address => {
      val remote = context.actorSelection(s"${addr.toString}/user/worker")
      remote ! 100
    }
    case n: Int => {
      println(n.toString)
      Thread.sleep(1000)
      sender ! (n - 1)
    }
  }
}
