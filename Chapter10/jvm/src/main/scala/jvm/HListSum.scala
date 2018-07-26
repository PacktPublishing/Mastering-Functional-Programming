package jvm

case class Fraction(numerator: Int, denominator: Int)

object ListSum extends App {
  val list: List[Any] = List(0, 2.0, "3", Fraction(4, 2))
  val sum = list.map {
    case x: Int => x.toDouble
    case x: Double => x
    case x: String => x.toDouble
    // case Fraction(n, d) => n / d.toDouble
  }.sum
  println(sum)
}

object HListSum extends App {
  def sum[L <: HList, LR <: HList](hlist: L)(implicit m: MapToDouble.Aux[L, LR], s: Sum[LR]): Double =
    s.sum(m.map(hlist))

  val hlist: String ::: Int ::: Fraction ::: HNil = "1" ::: 2 ::: Fraction(3, 4) ::: HNil
  val s     = sum(hlist)
  println(s"Sum of $hlist is $s")
}

sealed trait HList {
  def :::[H](h: H): H ::: this.type = jvm.:::(h, this)
}
case class :::[+H, +T <: HList](head: H, tail: T) extends HList {
  override def toString() = s"$head ::: $tail"
}
trait HNil extends HList
case object HNil extends HNil

trait MapToDouble[L <: HList] {
  type Result <: HList
  def map(l: L): Result
}


trait Sum[L] {
  def sum(l: L): Double
}

trait ToDouble[T] {
  def toDouble(t: T): Double
}

object MapToDouble {
  type Aux[L <: HList, LR <: HList] = MapToDouble[L] { type Result = LR }
  def apply[L <: HList](implicit m: MapToDouble[L]) = m

  implicit def hcons[H, T <: HList, TR <: HList](implicit
    td: ToDouble[H]
  , md: MapToDouble.Aux[T, TR]
  ): Aux[H ::: T, Double ::: TR] = new MapToDouble[H ::: T] {

    type Result = Double ::: TR
    def map(l: H ::: T): Double ::: TR =
      td.toDouble(l.head) ::: md.map(l.tail)
  }

  implicit def hnil[H <: HNil]: MapToDouble.Aux[H, HNil] = new MapToDouble[H] {

    type Result = HNil
    def map(h: H) = HNil
  }
}

object Sum {
  def apply[L <: HList](implicit s: Sum[L]) = s

  implicit def hcons[T <: HList](implicit st: Sum[T]): Sum[Double ::: T] =
    { (l: Double ::: T) => l.head + st.sum(l.tail) }

  implicit def hnil[H <: HNil]: Sum[H] =
    { (x: HNil) => 0 }
}

object ToDouble {
  def apply[T](implicit t: ToDouble[T]) = t

  implicit def double: ToDouble[Double] = identity
  implicit def int   : ToDouble[Int   ] = _.toDouble
  implicit def string: ToDouble[String] = _.toDouble

  implicit def fraction: ToDouble[Fraction] =
    f => f.numerator / f.denominator.toDouble
}
