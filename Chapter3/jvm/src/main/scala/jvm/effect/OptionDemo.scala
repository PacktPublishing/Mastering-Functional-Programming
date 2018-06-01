package jvm.effect

import scala.util.Try

object TryDemo extends App {
  def getUserName(id: Int): Option[String] =
    if (Set(1, 2, 3).contains(id)) Some(s"User-$id")
    else None

  getUserName(1) match {  // "User-1"
    case Some(x) => println(x)
    case None    => println("User not found")
  }

  getUserName(10) match {  // "User not found"
    case Some(x) => println(x)
    case None    => println("User not found")
  }
}
