package de.htwg.mps.menschAergereDichNicht.model.diceFakeImpl
import de.htwg.mps.menschAergereDichNicht.model.DiceInterface

class Dice(results: Array[Int]) extends DiceInterface {
  var counter = 0

  override def roll(): Int = {
    if (results.length == 0)
      6
    else {
      val result = results(counter)
      if (counter < results.length - 1) counter += 1
      else counter = 0
      result
    }
  }
}
