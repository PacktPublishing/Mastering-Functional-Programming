package crawler

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object MainSpec extends Properties("MainSpec") {

  property("&& law") = forAll { x: Boolean => (x && false) == false }
  property("|| law") = forAll { x: Boolean => (x || true ) == true  }

}
