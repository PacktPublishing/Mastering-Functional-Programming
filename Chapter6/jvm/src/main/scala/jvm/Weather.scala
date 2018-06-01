package jvm

import scala.concurrent.{ Future, ExecutionContext }
import java.util.concurrent.Executors


object Weather {
  case class Event(time: Long, location: String)

  def getEvent(id: Int): Event = {
    Thread.sleep(1000)  // Simulate delay
    Event(System.currentTimeMillis, "New York")
  }

  def getWeather(time: Long, location: String): String = {
    Thread.sleep(1000) // Simulate delay
    "bad"
  }

  def notifyUser(): Unit = {
    Thread.sleep(1000)
    println("The user is notified")
  }

  def weatherImperative(eventId: Int): Unit = {
    val evt = getEvent(eventId)  // Will block
    val weather = getWeather(evt.time, evt.location)  // Will block
    if (weather == "bad") notifyUser() // Will block
  }

  def weatherImperativeThreaded(eventId: Int): Unit = {
    // Utility methods
    def thread(op: => Unit): Thread =
      new Thread(new Runnable { def run(): Unit = { op }})

    def runThread(t: Thread): Unit = t.start()
    

    // Business logic methods
    def notifyThread(weather: String): Thread = thread {
      if (weather == "bad") notifyUser()
    }

    def weatherThread(evt: Event): Thread = thread {
      val weather = getWeather(evt.time, evt.location)
      runThread(notifyThread(weather))
    }

    val eventThread: Thread = thread {
      val evt = getEvent(eventId)
      runThread(weatherThread(evt))
    }


    // Run the app
    runThread(eventThread)  // Prints "The user is notified"
  }

  def weatherFuture(eventId: Int): Unit = {
    implicit val context = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))

    Future { getEvent(eventId) }
      .onSuccess { case evt =>
        Future { getWeather(evt.time, evt.location) }
          .onSuccess { case weather => Future { if (weather == "bad") notifyUser } }
      }
  }

  def weatherFutureFlatmap(eventId: Int): Future[Unit] = {
    implicit val context = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))

    for {
      evt     <- Future { getEvent(eventId) }
      weather <- Future { getWeather(evt.time, evt.location) }
      _       <- Future { if (weather == "bad") notifyUser() }
    } yield ()
  }

  def weatherFutureFlatmapDesugared(eventId: Int): Future[Unit] = {
    implicit val context = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))

    Future { getEvent(eventId) }
      .flatMap { evt => Future { getWeather(evt.time, evt.location) } }
      .flatMap { weather => Future { if (weather == "bad") notifyUser() } }
  }

}

import Weather._
object WeatherImperativeSync extends App { weatherImperative(0) }
object WeatherImperativeThreaded extends App { weatherImperativeThreaded(0) }
object WeatherFuture extends App { weatherFuture(0) }
object WeatherFutureFlatmap extends App { weatherFutureFlatmap(0) }
object WeatherFutureFlatmapDesugared extends App { weatherFutureFlatmapDesugared(0) }
