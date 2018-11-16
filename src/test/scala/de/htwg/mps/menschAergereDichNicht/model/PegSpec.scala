package de.htwg.mps.menschAergereDichNicht.model

import org.scalatest.{Matchers, WordSpec}

class PegSpec extends WordSpec with Matchers {
  "A peg that is out" should {
    val peg = Peg(Color.Yellow, None)
    "stay out if it is moved with less then 6" in {
      peg.move(5) should be(peg)
    }
    "be placed on the field when moved with 6" in {
      peg.move(6) should be(Peg(peg.color, Some(0)))
    }
  }

  "A peg on the field" should {
    val peg = Peg(Color.Yellow, Some(0))
    "move 3 fields ahead when moved by 3" in {
      peg.move(3) should be(Peg(peg.color, Some(3)))
    }
    "move 6 fields ahead when moved by 6" in {
      peg.move(6) should be(Peg(peg.color, Some(6)))
    }
  }
}
