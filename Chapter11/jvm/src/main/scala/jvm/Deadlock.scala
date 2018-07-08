package jvm

import scala.collection.JavaConverters._

import java.io.File
import java.nio.charset.Charset

import org.apache.commons.io.FileUtils

object Deadlock {
  def main(args: Array[String]): Unit = {
    // Files we will be working with
    val input  = new File("goods.csv" )
    val log    = new File("log.txt"   )
    val output = new File("goods.json")

    // Encoding for the file I/O operations
    val encoding = "utf8"

    // Convenience method to construct threads
    def makeThread(f: => Unit): Thread =
      new Thread(new Runnable {
        override def run(): Unit = f
      })

    // Convenience method to write log
    def doLog(l: String): Unit = {
      FileUtils.write(
        log
      , l + "\n"
      , encoding
      , true  // Append to the file rather than rewrite it
      )
      println(s"Log: $l")  // Trace console output
    }

    // Convenience method to read the input file
    def readInput(): List[(String, Int)] =
      FileUtils.readLines(input, encoding).asScala.toList.tail
        .map(_.split(',').toList match {
          case name :: price :: Nil => (name, price.toInt)
        })

    val csv2json: Thread = makeThread {
      val inputList: List[(String, Int)] =
        input.synchronized {
          val result = readInput()
          log.synchronized {
            doLog(s"Read ${result.length} lines from input")
          }
          result
        }

      val json: List[String] =
        inputList.map { case (name, price) =>
          s"""{"Name": "$name", "Price": $price}""" }

      FileUtils.writeLines(output, json.asJava)
    }

    def statistics(avg: Boolean = true, max: Boolean = false, min: Boolean = false): Thread = makeThread {
      val inputList: List[(String, Int)] = log.synchronized {
        doLog(s"Computing the following stats: avg=$avg, max=$max, min=$min")
        val res = input.synchronized { readInput() }
        doLog(s"Read the input file to compute statistics on it")
        res
      }

      val prices: List[Int] = inputList.map(_._2)
      def reportMetrics(name: String, value: => Double): Unit = {
        val result = value
        log.synchronized { doLog(s"$name: $result") }
      }

      if (avg) reportMetrics("Average Price", prices.sum / prices.length.toDouble)
      if (max) reportMetrics("Maximal Price", prices.max)
      if (min) reportMetrics("Minimal Price", prices.min)
    }

    def statisticsSafe(avg: Boolean = true, max: Boolean = false, min: Boolean = false): Thread = makeThread {
      val inputList: List[(String, Int)] = input.synchronized {
        log.synchronized {
          doLog(s"Computing the following stats: avg=$avg, max=$max, min=$min")
          val res = readInput()
          doLog(s"Read the input file to compute statistics on it")
          res
        }
      }

      val prices: List[Int] = inputList.map(_._2)
      def reportMetrics(name: String, value: => Double): Unit = {
        val result = value
        log.synchronized { doLog(s"$name: $result") }
      }

      if (avg) reportMetrics("Average Price", prices.sum / prices.length.toDouble)
      if (max) reportMetrics("Maximal Price", prices.max)
      if (min) reportMetrics("Minimal Price", prices.min)
    }

    csv2json.start()
    statisticsSafe(true, true, true).start()
  }
}
