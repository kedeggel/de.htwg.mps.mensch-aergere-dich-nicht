package de.htwg.mps.menschAergereDichNicht.model

sealed trait Field {                      // 'sealed' traits can only be extended in same file
  var peg: Option[Peg]
  def isOccupied: Boolean = peg != None
}

case class NormalField(index: Int) extends Field {
  override var peg: Option[Peg] = None
}

case class StartField(index: Int, color: Color.Value) extends Field {
  override var peg: Option[Peg] = None
}

case class OutField(index: Int, color: Color.Value) extends Field {
  override var peg: Option[Peg] = None
}

case class HomeFields(index: Int, color: Color.Value) extends Field {
  override var peg: Option[Peg] = None
}
