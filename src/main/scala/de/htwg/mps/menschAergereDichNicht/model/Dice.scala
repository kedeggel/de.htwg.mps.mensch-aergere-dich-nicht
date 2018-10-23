package de.htwg.mps.menschAergereDichNicht.model

import scala.util.Random

object Dice {
  val random = Random

  def role(): Int = {
    random.nextInt(6) + 1
  }
}
