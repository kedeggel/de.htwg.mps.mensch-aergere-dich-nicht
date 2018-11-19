package de.htwg.mps.menschAergereDichNicht.model

sealed trait Field { // 'sealed' traits can only be extended in same file
  val emptyFieldString: String
  val id: Int

  def toString(peg: Option[Peg]) : String = {
    peg match {
      case Some(peg) => peg.color.arg()
      case None    => emptyFieldString
    }
  }
}

case class NormalField(id: Int) extends Field {
  override val emptyFieldString: String = "o"
}

case class StartField(id: Int, color: Color.Value) extends Field {
  override val emptyFieldString: String = "s"
}

case class OutField(id: Int, color: Color.Value) extends Field {
  override val emptyFieldString: String = "o"
}

case class HomeField(id: Int, color: Color.Value) extends Field {
  override val emptyFieldString: String = "h"

  override def toString(peg: Option[Peg]) : String = {
    if (peg.nonEmpty) {
      "x"
    } else {
      emptyFieldString
    }
  }
}
