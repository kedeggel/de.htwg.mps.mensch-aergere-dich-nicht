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
  // position is relative to start field of peg
  var position: Option[Int] = None
  override def receive: Receive = {
    case ReqeuestModelOfPeg(color) =>
      sender ! model.Peg(color, position)
    case TryMove(steps) =>
      // can move when new_position is different to old one
        sender ! (position != new_position(position, steps))
    case MoveIt(steps) =>
      position = new_position(position, steps)

    case _ =>
      println("Player with color received message")


  }

  def new_position(old: Option[Int], steps: Int): Option[Int] = {
    old match {
      case None =>
        if (steps != 6) {
          None
        } else {
          Some(0)
        }
      case Some(old) =>
        //overstepped homerow
        val updated = old + steps
        if (44 < updated) {
          None
        } else {
          Some(updated)
        }
    }
  }
}
