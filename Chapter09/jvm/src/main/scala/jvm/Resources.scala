package jvm

import scala.language.postfixOps
import cats._, cats.implicits._, cats.effect._, cats.data._

object ProductRight extends App {
  def foo = IO[Int] { println("Foo"); 1 }
  def bar = IO[Int] { println("Bar"); 2 }

  println( (foo *> bar).unsafeRunSync )
}

object Bracket extends App {
  class DBSession {
    var closed = false
    def runStatement(stat: String): IO[List[String]] =  {
      val computation = IO {
        if (stat.contains("user")) List("John", "Ann")
        else if (stat.contains("post")) List("Post1", "Post2")
        else Nil
      }
      if (!closed) computation
      else IO.raiseError { new RuntimeException("Connection is closed") }
    }
    def close(): Unit = closed = true
    def isClosed = closed
  }

  def dbSession: IO[DBSession] = IO { new DBSession }

  def selectUsers(db: DBSession): IO[List[String]] =
    dbSession.flatMap(_.runStatement("select * from user"))

  var sessIntercept: DBSession = null
  val computation: IO[Unit] =
    dbSession.bracket(sess => for {
      users <- selectUsers(sess)

      _ = println(s"Users:\n${users.mkString("\n")}")
      _ = sessIntercept = sess
    } yield ())(sess => IO { sess.close() })

  println(s"Session intercept before execution: $sessIntercept")
  computation.unsafeRunSync
  println(s"Session intercept after execution: $sessIntercept")
  println(s"Session intercept closed status: ${sessIntercept.isClosed}")
}
