package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.Actor
import akka.event.Logging
import akka.util.Timeout
import de.htwg.mps.menschAergereDichNicht.model
import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.{Await, Future, Promise}

class Peg(color: model.Color.Value) extends Actor {
  val log = Logging(context.system, this)
  // position is relative to start field of peg
  var position: Option[Int] = None
  override def receive: Receive = {
    case ReqeuestModelOfPeg =>
      sender ! model.Peg(color, position)
    case TryMove(steps) =>
      // can move when new_position is different to old one
        sender ! (position != new_position(position, steps))
    case TryMoveModel(steps) =>
      sender ! model.Peg(color, new_position(position, steps))
    case MoveIt(steps) =>
      implicit val timeout = Timeout(1 seconds)
      val new_pos = new_position(position, steps)
      // can never be None
      context.actorSelection("../../**/Peg*") ! KickOut(model.Peg(color, new_pos))
      (position, new_pos) match {
        case (Some(old_pos), Some(new_pos)) =>
          // moved from board into home
          if (old_pos <= 39 && 39 < new_pos) {
            context.parent ! ReportHome
          }
        case _ =>
      }
      position = new_pos
      sender ! PrepareNextTurn
    case KickOut(other) =>
      if (sender.path.parent == self.path.parent) {
      } else {
        val me = model.Peg(color, position)
        if (me.absolute_position() == other.absolute_position()) {
          position = None
        }
      }

    case _ =>
      log.warning("Peg with path {} received message", self.path)


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
        if (updated < 44) {
          Some(updated)
        } else {
          Some(old)
        }
    }
  }
}
