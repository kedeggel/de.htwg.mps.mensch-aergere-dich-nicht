package de.htwg.mps.menschAergereDichNicht

import de.htwg.mps.menschAergereDichNicht.aview.Tui
import de.htwg.mps.menschAergereDichNicht.model.{Board, Color, Game, Peg}

import scala.io.StdIn.readLine

object MenschAergereDichNicht {
  val tui = new Tui
  var game: Option[Game] = None

  def main(args: Array[String]): Unit = {
    var input: String = ""

    do {
      game = tui.preInput(game)
      input = readLine()
      game = tui.processInputLine(input, game)
    } while (input != "q")
    println("Quit...")
  }
}
