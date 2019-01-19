package de.htwg.mps.menschAergereDichNicht.model.diceBaseImpl
import com.google.inject.Singleton
import de.htwg.mps.menschAergereDichNicht.model.DiceInterface

import scala.util.Random

class Dice extends DiceInterface {
  private val random = Random

  override def roll(): Int = {
    random.nextInt(6) + 1
  }
}
