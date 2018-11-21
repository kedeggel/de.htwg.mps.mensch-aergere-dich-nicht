package de.htwg.mps.menschAergereDichNicht

import de.htwg.mps.menschAergereDichNicht.aview.Tui
import de.htwg.mps.menschAergereDichNicht.model.{Board, Color, Game, Peg}

import scala.io.StdIn.readLine

object MenschAergereDichNicht {
  val tui = new Tui
  var game = Game(Board(), Array(
    Array(
      Peg(Color.Blue, None),
      Peg(Color.Blue, None),
      Peg(Color.Blue, None),
      Peg(Color.Blue, None),
    ),
    Array(
      Peg(Color.Yellow, None),
      Peg(Color.Yellow, None),
      Peg(Color.Yellow, None),
      Peg(Color.Yellow, None),
    ),
    Array(
      Peg(Color.Green, None),
      Peg(Color.Green, None),
      Peg(Color.Green, None),
      Peg(Color.Green, None),
    ),
    Array(
      Peg(Color.Red, None),
      Peg(Color.Red, None),
      Peg(Color.Red, None),
      Peg(Color.Red, None),
    ),
  ), Color.Blue)


  def main(args: Array[String]): Unit = {
    var input: String = ""

    var pegs = game.move_options(6)
    println("Move peg: " + pegs(0).field_id)
    game = game.move(6, pegs(0))
    println(game.board.toString(game.pegs, None) + "\n")

    pegs = game.move_options(6)
    println("Move peg: " + pegs(0).field_id)
    game = game.move(6, pegs(0))
    println(game.board.toString(game.pegs, None) + "\n")

    pegs = game.move_options(6)
    println("Move peg: " + pegs(0).field_id)
    game = game.move(6, pegs(0))
    println(game.board.toString(game.pegs, None) + "\n")

//    do {
//      println(board.toString)
//      input = readLine()
//      board = tui.processInputLine(input, board)
//      println("")
//    } while (input != "q")
//    println("Quit...")
  }
}
