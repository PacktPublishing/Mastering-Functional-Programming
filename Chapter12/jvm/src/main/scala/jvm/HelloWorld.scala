package jvm

import scala.language.postfixOps

import scala.concurrent.{ Future, Await }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import akka.event.Logging

import cats._, cats.implicits._, cats.data._, cats.effect._

object HelloWorld {
  case object Ping

  class HelloWorld extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case Ping ⇒ log.info("Hello World")
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    val helloWorld = system.actorOf(Props[HelloWorld], "hello-world")
    helloWorld ! Ping
  }
}

object ActorProperties {
  class HelloName(name: String) extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case "say-hello" ⇒ log.info(s"Hello, $name")
    }
  }

  object HelloName {
    def props(name: String): Props =
      Props(classOf[HelloName], name)
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("hello-custom")
    val helloPerson = system.actorOf(HelloName.props("Awesome Person"), "hello-name")
    val helloAlien  = system.actorOf(HelloName.props("Alien Invaders"), "hello-aliens")
    helloPerson ! "say-hello"
    helloAlien  ! "say-hello"
    helloAlien  ! "random-msg"
  }
}

object HierarchiesBase {
  case class  SpawnGreeters(n: Int)
  case class  SayHello(name: String)
  case class  Execute(f: () => Unit)
  case object JobDone
  case class  GreetersResolution(result: Try[ActorRef])
  case class  GreetersTerminated(result: List[Any])
  case object GreetersCreationAuthorised
  case object Die
  case object Dead


  class GreetingsManager extends Actor {
    val log = Logging(context.system, this)

    def greeterFromId(id: Any) = s"greeter-$id"

    def resolveGreeters() =
      context.actorSelection(greeterFromId("*")).resolveOne(1 second)
        .transformWith {
          case s@Success(_) => Future.successful(s)
          case f@Failure(_) => Future.successful(f)
        }
        .map(GreetersResolution) pipeTo self


    def spawningGreeters(requester: ActorRef, numGreeters: Int): Receive = {
      case GreetersResolution(Failure(_)) =>
        self ! GreetersCreationAuthorised
      
      case GreetersResolution(Success(_)) =>
        log.warning(s"Greeters already exist. Killing them and creating the new ones.")
        context.children
          .filter(c => raw"greeter-\d".r.unapplySeq(c.path.name).isDefined)
          .toList.traverse(_ ? Die)
          .map(GreetersTerminated) pipeTo self

      case GreetersTerminated(report) =>
        log.info(s"All greeters terminated, report: $report. Creating the new ones now.")
        self ! GreetersCreationAuthorised

      case GreetersCreationAuthorised =>
        (1 to numGreeters).foreach { id =>
          context.actorOf(Props[Greeter], greeterFromId(id)) }
        log.info(s"Created $numGreeters greeters")
        requester ! JobDone
        context become baseReceive
    }

    def sayingHello(requester: ActorRef, msg: Any): Receive = {
      case GreetersResolution(Failure(_)) =>
        log.error("There are no greeters. Please create some first with SpawnGreeters message.")
        context become baseReceive
        requester ! JobDone

      case GreetersResolution(Success(_)) =>
        log.info(s"Dispatching message $msg to greeters")
        context.actorSelection(greeterFromId("*")) ! msg
        context become baseReceive
        requester ! JobDone
    }

    def baseReceive: Receive = {
      case SpawnGreeters(n) =>
        log.info("Spawning {} greeters", n)
        resolveGreeters()
        context become spawningGreeters(sender, n)
      
      case msg@SayHello(_) =>
        resolveGreeters()
        context become sayingHello(sender, msg)

      case "resolve" =>
        val selection = context.actorSelection(greeterFromId("*"))
        selection.resolveOne(1 second).onComplete { res =>
          log.info(s"Selection: $selection; Res: $res") }
    }

    def receive = baseReceive
  }

  class Greeter extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case SayHello(name) => log.info(s"Greetings to $name")
      case Die =>
        context stop self
        sender ! Dead
    }
  }

  implicit val timeout: Timeout = 3 seconds
  val system = ActorSystem("hierarchy-demo")
  val gm = system.actorOf(Props[this.GreetingsManager], "greetings-manager")
}

object HierarchiesResolve extends App {
  import HierarchiesBase._
  Await.ready(for {
    _ <- Future { gm ! "resolve" }
    _ <- gm ? SpawnGreeters(10)
    _ <- (1 to 10).toList.traverse(_ => Future { gm ! "resolve" })
  } yield (), 5 seconds)
}

object HierarchiesDemo extends App {
  import HierarchiesBase._
  
  def printState(childrenEmpty: Boolean, isHelloMessage: Boolean) =
    Future { println(s"\n=== Children: ${if (childrenEmpty) "empty" else "present"}, " +
      s"Message: ${if (isHelloMessage) "SayHello" else "SpawnGreeters"}") }

  Await.ready(for {
    _ <- printState(true, true)
    _ <- gm ? SayHello("me")

    _ <- printState(true, false)
    _ <- gm ? SpawnGreeters(3)

    _ <- printState(false, false)
    _ <- gm ? SpawnGreeters(3)

    _ <- printState(false, true)
    _ <- gm ? SayHello("me")
  } yield (), 5 seconds)
}
