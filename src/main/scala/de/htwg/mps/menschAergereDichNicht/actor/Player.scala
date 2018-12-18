package de.htwg.mps.menschAergereDichNicht.actor

import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import akka.actor.{Actor, Props}
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.model

import scala.concurrent.{Await, Future}

final case object RequestColorOfPlayer
final case class ColorOfPlayer(color: model.Color.Value)

final case object RequestPegsOfPlayer
final case class PegsOfPlayer(pegs: Array[model.Peg])

final case object ReqeuestModelOfPeg

class Player(color: model.Color.Value) extends Actor {
  import context.dispatcher
  val log = Logging(context.system, this)
  val pegs = Array(
    context.actorOf(Props[Peg], "Peg1"),
    context.actorOf(Props[Peg], "Peg2"),
    context.actorOf(Props[Peg], "Peg3"),
    context.actorOf(Props[Peg], "Peg4"),
  )
  override def receive: Receive = {
    case RequestColorOfPlayer =>
      sender ! color

    case RequestPegsOfPlayer =>
      implicit val timeout = Timeout(1 seconds)
      val future_peg1 = pegs(0) ? ReqeuestModelOfPeg
      val future_peg2 = pegs(1) ? ReqeuestModelOfPeg
      val future_peg3 = pegs(2) ? ReqeuestModelOfPeg
      val future_peg4 = pegs(3) ? ReqeuestModelOfPeg

      val peg1 = Await.result(future_peg1, timeout.duration).asInstanceOf[model.Peg]
      val peg2 = Await.result(future_peg2, timeout.duration).asInstanceOf[model.Peg]
      val peg3 = Await.result(future_peg3, timeout.duration).asInstanceOf[model.Peg]
      val peg4 = Await.result(future_peg4, timeout.duration).asInstanceOf[model.Peg]

      val peggis = Array(peg1, peg2, peg3, peg4)
      sender ! PegsOfPlayer(peggis)

    case _ =>
      println("Player with color {} received message", color)
  }
}
