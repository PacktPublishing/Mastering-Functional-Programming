package jvm

object Bank {
  class Connection
  case class User(id: Option[Int], name: String)
  case class Account(id: Option[Int], ownerId: Int, balance: Double)

  def createUser(u: User, c: Connection): Int = ???
  def createAccount(a: Account, c: Connection): Int = ???

  def registerNewUser(name: String, c: Connection): Int = {
    val uid   = createUser(User(None, name), c)
    val accId = createAccount(Account(None, uid, 0), c)
    accId
  }

  def createUserFunc   (u: User   ): Connection => Int = ???
  def createAccountFunc(a: Account): Connection => Int = ???

  def registerNewUserFunc(name: String): Connection => Int = { c: Connection =>
    val uid   = createUserFunc(User(None, name))(c)
    val accId = createAccountFunc(Account(None, uid, 0))(c)
    accId
  }

  case class Reader[A, B](f: A => B) {
    def apply(a: A): B = f(a)

    def flatMap[C](f2: B => Reader[A, C]): Reader[A, C] =
      Reader { a => f2(f(a))(a) }
  }

  def createUserReader   (u: User   ): Reader[Connection, Int] = ???
  def createAccountReader(a: Account): Reader[Connection, Int] = ???

  def registerNewUserReader(name: String): Reader[Connection, Int] =
    createUserReader(User(None, name)).flatMap { uid =>
      createAccountReader(Account(None, uid, 0)) }
}
