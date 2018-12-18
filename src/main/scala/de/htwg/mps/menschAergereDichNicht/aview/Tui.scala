package de.htwg.mps.menschAergereDichNicht.aview
import de.htwg.mps.menschAergereDichNicht.model._

class Tui {
  def preInput(game: Option[Game]): Option[Game] = {
    if (game.isDefined) {
      val color = game.get.turn
      var text = Board.toString(game.get.pegs)
      println(text + "\n")
      println("It is players " + color + " turn!\n" +
        "Press d to roll the dice")
      game
    } else {
      println("Start new game...\n" +
        "Please enter the number of players and press enter: 2-4")
      game
    }
  }

  def processInputLine(input: String, game: Option[Game]): Option[Game] = {
    if (game.isDefined) {
      input match {
        case "q" => game
        case "d" => println("Rolled dice...")
        case "n" => game
        // TODO: case: move peg
      }
      game
    } else {
      input match {
        case "2" => return Some(GameCreator.createGame(2))
        case "3" => return Some(GameCreator.createGame(3))
        case "4" => return Some(GameCreator.createGame(4))
        case input => println("Input " + input + " is invalid...")
      }
      return None
    }

  }
}
