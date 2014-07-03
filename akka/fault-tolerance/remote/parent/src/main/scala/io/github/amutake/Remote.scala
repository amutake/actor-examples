package io.github.amutake

import akka.actor._
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global

class Parent extends Actor {
  val child = context.actorSelection("akka.tcp://remote@127.0.0.1:2552/user/child")
  child.resolveOne(new FiniteDuration(1, SECONDS)).foreach { c =>
    context.watch(c)
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case _ => {
      println("Child actor died!")
      Resume
    }
  }

  def receive = {
    case "go" => child ! "ping"
    case "pong" => {
      println("pong received")
      child ! new ArithmeticException
      Thread.sleep(1000)
      self ! "go"
    }
  }
}

object Remote extends App {
  val system = ActorSystem("system")
  val parent = system.actorOf(Props[Parent], name ="parent")
  parent ! "go"
}
