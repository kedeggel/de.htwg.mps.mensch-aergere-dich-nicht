package de.htwg.mps.menschAergereDichNicht.model

sealed trait Field {                      // 'sealed' traits can only be extended in same file
  var peg: Option[Peg]
  def isOccupied: Boolean = peg != None
}

case class NormalField() extends Field {
  override var peg: Option[Peg] = None
  override def toString = {
    if (peg == None) {
      "o"
    } else {
      peg.get.color.toString
    }
  }
}

case class StartField(color: Color.Value) extends Field {
  override var peg: Option[Peg] = None
  override def toString = {
    if (peg == None) {
      "s"
    } else {
      peg.get.color.toString
    }
  }
}

case class OutField(color: Color.Value) extends Field {
  override var peg: Option[Peg] = None
}

case class HomeFields(color: Color.Value) extends Field {
  override var peg: Option[Peg] = None
  override def toString = {
    if (peg == None) {
      "h"
    } else {
      "x"
    }
  }
}
