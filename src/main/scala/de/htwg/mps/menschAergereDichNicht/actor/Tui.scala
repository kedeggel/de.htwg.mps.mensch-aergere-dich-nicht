package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.{Actor, ActorPath, ActorSelection, FSM}
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.model.{Board, Dice}

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

    case ShowBoard(pegs) =>
      println(Board.toString(pegs))

    case RequestRollDice(player) =>
      if(self.path.name == "ViewMain") {
        println("Turn for " + player + " press 'd' to roll the dice")
        scala.io.StdIn.readLine() match {
          case "d" =>
            sender ! Rolled(Dice.role())
          case "q" =>
            sender ! QuitGame
          case _ =>
            println("Invalid input try again")
            self ! RequestRollDice(player)
        }
      }

    case Rolled(value) =>
      println("Rolled " + value + "!")

    case ShowBoardWithOptions(pegs, options) =>
      // TODO: somehow get pegs of current player + dice roll and show which can be moved
      println(Board.toStringMove(pegs, options))

    case RequestMovePeg(_, _) =>
      if(self.path.name == "ViewMain") {
        // TODO: ask for user input to select peg to move
        println("Placeholder for RequestMovePeg")
      }
  }
}
