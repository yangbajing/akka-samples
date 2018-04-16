package sample.remote.benchmark

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Timers}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

object Receiver {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Sys", ConfigFactory.load("remotelookup"))
    system.actorOf(Props[Receiver], "rcv")
  }
}

class Receiver extends Actor with ActorLogging with Timers {
  import Sender._

  timers.startPeriodicTimer("tick", "tick", 5.seconds)
@volatile  var size = 0L

  override def postStop(): Unit = {
    timers.cancelAll()
  }

  def receive = {
    case m: Echo  => sender() ! m
      log.info("received msg: " + m)
    case Shutdown => context.system.terminate()
    case "tick" => log.info(s"tick, received size: $size")
    case _        => size += 1
  }
}

