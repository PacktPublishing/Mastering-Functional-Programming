package jvm.server
package db

import cats._, cats.implicits._, cats.effect._, cats.data._
import doobie._, doobie.implicits._

import infrastructure.tr

object CustomerDB extends App {
  val customersTest: IO[Unit] = for {
    id1 <- customer.create(Customer(name = "John Smith"))
    id2 <- customer.create(Customer(name = "Ann Watson"))

    _   = println(s"Looking up customers by name")
    c1 <- customer.findByName("John Smith")
    _   = println(c1)
    c2 <- customer.findByName("Foo")
    _   = println(c2)

    _   = println("\nAll customers")
    cs <- customer.list
    _   = println(cs.mkString("\n"))

    _   = println(s"\nCustomer with id $id1")
    c3 <- customer.get(id1)
    _   = println(c3)

    _   = println(s"\nUpdate customer with id $id1")
    r  <- customer.update(c3.copy(name = "Bob"))
    _   = println(s"Rows affected: $r")
    c4 <- customer.get(id1)
    _   = println(s"Updated customer: $c4")

    _   = println(s"\nClean-up: remove all customers")
    _  <- List(id1, id2).traverse(customer.delete)
    cx <- customer.list
    _   = println(s"Customers table after clean-up: $cx") 
  } yield ()

  customersTest.unsafeRunSync()
}

object customer extends CustomerDbHelpers {
  def create(c: Customer): IO[Int] =
    sql"""
      insert into customer (name)
      values (${c.name})
    """
    .update.withUniqueGeneratedKeys[Int]("id").transact(tr)

  def findByName(name: String): IO[Option[Customer]] =
    (selectCustomerSql ++ sql"""where name = $name""")
      .query[Customer].option.transact(tr)

  def list: IO[List[Customer]] =
    selectCustomerSql.query[Customer].to[List].transact(tr)

  def get(id: Int): IO[Customer] =
    (selectCustomerSql ++ sql"where id = $id")
      .query[Customer].unique.transact(tr)

  def update(c: Customer): IO[Int] =
    sql"""
      update customer set
        name = ${c.name}
      where id = ${c.id}
    """
    .update.run.transact(tr)

  def delete(id: Int): IO[Int] =
    sql"""delete from customer where id = $id"""
      .update.run.transact(tr)
}

trait CustomerDbHelpers {
  val selectCustomerSql =
    sql"""
      select
        id
      , name
      from customer
    """
}
