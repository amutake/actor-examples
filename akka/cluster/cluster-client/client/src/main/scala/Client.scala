package me.amutake.client

import akka.actor._
import akka.contrib.pattern._

object Client {
  def main(args: Array[String]) {
    val system = ActorSystem("client")
    val immArgs = collection.immutable.Seq[String](args:_*)
    val initialContacts: Set[ActorSelection] = immArgs.toSet.map { addr: String =>
      system.actorSelection(addr ++ "/user/receptionist")
    }
    println(initialContacts.mkString(", "))
    val c = system.actorOf(ClusterClient.props(initialContacts))
    (1 to 100).foreach { n: Int =>
      c ! ClusterClient.Send("/user/worker", "hello: " ++ n.toString, localAffinity = true)
      c ! ClusterClient.SendToAll("/user/worker", "hello: " ++ n.toString)
      // c ! ClusterClient.Publish("listener", "hello publish") ?
      Thread.sleep(1234)
    }
  }
}

// worker クラスタがいて、そいつらになんかできるやつ
// 適当に仕事投げたら適当に割り当てて、結果を返してくれるようなものとか
