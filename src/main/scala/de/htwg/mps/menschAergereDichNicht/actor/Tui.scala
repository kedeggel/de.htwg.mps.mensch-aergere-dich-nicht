package de.htwg.mps.menschAergereDichNicht.actor

import java.io.BufferedReader

import akka.actor.Actor
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.model.Board

import scala.util.{Success, Try}

// Messages that start with Request are only handled by the view with name ViewMain
class Tui extends Actor {
  val log = Logging(context.system, this)
  val input = new BufferedReader(Console.in)
  var handler: Option[Game] = None

  override def receive: Receive = {
    case Handler(h) => this.handler = Some(h)

    case RequestHumanCount(min, max) =>
      if (self.path.name == "ViewMain") {
        println(
          "Please insert number of Human Players [" + min + "-" + max + "]"
        )
        while (!this.handler.get.handled) {
          if (input.ready()) {
            val line = input.readLine
            Try(line.toInt) match {
              case Success(humans) if min <= humans && humans <= max =>
                sender ! HumanCount(humans)
              case _ =>
                println(
                  "Please insert number of Human Players [" + min + "-" + max + "]")
            }
          }
        }
      }

    case ShowBoard(pegs) =>
      println(Board.toString(pegs))

    case RequestRollDice(dice, player) =>
      if (self.path.name == "ViewMain") {
        println("Turn for " + player + " press 'd' to roll the dice")
        while (!this.handler.get.handled) {
          if (input.ready()) {
            val line = input.readLine
            var cheating = false

            try {
              val roll = line.toInt
              sender ! Rolled(roll)
              cheating = true
            } catch {
              case e: Exception =>
            }

            if (!cheating) {
              line match {
                case "d" =>
                  sender ! Rolled(dice.roll())
                case "q" =>
                  sender ! QuitGame
                case "n" =>
                  sender ! NewGame
                case _ =>
                  println("Invalid input try again...")
              }
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
        println((i + 1) + ". place is " + winner)
      }
      sender ! ShowedWinScreen

    case RequestMovePeg(player, options) =>
      if (self.path.name == "ViewMain") {
        println(player + "'s turn please select what to do")

        var option_string = ""
        var can_choose = false
        for ((movable, i) <- options.zipWithIndex) {
          if (movable) {
            can_choose = true
            option_string += (i + 1) + " "
          }
        }
        if (can_choose) {
          println(
            "Please select one of the following numbers: " + option_string
          )
          var break = false
          while (!this.handler.get.handled && !break) {
            if (input.ready()) {

              val line = input.readLine
              Try(line.toInt) match {
                case Success(selected_peg) if 1 <= selected_peg && selected_peg < 5 && options(selected_peg - 1) =>
                  sender ! ExecuteMove(Some(selected_peg))
                  break = true
                case _ =>
                  println("Invalid input try again...")
                  println(
                    "Please select one of the following numbers: " + option_string
                  )
              }
            } else {
              Thread.sleep(10)
            }
          }
        } else {
          println("Can't move any peg. Your turn ends.")
          sender ! ExecuteMove(None)
        }
      }

    case EndGame =>
      println("Quiting Game press enter to confirm...")
      scala.io.StdIn.readLine()
      sender ! QuitGame
  }
}
