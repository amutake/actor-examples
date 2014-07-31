package me.amutake.clustering

import akka.actor._
import akka.cluster._
import akka.cluster.ClusterEvent._

class ClusterListener(val seedNode: Option[Address], val worker: ActorRef) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart() = {
    seedNode.fold({
      cluster.join(cluster.selfAddress)
    })({ s =>
      cluster.joinSeedNodes(collection.immutable.Seq(s))
    })
    cluster.subscribe(self, classOf[ClusterDomainEvent])
  }

  def receive = {
    // case state: CurrentClusterState => {}
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case any => {
      // println(any.toString)
    }
  }
}
