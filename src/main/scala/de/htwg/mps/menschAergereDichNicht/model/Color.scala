package de.htwg.mps.menschAergereDichNicht.model
import de.htwg.mps.menschAergereDichNicht.model

object Color extends Enumeration {
  type EnumType = Value
  val Yellow, Blue, Green, Red = Value

  implicit class ColorValue(color: Value) {
    def toInt() : Int = color match {
      case Blue => 0
      case Yellow => 1
      case Green => 2
      case Red => 3
    }
  }

}
