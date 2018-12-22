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

    case RequestMovePeg(player, options) =>
      if(self.path.name == "ViewMain") {
        // TODO: ask for user input to select peg to move
        println(player + "'s turn please select what to do")

        var option_string = ""
        var can_choose = false
        for ((movable, i) <- options.zipWithIndex) {
          if (movable) {
            can_choose = true
            option_string +=  (i+1) + " "
          }
        }

        if (can_choose) {
          println("Please select one of the following numbers: " + option_string)
          val selected_peg = scala.io.StdIn.readInt()
          if(1 <= selected_peg && selected_peg < 5 && options(selected_peg-1) ) {
            sender ! ExecuteMove(Some(selected_peg))
          } else {
            // TODO: sender ! is wrong if this gets executed, handle without actors
            println("Invalid input try again")
            self ! RequestMovePeg(player, options)
          }
        } else {
          println("Can't move any peg, press enter to end turn")
          scala.io.StdIn.readLine()
          sender ! ExecuteMove(None)
        }



      }
  }
}
