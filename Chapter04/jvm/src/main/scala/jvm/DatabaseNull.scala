package jvm

object DatabaseNull extends App {
  case class User(name: String)

  def getUser(id: Int): User =
    if (Set(1, 2, 3).contains(id)) User(s"User-$id")
    else null


  println(getUser(1 ).name)  // User-1
  println(getUser(10).name)  // NullPointerException
}
