package jvm

import akka.actor._


object GameActorsImmutable extends App {
  case class User(name: String, score: Int)

  sealed trait Protocol
  case class   Connect      (user : User      ) extends Protocol
  case class   Disconnect   (user : User      ) extends Protocol
  case class   RewardWinners(users: Seq[User]) extends Protocol
  case object  Round                            extends Protocol

  class GameState(notifications: ActorRef) extends Actor {
    var onlineUsers = List[User]()

    def receive = {
      case Connect   (u) => onlineUsers :+= u
      case Disconnect(u) => onlineUsers = onlineUsers.filter(_ != u)
      case Round         => notifications ! RewardWinners(onlineUsers)
    }
  }

  class NotificationsActor extends Actor {
    def receive = {
      case RewardWinners(users) =>
        Thread.sleep(1000)
        val winners = users.filter(_.score >= 100)
        if (winners.nonEmpty) winners.foreach { u =>
          println(s"User $u is rewarded!") }
        else println("No one to reward!")
    }
  }


  val system = ActorSystem("GameActors")

  val notifications = system.actorOf(Props[NotificationsActor], name = "notifications")
  val gameState     = system.actorOf(Props(classOf[GameState], notifications), name = "gameState")

  val u1 = User("User1", 10)
  val u2 = User("User2", 100)

  gameState ! Connect(u1)
  gameState ! Connect(u2)
  gameState ! Round
  gameState ! Disconnect(u2)
}
