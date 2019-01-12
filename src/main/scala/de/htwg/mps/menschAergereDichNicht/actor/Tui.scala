package de.htwg.mps.menschAergereDichNicht.actor

import java.io.BufferedReader

import akka.actor.{Actor, ActorPath, ActorSelection, FSM}
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.model.{Board, Dice}

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}


// Messages that start with Request are only handled by the view with name ViewMain
class Tui extends Actor {
  val log = Logging(context.system, this)
  val input = new BufferedReader(Console.in)
  var handler: Option[Game] = None

  override def receive: Receive = {
    case Handler(handler) => this.handler = Some(handler)

    case RequestHumanCount(min, max) =>
      if(self.path.name == "ViewMain") {
        println("Please insert number of Human Players [" + min + "-" + max +"]")
        while (!this.handler.get.handled) {
          if (input.ready()) {
            val line = input.readLine
            Try(line.toInt) match {
            case Success(humans) if min <= humans && humans <= max => {
              sender ! HumanCount(humans)
            }
            case _ => {
              println( "Please insert number of Human Players [" + min + "-" + max +"]")
            }
          }
        } else {
            Thread.sleep(200)
        }
      }
    }

    case ShowBoard(pegs) =>
      println(Board.toString(pegs))

    case RequestRollDice(player) =>
      if(self.path.name == "ViewMain") {
        var valid_input = false

        while (!valid_input) {
          println("Turn for " + player + " press 'd' to roll the dice")
          val input = scala.io.StdIn.readLine()

          try {
            val roll = input.toInt
            sender ! Rolled(roll)
            valid_input = true
          } catch {
            case e: Exception =>
          }

          if (!valid_input) {
            input match {
              case "d" =>
                sender ! Rolled(Dice.role())
                valid_input = true

              case "q" =>
                sender ! QuitGame
                valid_input = true
              case _ =>
                println("Invalid input try again...")
            }
          }
        }
      }

    case Rolled(value) =>
      println("Rolled " + value + "!")

    case ShowBoardWithOptions(pegs, options) =>
      println(Board.toStringMove(pegs, options))

    case ShowWinScreen(winner) =>
      for ((winner, i) <- winner.zipWithIndex) {
        println((i+1) + ". place is " + winner)
      }
      sender ! ShowedWinScreen

    case RequestMovePeg(player, options) =>
      if(self.path.name == "ViewMain") {
        println(player + "'s turn please select what to do")

        var option_string = ""
        var can_choose = false
        for ((movable, i) <- options.zipWithIndex) {
          if (movable) {
            can_choose = true
            option_string +=  (i+1) + " "
          }
        }

        var valid_input = false

        while (!valid_input) {
          if (can_choose) {
            try {
              println("Please select one of the following numbers: " + option_string)
              val selected_peg = scala.io.StdIn.readInt()
              if (1 <= selected_peg && selected_peg < 5 && options(selected_peg - 1)) {
                sender ! ExecuteMove(Some(selected_peg))
                valid_input = true
              } else {
                println("Invalid input try again...")
              }
            } catch {
              case e: NumberFormatException =>
                println("Invalid input try again...")
            }
          } else {
            println("Can't move any peg, press enter to end turn")
            scala.io.StdIn.readLine()
            sender ! ExecuteMove(None)
            valid_input = true
          }
        }
      }

    case EndGame =>
      println("Quiting Game press enter to confirm...")
      scala.io.StdIn.readLine()
      sender ! QuitGame
  }
}
