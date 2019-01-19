package de.htwg.mps.menschAergereDichNicht.model

import de.htwg.mps.menschAergereDichNicht.model.diceBaseImpl.Dice
import org.scalatest.{Matchers, WordSpec}

class DiceSpec extends WordSpec with Matchers {
  "A dice" should {
    val dice = new Dice

    "not be higher than 6" in {
      val scores = List.fill(100)(dice.roll())
      scores.count(_ > 6) should be(0)
    }

    "not be less that 1" in {
      val scores = List.fill(100)(dice.roll())
      scores.count(_ < 0) should be(0)
    }
  }
}
