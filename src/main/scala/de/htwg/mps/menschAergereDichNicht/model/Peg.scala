package de.htwg.mps.menschAergereDichNicht.model

import de.htwg.mps.menschAergereDichNicht.model.Color.{Blue, Green, Red, Yellow}

case class Peg(color: Color.Value) {
  override def toString: String = color match {
    case Blue => "b"
    case Yellow => "y"
    case Green => "g"
    case Red => "r"
  }
}
