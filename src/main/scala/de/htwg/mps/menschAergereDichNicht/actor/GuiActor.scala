package de.htwg.mps.menschAergereDichNicht.actor
import akka.actor.Actor
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.aview.Gui



class GuiActor extends Actor {
  var handler: Option[Game] = None

  def newGame: Unit = {
    println("Sending new game")
    sender ! NewGame
  }

  val log = Logging(context.system, this)
  val gui = new Gui(this)

  override def receive: Receive = {
    case Handler(handler) => this.handler = Some(handler)

    case RequestHumanCount(min, max) => {
      val nPlayers = gui.numberOfPlayer(min, max)
      if (nPlayers != -1) sender ! HumanCount(nPlayers)
    }
    case ShowBoard(pegs) => {
      print("GUI: ShowBoard")
    }
    case RequestRollDice(player) => {
      println("GUI: RequestRollDice")
    }
    case Rolled(value) => {}
    case ShowBoardWithOptions(pegs, options) => {}
    case ShowWinScreen(winner) => {}
    case RequestMovePeg(player, options) => {}
    case EndGame => {}
  }
}
