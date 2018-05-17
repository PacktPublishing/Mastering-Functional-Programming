package jvm

object DatabaseLogged extends App {

  def getUserName(id: Int): String = {
    val name = s"User-$id"
    println(s"LOG: Requested user: $name")
    name
  }

  def getUserNamePure(id: Int): (List[String], String) = {
    val name = s"User-$id"
    val log  = List(s"LOG: Requested user: $name")
    (log, name)
  }

  val u = getUserName(10)

  val u2 = getUserNamePure(10)
}
