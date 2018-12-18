package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.Actor
import akka.event.Logging


class Peg extends Actor {
  val log = Logging(context.system, this)
  val position: Option[Int] = None
  override def receive: Receive = {
    case _ =>
      println("Player with color {} received message")
  }
}
