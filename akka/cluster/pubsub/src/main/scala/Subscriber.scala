package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.contrib.pattern._
import akka.contrib.pattern.DistributedPubSubMediator._

class Subscriber extends Actor {
  val mediator = DistributedPubSubExtension(context.system).mediator
  mediator ! Subscribe("content", self)

  def receive = {
    case SubscribeAck(s) => println("SubscribeAck: " ++ s.toString)
    case msg: String => {
      println("Message: " ++ msg)
    }
  }
}
