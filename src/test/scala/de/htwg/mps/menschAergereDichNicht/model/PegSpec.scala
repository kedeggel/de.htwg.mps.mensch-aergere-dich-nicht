package de.htwg.mps.menschAergereDichNicht.model

import org.scalatest.{Matchers, WordSpec}

class PegSpec extends WordSpec with Matchers {
  "A peg" should {
    val field = NormalField()
    val peg = Peg(Color.yellow, field)
    "know its field" in {
      peg.field should be(field)
    }
    "be known be its field" in {
      field.peg.get should be(peg)
    }
  }
}
