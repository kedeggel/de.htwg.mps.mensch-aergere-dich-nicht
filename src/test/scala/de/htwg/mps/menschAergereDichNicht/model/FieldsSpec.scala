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
}
