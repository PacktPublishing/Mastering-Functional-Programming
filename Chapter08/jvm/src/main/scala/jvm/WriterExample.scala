package jvm

import cats._, cats.implicits._, cats.data._

object WriterExample extends App {
  def add(a: Double, b: Double): Writer[List[String], Double] =
    for {
      _   <- Writer.tell(List(s"Adding $a to $b"))
      res  = a + b
      _   <- Writer.tell(List(s"The result of the operation is $res"))
    } yield res

  println(add(1, 2))  // WriterT((List(Adding 1.0 to 2.0, The result of the operation is 3.0),3.0))

}