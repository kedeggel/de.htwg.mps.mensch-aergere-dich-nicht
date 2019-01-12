package de.htwg.mps.menschAergereDichNicht

import akka.actor._
import de.htwg.mps.menschAergereDichNicht.actor.{Game, GuiActor, NewGame, Tui}

object MenschAergereDichNicht extends App{
  val system = ActorSystem()
  val game: ActorRef = system.actorOf(Props(classOf[Game]), "GameController")
  system.actorOf(Props[Tui], "ViewMain")
  system.actorOf(Props[GuiActor], "View")
  game ! NewGame
}
