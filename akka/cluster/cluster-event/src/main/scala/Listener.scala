package me.amutake.cluster.event

import akka.actor._
import akka.cluster._
import akka.cluster.ClusterEvent._

class Listener(val seedNodes: Array[Address]) extends Actor {

  val cluster = Cluster(context.system)

  def d(any: Any) = {
    println("[DEBUG] " + any.toString)
  }

  override def preStart() = {
    if (seedNodes.length > 0) {
      println("Join: " + seedNodes.toString)
      cluster.joinSeedNodes(seedNodes.to[collection.immutable.Seq])
    } else {
      cluster.join(cluster.selfAddress)
    }
    cluster.subscribe(self, InitialStateAsEvents,
      classOf[ClusterDomainEvent]
      // classOf[MemberEvent],
      // classOf[ReachabilityEvent],
      // classOf[SubscriptionInitialStateMode]
    )
  }

  def receive = {
    case state: CurrentClusterState => {
      d("CurrentClusterState: " + state)
    }
    case LeaderChanged(leader) => {
      d("LeaderChanged: " + leader)

      // self ! cluster.send
    }
    case RoleLeaderChanged(role, leader) => {
      d("RoleLeaderChanged: " + role + ", " + leader.toString)
    }
    // MemberEvent
    case MemberExited(member) => {
      d("MemberExited: " + member)
    }
    // ReachabilityEvent
    case ReachableMember(member) => {
      d("ReachableMember: " + member)
    }
    case UnreachableMember(member) => {
      d("UnreachableMember: " + member)
    }
  }
}
