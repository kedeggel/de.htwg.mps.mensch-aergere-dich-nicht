package de.htwg.mps.menschAergereDichNicht.model

import org.scalatest.{Matchers, WordSpec}

class BoardSpec extends WordSpec with Matchers {
  "A board" should {
    "have 4 start fields" in {
      Board.fields.count {
        case _: StartField => true
        case _             => false
      } should be(4)
    }
    "have 36 normal fields" in {
      Board.fields.count {
        case _: NormalField => true
        case _              => false
      } should be(36)
    }

    "have 16 out fields" in {
      Board.outFields.flatten.length should be(16)
    }

    "have 16 home fields" in {
      Board.homeFields.flatten.length should be(16)
    }

    "be displayed as expected" in {
      Board.toString should be(
        "" +
          "o o     o o s     o o\n" +
          "o o     o h o     o o\n" +
          "        o h o        \n" +
          "        o h o        \n" +
          "s o o o o h o o o o o\n" +
          "o h h h h   h h h h o\n" +
          "o o o o o h o o o o s\n" +
          "        o h o        \n" +
          "        o h o        \n" +
          "o o     o h o     o o\n" +
          "o o     s o o     o o"
      )
    }
  }
}
