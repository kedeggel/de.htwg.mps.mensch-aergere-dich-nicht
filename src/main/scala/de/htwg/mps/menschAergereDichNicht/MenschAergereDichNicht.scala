package de.htwg.mps.menschAergereDichNicht

import akka.actor._
import com.google.inject.Guice
import de.htwg.mps.menschAergereDichNicht.actor.{Game, GuiActor, NewGame, Tui}
import de.htwg.mps.menschAergereDichNicht.model.DiceInterface

object MenschAergereDichNicht extends App{
  val injector = Guice.createInjector(new MenschAergereDichNichtModule)
  val dice = injector.getInstance(classOf[DiceInterface])
  val system = ActorSystem()
  val game: ActorRef = system.actorOf(Props(classOf[Game], dice), "GameController")
  system.actorOf(Props[Tui], "ViewMain")
  system.actorOf(Props[GuiActor], "View")
  game ! NewGame
}
