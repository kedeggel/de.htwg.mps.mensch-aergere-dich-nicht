package de.htwg.mps.menschAergereDichNicht.model

import de.htwg.mps.menschAergereDichNicht.model.Color.{Blue, Green, Red, Yellow}

case class Peg(color: Color.Value, field_id: Option[Int]) {
  override def toString: String = color match {
    case Blue => "b"
    case Yellow => "y"
    case Green => "g"
    case Red => "r"
  }
  def move(fields: Int) : Peg = {
    field_id match {
      case Some(x) => Peg(color, Some(x+fields))
      case None => if (fields != 6) { Peg(color, None) } else { Peg(color, Some(0)) }
    }
  }
}
