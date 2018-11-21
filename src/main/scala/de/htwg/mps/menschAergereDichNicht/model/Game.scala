package de.htwg.mps.menschAergereDichNicht.model



case class Game(board: Board, pegs: Array[Array[Peg]], turn: Color.Value) {
  // returns all the pegs that can be possible moved if moved by dice_roll
  def move_options(dice_roll: Int): Array[Peg] = {
    var result: Array[Peg] = Array()
    for ( color <- pegs ) {
      if (color(0).color == turn) {
        var pegs = Array()
        // check move for every peg of player with color turn
        for (peg <- color) {
          val turned_peg = peg.move(dice_roll)
          // assume that move is possible and check for all cases in which case the peg can't be moved
          var move_possible = true
          //TODO: check for cases when moving is not possible

          if (move_possible) {
            result = result :+ peg
          }
        }
      }
    }
    result
  }

  // moves the peg by dice_roll
  def move(dice_roll: Int, peg: Peg): Game = {
    // this seems wrong but is need so that we don't change the old game
    val new_pegs = pegs.transpose.transpose
    val new_peg = peg.move(dice_roll)
    for (i <- 0 until new_pegs.length) {
      for (j <- 0 until new_pegs(i).length) {
        // update peg position
        if (new_pegs(i)(j) == peg) {
          new_pegs(i)(j) = new_peg
        }
        // check if other peg needs to be kicked out
        if (new_pegs(i)(j).absolute_position() == new_peg.absolute_position()) {
          new_pegs(i)(j) = Peg(new_pegs(i)(j).color, None)
        }
      }
    }

    // TODO: change turn

    Game(board, new_pegs, turn)
  }
}
