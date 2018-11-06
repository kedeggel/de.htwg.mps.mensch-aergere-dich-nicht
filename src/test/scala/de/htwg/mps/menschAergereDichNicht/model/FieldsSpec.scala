package de.htwg.mps.menschAergereDichNicht.model

import org.scalatest.{Matchers, WordSpec}

class FieldsSpec extends WordSpec with Matchers {
  "A field" should {
    val field = NormalField()
    "be not occupied without a peg on it" in {
      field.isOccupied should be(false)
    }

    "be occupied with a peg on it" in {
      field.peg = Some(Peg(Color.yellow, field))
      field.isOccupied should be(true)
    }
  }

  "A normal field" should {
    val field = NormalField()
    "be represented with \"o\" if empty" in {
      field.toString should be("o")
    }
    "be represented with peg\'s color if occupied" in {
      field.peg = Some(Peg(Color.yellow, field))
      field.toString should be("a")
    }
  }

  "A start field" should {
    val field = StartField(Color.yellow)
    "be represented with \"s\" if empty" in {
      field.toString should be("s")
    }
    "be represented with peg\'s color if occupied" in {
      field.peg = Some(Peg(Color.yellow, field))
      field.toString should be(Color.yellow.toString)
    }
  }

  "An out field" should {
    val field = OutField(Color.yellow)
    "be represented with peg\'s color if empty" in {
      field.toString should be(Color.yellow.toString)
    }
    "be represented with peg\'s color if occupied" in {
      field.peg = Some(Peg(Color.yellow, field))
      field.toString should be(Color.yellow.toString)
    }
  }

  "A home field" should {
    val field = HomeField(Color.yellow)
    "be represented with \"h\" if empty" in {
      field.toString should be("h")
    }
    "be represented with peg\'s color if occupied" in {
      field.peg = Some(Peg(Color.yellow, field))
      field.toString should be("x")
    }
  }
}
