package de.htwg.mps.menschAergereDichNicht

import de.htwg.mps.menschAergereDichNicht.aview.Tui
import de.htwg.mps.menschAergereDichNicht.model.Board

import scala.io.StdIn.readLine

object MenschAergereDichNicht {
  var board = Board()
  val tui = new Tui

  def main(args: Array[String]): Unit = {
    var input: String = ""

    do {
      println(board.toString)
      input = readLine()
      board = tui.processInputLine(input, board)
      println("")
    } while (input != "q")
    println("Quit...")
  }
}
