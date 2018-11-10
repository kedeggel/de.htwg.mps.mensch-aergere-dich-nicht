package de.htwg.mps.menschAergereDichNicht.aview
import de.htwg.mps.menschAergereDichNicht.model.Board

class Tui {
  def processInputLine(input: String, board: Board): Board = {
    input match {
      case "q" => board
      case "n" => Board()
      // TODO: case: move peg
    }
  }
}
