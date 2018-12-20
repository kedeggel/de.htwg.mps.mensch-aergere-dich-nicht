package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.{Actor, ActorPath, ActorSelection, FSM, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask
import de.htwg.mps.menschAergereDichNicht.model.{Board, Color}
import de.htwg.mps.menschAergereDichNicht.model

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await

final case object NewGame

// Human Player Count
final case class RequestHumanCount(min: Int, max: Int)
final case class HumanCount(count: Int)

final case object ConstructGame
final case class RequestRollDice(player: String)
final case class Rolled(value: Int)
final case class RequestMovePeg(player: String, value: Int)

final case class ShowBoard(pegs: Array[Array[model.Peg]])
final case class ShowBoardWithOptions(pegs: Array[Array[model.Peg]], options:Array[Option[model.Peg]])


final case object ResetGame
final case object QuitGame

sealed trait State
// try to initialize the game, by getting necessary parameters from the player
case object New extends State
// show current state of game, and ask to roll the dice
case object RollDice extends State
// check which moves are possible with the rolled number, let user select one
case object SelectMove extends State
// execute the move of the selected peg/skip if none could be moved
case object Move extends State
// game finished, show winning screen..., exit game or allow to create new one
case object Finish extends State

sealed trait Data
case object UninitializedGameData extends Data
case class ConstructingGame(humans: Option[Int]) extends Data
case class GameData(current_player: Int, player_count: Int) extends Data

class Game extends Actor with FSM[State, Data]{
  startWith(New, UninitializedGameData)
  // how to handle multiple views simultaneously?
  var views = context.system.actorSelection("/**/View*")
  var min_player = 2
  var max_player = 4

  /*
  @startuml
    NewGame -> View : msg: RequestHumanCount
    View -> NewGame : msg: HumanCount
    NewGame -> NewGame : msg: ConstructGame
    NewGame -> NewGame : changeState(RollDice)
    NewGame -> RollDice : msg: RollDiceMsg
  @enduml
   */
  when(New) {
    case Event(NewGame, UninitializedGameData) =>
      log.info("New Game")
      // Do feature?
      views ! RequestHumanCount(1,max_player)
      stay

    case Event(HumanCount(humans), UninitializedGameData) =>
      self ! ConstructGame
      stay using ConstructingGame(Some(humans))

    case Event(ConstructGame, ConstructingGame(Some(humans))) =>
      var i = 0
      for (human_number <- 0 to humans) {
        val color = i match {
          case 0 => Color.Blue
          case 1 => Color.Green
          case 2 => Color.Yellow
          case _ => Color.Red
        }
        context.system.actorOf(Props(classOf[Player], color), "Player" + i)
        i += 1
      }

      val data = GameData(1, humans)
      log.info("Created new game {}", data)
      self ! RequestRollDice
      goto(RollDice) using data
  }

  /*
  @startuml
  RollDice -> View : msg: ShowBoard
  RollDice -> View : msg: RollDice
  RollDice -> RollDice : changeState(SelectMove)
  View -> SelectMove : msg Rolled
  @enduml
   */
  when(RollDice) {
    case Event(RequestRollDice, GameData(current_player, player_count)) =>
      views ! ShowBoard(get_pegs_of_players(player_count))
      views ! RequestRollDice("Player" + current_player)
      goto(SelectMove)
  }

  /*
  @startuml
  SelectMove -> View : msg: ShowBoardWithOptions
  SelectMove -> View : msg: SelectMove
  SelectMove -> SelectMove : DoMove()
  SelectMove -> SelectMove : changeState(RollDice)
  @enduml
   */
  when(SelectMove) {
    case Event(Rolled(value), GameData(current_player, player_count)) =>
      implicit val timeout = Timeout(1 seconds)
      val future = context.system.actorSelection("**/Player" + current_player) ? TryMove(value)

      views ! Rolled(value)

      val pegs = get_pegs_of_players(player_count)
      val model_pegs = get_pegs_of_player("Player" + current_player)

      val movable = Await.result(future, timeout.duration).asInstanceOf[Array[Boolean]]

      val buf = new ListBuffer[Option[model.Peg]]

      for ((moves, peg) <- movable zip model_pegs) {
        if (moves) {
          buf += Some(peg)
        } else {
          buf += None
        }
      }


      views ! ShowBoardWithOptions(pegs, buf.toArray)
      views ! RequestMovePeg("Player" + current_player, value)
      self ! Move
      stay
    case Event(Move, GameData(current_player, player_count)) =>

      // TODO: player selected move, execute it

      val pegs = get_pegs_of_player("Player" + current_player)

      println("Current player peg 1 is on field: " + pegs(0).field_id)



      // TODO: check if next player is finished
      var next_player = current_player + 1
      if (player_count < next_player) {
        next_player = 1
      }
      // TODO: check game status to switch to Finish instead of RollDice
      println("Placeholder for Move")
      self ! RequestRollDice
      goto(RollDice) using GameData(next_player, player_count)
  }

  when(Finish) {
    case _ =>
      stay
  }

  whenUnhandled {
    case Event(QuitGame, s) =>
      log.info("Default handler: Quiting Game")
      context.system.terminate()
      stay
    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  def get_current_player(player: Int): String = {
    "Player" + player
  }

  def get_color_of_player(player: String): model.Color.Value = {
    implicit val timeout = Timeout(1 seconds)
    val future = context.system.actorSelection(player) ? RequestColorOfPlayer
    Await.result(future, timeout.duration).asInstanceOf[model.Color.Value]
  }

  def get_pegs_of_player(player: String): Array[model.Peg] = {
    implicit val timeout = Timeout(1 seconds)
    val future_pegs = context.system.actorSelection("**/" + player) ? RequestPegsOfPlayer

    Await.result(future_pegs, timeout.duration).asInstanceOf[Array[model.Peg]]
  }

  def get_pegs_of_players(player_count: Int): Array[Array[model.Peg]] = {
    var pegs: ListBuffer[Array[model.Peg]] = new ListBuffer()
    for ( i <- 0 until player_count) {
      val player_pegs = get_pegs_of_player("Player" + i)
      pegs += player_pegs
    }
    pegs.toArray
  }

  initialize()
}
