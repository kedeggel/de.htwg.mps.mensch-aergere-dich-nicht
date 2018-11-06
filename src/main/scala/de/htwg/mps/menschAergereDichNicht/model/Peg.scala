package de.htwg.mps.menschAergereDichNicht.model

case class Peg(color: Color.Value, var field: Field) {
  field.peg = Some(this)
}
