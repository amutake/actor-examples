package me.amutake

import akka.actor._

object AkkaExample {

  class Worker extends Actor {
    def receive = {
      case "heavy" => {
        Thread.sleep(10000)
        println("heavy task done!")
      }
      case "light" => {
        println("light task done!")
      }
    }
  }

  def main(args: Array[String]) = {
    val system = ActorSystem()
    val worker = system.actorOf(Props[Worker])

    worker ! "heavy"
    worker ! "light"
  }
}
