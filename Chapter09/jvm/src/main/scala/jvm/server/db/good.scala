package jvm.server
package db

import cats._, cats.implicits._, cats.effect._, cats.data._
import doobie._, doobie.implicits._

import infrastructure.tr

object good extends GoodDbHelpers {
  def create(c: Good): IO[Int] =
    sql"""
      insert into good (
        name
      , price
      , stock)
      values (
        ${c.name}
      , ${c.price}
      , ${c.stock})
    """
    .update.withUniqueGeneratedKeys[Int]("id").transact(tr)

  def findByName(name: String): IO[Option[Good]] =
    (selectGoodSql ++ sql"""where name = $name""")
      .query[Good].option.transact(tr)

  def list: IO[List[Good]] =
    selectGoodSql.query[Good].to[List].transact(tr)

  def get(id: Int): IO[Good] =
    (selectGoodSql ++ sql"where id = $id")
      .query[Good].unique.transact(tr)

  def update(c: Good): IO[Int] =
    sql"""
      update good set
        name  = ${c.name }
      , price = ${c.price}
      , stock = ${c.stock}
      where id = ${c.id}
    """
    .update.run.transact(tr)

  def delete(id: Int): IO[Int] =
    sql"""delete from good where id = $id"""
      .update.run.transact(tr)
}

trait GoodDbHelpers {
  val selectGoodSql = fr"select * from good"
}
