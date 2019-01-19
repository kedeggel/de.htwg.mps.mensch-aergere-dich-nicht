package de.htwg.mps.menschAergereDichNicht.model

import org.scalatest.{Matchers, WordSpec}

class FieldsSpec extends WordSpec with Matchers {
//  "A field" should {
//    val field = NormalField(1)
//    "be not occupied without a peg on it" in {
//      field.isOccupied should be(false)
//    }
//
//    "be occupied with a peg on it" in {
//      field.peg = Some(Peg(Color.yellow, field))
//      field.isOccupied should be(true)
//    }
//  }

  "A normal field" should {
    val field = NormalField(1)
    "be represented with \"o\" if empty" in {
      val peg = None
      field.toString(peg) should be("o")
    }
    "be represented with peg\'s color if occupied" in {
      val peg = Some(Peg(Color.Yellow, None))
      field.toString(peg) should be("y")
    }
  }

  "A start field" should {
    val field = StartField(0, Color.Yellow)
    "be represented with \"s\" if empty" in {
      val peg = None
      field.toString(peg) should be("s")
    }
    "be represented with peg\'s color if occupied" in {
      val peg = Some(Peg(Color.Yellow, None))
      field.toString(peg) should be("y")
    }
  }

  "An out field" should {
    val field = OutField(0, Color.Yellow)
    "be represented with \"o\" if empty" in {
      val peg = None
      field.toString(peg) should be("o")
    }
    "be represented with peg\'s color if occupied" in {
      val peg = Some(Peg(Color.Yellow, None))
      field.toString(peg) should be("y")
    }
  }

  "A home field" should {
    val field = HomeField(0, Color.Yellow)
    "be represented with \"h\" if empty" in {
      val peg = None
      field.toString(peg) should be("h")
    }
    "be represented with peg\'s color if occupied" in {
      val peg = Some(Peg(Color.Yellow, None))
      field.toString(peg) should be("x")
    }
  }
}
