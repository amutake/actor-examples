package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.cluster.ClusterEvent._

class ClusterListener(val seedNodes: collection.immutable.Seq[Address], val worker: ActorRef) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

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
    case state: CurrentClusterState =>
      log.info("Current members: {}", state.members.mkString(", "))
    case MemberUp(member) => {
      log.info("Member is Up: {}", member.address)
      if (member.address != cluster.selfAddress) {
        worker ! member.address
      }
    }
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case any => // log.info("Any Event: " + any.toString)
  }
}
