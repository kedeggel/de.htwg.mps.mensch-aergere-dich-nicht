package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.{Actor, ActorPath, ActorSelection, FSM}
import akka.event.Logging
import scala.io.StdIn.readLine


// Messages that start with Request are only handled by the view with name ViewMain
class Tui extends Actor {
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case RequestHumanCount(min, max) =>
      if(self.path.name == "ViewMain") {
        println( "Please insert number of Human Players [" + min + "-" + max +"]" )
        val humans = scala.io.StdIn.readInt()
        sender ! HumanCount(humans)
      }

    case ShowBoard =>
      println("Placeholder for ShowBoard")

    case RequestRollDice(player) =>
      if(self.path.name == "ViewMain") {
        println("Turn for " + player + " press 'd' to roll the dice")
        scala.io.StdIn.readLine() match {
          case "d" =>
            sender ! Rolled(3)
          case _ =>
            println("Invalid input try again")
            self ! RequestRollDice(player)
        }
      }
  }
}
