package jvm.lambda

object Lambda {
  def main(args: Array[String]): Unit = {
    case class Cookie(name: String, gender: String)

    def greeting(cookie: Cookie)(modifier: (String, String) => String): Unit = {
      val name         = cookie.name
      val gender       = cookie.gender
      val modifiedName = modifier(name, gender)
      print(s"Hello, $modifiedName")
    }

    def isPhd(name: String): Boolean = name == "Smith"

    val cookie = Cookie("Smith", "male")

    greeting(cookie) { (name, gender) =>
      if (isPhd(name)) s"Dr $name"
      else gender match {
        case "male"   => s"Mr $name"
        case "female" => s"Mrs $name"
      }
    }
  }
}
