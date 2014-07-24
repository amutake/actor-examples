package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.cluster.ClusterEvent._

case class Msg(msg: String, n: Int)

class ClusterListener(val seedNodes: collection.immutable.Seq[Address], val worker: ActorRef) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  // case class Msg(msg: String, n: Int)
  // この位置に case class を置くとエラー

  override def preStart(): Unit = {
    if (seedNodes.length > 0) {
      log.info(seedNodes.toString)
      cluster.joinSeedNodes(seedNodes)
    } else {
      log.info(cluster.selfAddress.toString)
      cluster.join(cluster.selfAddress)
    }
    cluster.subscribe(self, classOf[ClusterDomainEvent])
  }

  def receive = {
    case state: CurrentClusterState => {
      log.info("Current members: {}", state.members.mkString(", "))
    }
    case MemberUp(member) => {
      log.info("Member is Up: {}", member.address)
      log.info("Current members: {}", cluster.state.members.mkString(", "))
    }
    case UnreachableMember(member) =>
      println("!!!!!!!" ++ member.address.port.toString ++ "!!!!!!!")
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case Msg(msg, n) => {
      println(n.toString ++ " " ++ msg)
    }
    case any => {
      // broadcasting
      println(cluster.state.members.mkString(", "))
      cluster.state.members.foreach { m =>
        if (m.address != cluster.selfAddress) {
          val actor = context.actorSelection(s"${m.address.toString}/user/listener")
          actor ! Msg(any.toString.take(20) ++ "... from " ++ cluster.selfAddress.toString, 0)
        }
      }
    }
  }
}
