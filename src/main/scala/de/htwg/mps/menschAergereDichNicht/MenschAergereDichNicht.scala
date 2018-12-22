package de.htwg.mps.menschAergereDichNicht

//import de.htwg.mps.menschAergereDichNicht.model.{Board, Color, Game, Peg}

import scala.io.StdIn.readLine
import akka.actor._
import de.htwg.mps.menschAergereDichNicht.actor.NewGame
import de.htwg.mps.menschAergereDichNicht.actor.{Game, Tui}

object MenschAergereDichNicht extends App{
  val system = ActorSystem()
  val game: ActorRef = system.actorOf(Props(classOf[Game]), "GameController")
  system.actorOf(Props[Tui], "ViewMain")
//  system.actorOf(Props(classOf[Tui], "test2"), "View")
  game ! NewGame
}
