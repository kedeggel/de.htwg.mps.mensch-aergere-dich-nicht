package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.Actor
import akka.event.Logging
import akka.util.Timeout
import de.htwg.mps.menschAergereDichNicht.model
import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.{Await, Future, Promise}

class Peg extends Actor {
  val log = Logging(context.system, this)
  val position: Option[Int] = None
  override def receive: Receive = {
    case ReqeuestModelOfPeg =>
      implicit val timeout = Timeout(1 seconds)
      val future = context.parent ? RequestColorOfPlayer
      val color = Await.result(future, timeout.duration).asInstanceOf[model.Color.Value]
      sender ! model.Peg(color, position)
    case _ =>
      println("Player with color received message")
  }
}
