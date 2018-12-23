package de.htwg.mps.menschAergereDichNicht.actor

import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import akka.actor.{Actor, Props}
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.model

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}

// to Peg
final case class KickOut(other: model.Peg)
final case class ColorOfPlayer(color: model.Color.Value)
final case object RequestModelOfPeg
final case class TryMove(steps: Int)

// from Peg
final case object RequestColorOfPlayer
final case object ReportHome

class Player(color: model.Color.Value) extends Actor {
  import context.dispatcher
  val log = Logging(context.system, this)
  val pegs = Array(
    context.actorOf(Props(classOf[Peg], color), "Peg1"),
    context.actorOf(Props(classOf[Peg], color), "Peg2"),
    context.actorOf(Props(classOf[Peg], color), "Peg3"),
    context.actorOf(Props(classOf[Peg], color), "Peg4"),
  )
  var pegs_home = 0
  override def receive: Receive = {
    case RequestColorOfPlayer =>
      sender ! color

    case RequestPegsOfPlayer =>
      implicit val timeout = Timeout(1 seconds)
      val future_peg1 = pegs(0) ? RequestModelOfPeg
      val future_peg2 = pegs(1) ? RequestModelOfPeg
      val future_peg3 = pegs(2) ? RequestModelOfPeg
      val future_peg4 = pegs(3) ? RequestModelOfPeg

      val peg1 = Await.result(future_peg1, timeout.duration).asInstanceOf[model.Peg]
      val peg2 = Await.result(future_peg2, timeout.duration).asInstanceOf[model.Peg]
      val peg3 = Await.result(future_peg3, timeout.duration).asInstanceOf[model.Peg]
      val peg4 = Await.result(future_peg4, timeout.duration).asInstanceOf[model.Peg]

      sender ! Array(peg1, peg2, peg3, peg4)

    case TryMove(steps) =>
      implicit val timeout = Timeout(1 seconds)

      val future_peg1 = pegs(0) ? TryMove(steps)
      val future_peg2 = pegs(1) ? TryMove(steps)
      val future_peg3 = pegs(2) ? TryMove(steps)
      val future_peg4 = pegs(3) ? TryMove(steps)


      val peg1 = Await.result(future_peg1, timeout.duration).asInstanceOf[Boolean]
      val peg2 = Await.result(future_peg2, timeout.duration).asInstanceOf[Boolean]
      val peg3 = Await.result(future_peg3, timeout.duration).asInstanceOf[Boolean]
      val peg4 = Await.result(future_peg4, timeout.duration).asInstanceOf[Boolean]

      sender ! Array(peg1, peg2, peg3, peg4)

    case ReportHome =>
      pegs_home += 1

    case MoveIt(_) =>
      if (pegs_home == 4) {
        sender ! PlayerFinished
      }
      sender ! PrepareNextTurn

    case Finished =>
      sender ! (pegs_home == 4)

    case _ =>
      log.warning("Player with color {} received message", color)
  }
}
