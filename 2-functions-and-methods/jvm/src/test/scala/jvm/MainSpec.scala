package jvm

import org.scalatest._
import org.scalatest.prop.Checkers

import org.scalacheck.Prop.forAll
import org.scalacheck.Gen
import org.scalacheck.Test


class MainSpec extends FlatSpec with Matchers with Checkers {

  val positives: Gen[Int] = Gen.oneOf[Int](1 to 10)
  val negatives: Gen[Int] = Gen.oneOf[Int](-1 to -10 by -1)

  "Positives and negatives" should "be addable" in check (
    forAll(positives, negatives) { (p, n) => (p + n) == p - math.abs(n) }
  , Test.Parameters.default)

}
