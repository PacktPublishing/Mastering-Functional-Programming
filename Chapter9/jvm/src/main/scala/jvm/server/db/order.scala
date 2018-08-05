package jvm.server
package db

import cats.{ Order => COrder, _ }, cats.implicits._, cats.effect._, cats.data._
import doobie._, doobie.implicits._

import infrastructure.tr

object order extends OrderDbHelpers {
  def create(o: Order): IO[Int] =
    sql"""
      insert into "order" (customer, good)
      values (${o.customer}, ${o.good})
    """
    .update.withUniqueGeneratedKeys[Int]("id").transact(tr)

  def list: IO[List[Order]] =
    selectOrderSql.query[Order].to[List].transact(tr)

  def get(id: Int): IO[Order] =
    (selectOrderSql ++ sql"where id = $id")
      .query[Order].unique.transact(tr)
}

trait OrderDbHelpers {
  val selectOrderSql = fr"""select * from "order""""
}
