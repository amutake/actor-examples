package io.github.amutake

import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.remote.RemoteScope

class Child extends Actor {
  def receive = {
    case e: Exception => {
      println(e.toString)
      throw e
    }
  }
}

class Parent extends Actor {
  val address = Address("akka.tcp", "remote", "127.0.0.1", 2552)
  val child = context.actorOf(Props[Child].withDeploy(Deploy(scope = RemoteScope(address))), name = "child")

  override val supervisorStrategy = OneForOneStrategy() {
    case _ => {
      println("Child actor died!")
      Resume
    }
  }

  def receive = {
    case e: Exception => child ! e
  }
}

object Remote extends App {
  val system = ActorSystem("system")
  val parent = system.actorOf(Props[Parent], name ="parent")
  // while (true) {
  //   Thread.sleep(2000)
  //   parent ! new NullPointerException
  // }
}
