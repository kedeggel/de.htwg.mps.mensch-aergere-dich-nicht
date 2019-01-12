package de.htwg.mps.menschAergereDichNicht.actor

import akka.actor.{Actor, ActorPath, ActorSelection, FSM, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask
import de.htwg.mps.menschAergereDichNicht.model.{Board, Color}
import de.htwg.mps.menschAergereDichNicht.model

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await

// starts actor system game in main
final case object NewGame

// from/to Game
final case object ConstructGame
final case object PrepareNextTurn
final case object EndGame

// to TUI
final case class RequestHumanCount(min: Int, max: Int)
final case class RequestRollDice(player: String)
final case class RequestMovePeg(player: String, options: Array[Boolean])
final case class ShowBoard(pegs: Array[Array[model.Peg]])
final case class ShowBoardWithOptions(pegs: Array[Array[model.Peg]], options:Array[Option[model.Peg]])
final case class ShowWinScreen(winner: Array[String])
final case class Handler(game: Game)

// from TUI
final case class HumanCount(count: Int)
final case class Rolled(value: Int)
final case class ExecuteMove(move: Option[Int])
final case object ShowedWinScreen
final case object QuitGame

// to Player
final case object RequestPegsOfPlayer
final case object Finished
// from Player
final case class PegsOfPlayer(pegs: Array[model.Peg])
final case object PlayerFinished

// to Peg
final case class TryMoveModel(steps: Int)
final case class MoveIt(steps: Int)

sealed trait State
// try to initialize the game, by getting necessary parameters from the player
case object New extends State
// show current state of game, and ask to roll the dice
case object RollDice extends State
// check which moves are possible with the rolled number, let user select one
case object SelectMove extends State
// game finished, show winning screen..., exit game or allow to create new one
case object Finish extends State

sealed trait Data
case object UninitializedGameData extends Data
case class ConstructingGame(humans: Option[Int]) extends Data
case class GameData(current_player: Int, player_count: Int, roll: Option[Int], winner_list: Array[String]) extends Data

class Game extends Actor with FSM[State, Data]{
  startWith(New, UninitializedGameData)
  // how to handle multiple views simultaneously?
  var views = context.system.actorSelection("/**/View*")
  var min_player = 2
  var max_player = 4
  var handled = false

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
      views ! Handler(this)
      // Do feature?
      views ! RequestHumanCount(min_player,max_player)
      stay

    case Event(HumanCount(humans), UninitializedGameData) =>
      handled = true
      self ! ConstructGame
      stay using ConstructingGame(Some(humans))

    case Event(ConstructGame, ConstructingGame(Some(humans))) =>
      for (human_number <- 1 to humans) {
        val color = human_number match {
          case 1 => Color.Blue
          case 2 => Color.Green
          case 3 => Color.Yellow
          case _ => Color.Red
        }
        context.actorOf(Props(classOf[Player], color), "Player" + human_number)
      }

      val data = GameData(1, humans, None, Array())
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
    case Event(RequestRollDice, GameData(current_player, player_count, _, _)) =>
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
    case Event(Rolled(value), GameData(current_player, player_count, roll, winner)) =>
      implicit val timeout = Timeout(1 seconds)

      // test move options relative to peg
      val future = context.actorSelection("Player" + current_player) ? TryMove(value)

      views ! Rolled(value)

      val pegs = get_pegs_of_players(player_count)
      val model_pegs = get_pegs_of_player("Player" + current_player)

      val movable = Await.result(future, timeout.duration).asInstanceOf[Array[Boolean]]

      // verifiy move options on board level
      var updated_movable = new ListBuffer[Boolean]

      for((can_move, i) <- movable.zipWithIndex) {
        var allowd_by_game = true
        if(can_move) {
          // Pegs start at 1
          val peg_to_move = context.actorSelection("Player" + current_player + "/Peg" + (i+1))
          val color = get_color_of_player("Player" + current_player)
          val future = peg_to_move ? TryMoveModel(value)
          val pegs = get_pegs_of_player("Player" + current_player)

          val target_position = Await.result(future, timeout.duration).asInstanceOf[model.Peg]

          for (peg <- pegs) {
            // check if we would kick out our own peg
            if(peg.field_id == target_position.field_id) {
              allowd_by_game = false
            }
            // TODO: check if we rolled 6, start field is empty and we have a peg out
          }
        }
        updated_movable += can_move && allowd_by_game
      }

      val buf = new ListBuffer[Option[model.Peg]]

      for ((moves, peg) <- updated_movable zip model_pegs) {
        if (moves) {
          buf += Some(peg)
        } else {
          buf += None
        }
      }

      views ! ShowBoardWithOptions(pegs, buf.toArray)
      views ! RequestMovePeg("Player" + current_player, updated_movable.toArray)
      stay using GameData(current_player, player_count, Some(value), winner)

    case Event(ExecuteMove(move), GameData(current_player, player_count, roll, _)) =>
      implicit val timeout = Timeout(1 seconds)

      (move, roll) match {
        case (Some(move), Some(roll)) =>
          val peg_to_move = context.actorSelection("Player" + current_player + "/Peg" + move)
          peg_to_move ! MoveIt(roll)
        case _ =>
          self ! PrepareNextTurn
      }

      stay

    case Event(PlayerFinished, GameData(current_player, player_count, roll, winner_list)) =>
      var arr = winner_list
      arr :+= sender.path.name
      stay using GameData(current_player, player_count, roll, arr)

    case Event(PrepareNextTurn, GameData(current_player, player_count, roll, winner)) =>
      implicit val timeout = Timeout(1 seconds)

      var next_player = current_player + 1
      var determined_next_player = false
      var finished = false

      while (!determined_next_player) {
        if (player_count+1 == next_player) {
          next_player = 1
        }
        val future = context.actorSelection("Player"+next_player) ? Finished
        determined_next_player = !Await.result(future, timeout.duration).asInstanceOf[Boolean]
        if (next_player == current_player && !determined_next_player) {
          //no next player
          finished = true
          determined_next_player = true
        }

        if (!determined_next_player) {
          next_player += 1
        }
      }

      if (finished || (player_count != 1 && winner.length == player_count-1)) {
        var new_winner = winner
        if (!finished) {
          new_winner :+= "Player" + next_player
        }
        views ! ShowWinScreen(new_winner)
        goto(Finish) using UninitializedGameData
      } else {
        self ! RequestRollDice
        goto(RollDice) using GameData(next_player, player_count, None, winner)
      }
  }

  when(Finish) {
    case Event(ShowedWinScreen, _) =>
      views ! EndGame
      stay
    case Event(QuitGame, _) =>
      context.system.terminate()
      stay
    case _ =>
      stay
  }

  whenUnhandled {
    case Event(QuitGame, s) =>
      log.warning("Default handler: Quiting Game")
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
    val future = context.actorSelection(player) ? RequestColorOfPlayer
    Await.result(future, timeout.duration).asInstanceOf[model.Color.Value]
  }

  def get_pegs_of_player(player: String): Array[model.Peg] = {
    implicit val timeout = Timeout(1 seconds)
    val future_pegs = context.actorSelection(player) ? RequestPegsOfPlayer

    Await.result(future_pegs, timeout.duration).asInstanceOf[Array[model.Peg]]
  }

  def get_pegs_of_players(player_count: Int): Array[Array[model.Peg]] = {
    var pegs: ListBuffer[Array[model.Peg]] = new ListBuffer()
    for ( i <- 1 to player_count) {
      val player_pegs = get_pegs_of_player("Player" + i)
      pegs += player_pegs
    }
    pegs.toArray
  }

  initialize()
}
