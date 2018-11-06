package de.htwg.mps.menschAergereDichNicht.model

sealed trait Field { // 'sealed' traits can only be extended in same file
  val emptyFieldString: String
  var peg: Option[Peg]

  def isOccupied: Boolean = peg.isDefined

  override def toString: String = {
    peg match {
      case Some(p) => p.color.toString
      case None    => emptyFieldString
    }
  }
}

case class NormalField() extends Field {
  override val emptyFieldString: String = "o"
  override var peg: Option[Peg] = None
}

case class StartField(color: Color.Value) extends Field {
  override val emptyFieldString: String = "s"
  override var peg: Option[Peg] = None
}

case class OutField(color: Color.Value) extends Field {
  override val emptyFieldString: String = color.toString
  override var peg: Option[Peg] = None
}

case class HomeField(color: Color.Value) extends Field {
  override val emptyFieldString: String = "h"
  override var peg: Option[Peg] = None

  override def toString: String = {
    if (peg.nonEmpty) {
      "x"
    } else {
      super.toString
    }
  }
}
