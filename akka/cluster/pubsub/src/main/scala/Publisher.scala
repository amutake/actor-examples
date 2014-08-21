package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.contrib.pattern._
import akka.contrib.pattern.DistributedPubSubMediator._

class Publisher extends Actor {
  val mediator = DistributedPubSubExtension(context.system).mediator

  def receive = {
    case "go" => {
      mediator ! Publish("content", "hi")
    }
  }
}
