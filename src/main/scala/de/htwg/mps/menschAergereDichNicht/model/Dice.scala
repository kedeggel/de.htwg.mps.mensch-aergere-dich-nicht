package de.htwg.mps.menschAergereDichNicht.model

import scala.util.Random

object Dice {
  private val random = Random

  def roll(): Int = {
    random.nextInt(6) + 1
  }
}
