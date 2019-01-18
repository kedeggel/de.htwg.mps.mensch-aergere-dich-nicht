package de.htwg.mps.menschAergereDichNicht.actor
import akka.actor.Actor
import akka.event.Logging
import de.htwg.mps.menschAergereDichNicht.aview.Gui

class GuiActor extends Actor {
  var handler: Option[Game] = None
  private var lastSender = sender

  def newGame = {
    sender ! NewGame
  }

  val log = Logging(context.system, this)
  val gui = new Gui(this)

  override def receive: Receive = {
    case Handler(h) =>
      this.handler = Some(h)

    case RequestHumanCount(min, max) =>
      val nPlayers = gui.numberOfPlayer(min, max)
      if (nPlayers != -1) sender ! HumanCount(nPlayers)
    case ShowBoard(pegs) =>
      gui.updatePegs(pegs)
    case RequestRollDice(player) =>
      gui.setCurrentPlayer(player)
      val diceResult = gui.rollDice
      if (diceResult != -1) sender ! Rolled(diceResult)
    case Rolled(value) =>
      gui.updateDice(value)
    case ShowBoardWithOptions(pegs, options) =>
      gui.highlight(options)
    case ShowWinScreen(winner) =>
      gui.showWinScreen(winner)
    case RequestMovePeg(player, options) =>
      lastSender = sender
      gui makePegsMovable ((x: Option[Int]) => send(x))
    case EndGame =>
      gui.showEndGame()
  }

  def send(x: Option[Int]): Unit = lastSender ! ExecuteMove(x)
}
