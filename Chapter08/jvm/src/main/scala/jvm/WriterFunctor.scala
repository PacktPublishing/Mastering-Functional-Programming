package jvm

import cats._, cats.implicits._

object WriterFunctor extends App {
  implicit val simpleWriterFunctor: Functor[SimpleWriter] = new Functor[SimpleWriter] {
    override def map[A, B](fa: SimpleWriter[A])(f: A => B): SimpleWriter[B] =
      fa.copy(value = f(fa.value))
  }

  val x = SimpleWriter(Nil, 3)
  println(x.map(_ * 2))  // SimpleWriter(List(),6)
}
