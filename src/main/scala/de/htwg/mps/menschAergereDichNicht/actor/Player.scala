package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.{Actor, Props}
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.model.Color

class Player(color: Color.Value) extends Actor {
  val log = Logging(context.system, this)
  val peg1 = context.actorOf(Props[Peg], "Peg1")
  val peg2 = context.actorOf(Props[Peg], "Peg2")
  val peg3 = context.actorOf(Props[Peg], "Peg3")
  val peg4 = context.actorOf(Props[Peg], "Peg4")
  override def receive: Receive = {
    case _ =>
      println("Player with color {} received message", color)
  }
}
