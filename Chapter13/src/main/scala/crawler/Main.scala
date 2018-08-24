package crawler

import java.net.URL

import scala.util.Try
import scala.collection.JavaConverters._

import org.jsoup.nodes._
import org.jsoup._

object Main {
  def fetch(url: URL): Option[Set[URL]] = Try {
    Jsoup.connect(url.toString)
      .get
      .getElementsByAttribute("href")
      .asScala.map( h => new URL(url, h.attr("href")) ).toSet
  }.toOption

  def fetchToDepth(url: URL, depth: Int, visited: Set[URL] = Set()): Set[URL] = {
    val links = fetch(url).getOrElse(Set())

    if (depth > 0) links ++ links
      .filter(!visited(_))
      .toList
      .zipWithIndex
      .foldLeft(Set[URL]()) { case (accum, (next, id)) =>
        println(s"Progress for depth $depth: $id of ${links.size}")
        accum ++ (if (!accum(next)) fetchToDepth(next, depth - 1, accum) else Set())
      }
      .toSet
    else links
  }

  def main(args: Array[String]): Unit = {
    val target = new URL("http://mvnrepository.com/")

    val res = fetchToDepth(target, 1)
    println(res.take(10).mkString("\n"))
    println(res.size)
  }
}
