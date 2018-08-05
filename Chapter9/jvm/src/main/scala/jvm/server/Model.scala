package jvm.server

case class Customer(
  id  : Option[Int] = None
, name: String)

case class Good(
  id   : Option[Int] = None
, name : String
, price: Double
, stock: Int)

case class Order(
  id          : Option[Int] = None
, customer    : Int
, order_status: Int
, time_placed : Long = System.currentTimeMillis)
