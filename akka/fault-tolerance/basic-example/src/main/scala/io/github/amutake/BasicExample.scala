package io.github.amutake

import akka.actor._
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._

case object Go
case object Get

class Child extends Actor {
  var state = 0
  def receive = {
    case e: Exception => throw e
    case n: Int => state = n
    case Get => sender ! state
  }
}

class Supervisor extends Actor {
  val child = context.actorOf(Props[Child], name = "child")

  override val supervisorStrategy = OneForOneStrategy() {
    case _: ArithmeticException => {
      println("ArithmeticException!")
      Resume
    }
    case _: NullPointerException => {
      println("NullPointerException!")
      Restart
    }
    case _: NumberFormatException => {
      println("NumberFormatException!")
      Stop
    }
    case _: Exception => {
      println("Exception!")
      Escalate
    }
  }

  def receive = {
    case Get => sender ! child
  }
}

class Sender extends Actor {
  def receive = {
    case child: ActorRef => {
      child ! 10
      child ! new ArithmeticException
      child ! Get

      child ! new NullPointerException
      child ! Get

      child ! new NumberFormatException

      child ! 12
      child ! Get
      child ! new Exception
    }
    case Go => sender ! Get
    case n => println(n)
  }
}

object BasicExample extends App {
  val system = ActorSystem("system")
  val supervisor = system.actorOf(Props[Supervisor], name = "supervisor")
  val sender = system.actorOf(Props[Sender], name = "sender")
  sender.tell(Go, supervisor)
}
