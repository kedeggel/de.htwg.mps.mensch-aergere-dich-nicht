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
  def relativ_position(): Option[Int] = {
    field_id
  }
  def absolute_position(): Option[Int] = {
    field_id match {
      case Some(x) => {
        val id_start_field = color.toInt() * 10
        Some((x + id_start_field) % 40)
      }
      case None =>  field_id
    }
  }
}
