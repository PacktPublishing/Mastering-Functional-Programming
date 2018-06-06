package jvm

object WebAPI {
  // Domain model
  case class FullUser(name: String, id: Int, passwordHash: String)
  case class ShortUser(name: String, id: Int)

  val rootUser = FullUser("root", 0, "acbd18db4cc2f85cedef654fccc4a4d8")

  // Respond to the current request with the domain object
  def respondWith(user: ShortUser): Unit = ???

  // Converter to convert between full user and short user
  implicit def full2short(u: FullUser): ShortUser =
    ShortUser(u.name, u.id)

  // Request handler
  val handlerExplicit: PartialFunction[String, Unit] = {
    case "/root_user" => respondWith(full2short(rootUser))
  }

  val handlerImplicit: PartialFunction[String, Unit] = {
    case "/root_user" => respondWith(rootUser)
  }
}
