package de.htwg.mps.menschAergereDichNicht.model

import scala.collection.mutable.ListBuffer

case class Board() {
  private val NUMBER_OF_FIELDS = 40
  private val COLOR_ORDER =
    Array(Color.Yellow, Color.Red, Color.Blue, Color.Green)

  val fields: Array[Field] = generateFields()
  val homeFields: Array[Array[HomeField]] = generateHomeFields()
  val outFields: Array[Array[OutField]] = generateOutFields()

  private val lookupTableFields = Array(
    (4, 10),  (4, 9), (4, 8), (4, 7), (4, 6), (3, 6), (2, 6), (1, 6), (0, 6),   (0, 5),
    (0, 4),   (1, 4), (2, 4), (3, 4), (4, 4), (4, 3), (4, 2), (4, 1), (4, 0),   (5, 0),
    (6, 0),   (6, 1), (6, 2), (6, 3), (6, 4), (7, 4), (8, 4), (9, 4), (10, 4),  (10, 5),
    (10, 6),  (9, 6), (8, 6), (7, 6), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10),  (5, 10),
  )

  private val lookupTableHomeFields = Array(
    Array((5,9),(5,8),(5,7),(5,6)),
    Array((1,5),(2,5),(3,5),(4,5)),
    Array((5,1),(5,2),(5,3),(5,4)),
    Array((6,5),(7,5),(8,5),(9,5))
  )

  private val lookupTableOutFields = Array(
    Array((0,0),(0,1),(1,0),(1,1)),
    Array((9,0),(9,1),(10,0),(10,1)),
    Array((0,9),(0,10),(1,9),(1,10)),
    Array((9,9),(9,10),(10,9),(10,10))
  )

  private def generateFields(): Array[Field] = {
    var _fields: ListBuffer[Field] = new ListBuffer()
    var _colorCounter = 0
    for (i <- 0 until NUMBER_OF_FIELDS) {
      i % 10 match {
        case 0 =>
          _fields += StartField(i, COLOR_ORDER(_colorCounter))
          _colorCounter += 1
        case _ => _fields += NormalField(i)
      }
    }
    _fields.toArray
  }

  private def generateHomeFields(): Array[Array[HomeField]] = {
    val _homeFields: Array[Array[HomeField]] = Array.ofDim(4, 4)
    for (i <- 0 until 4;
         j <- 0 until 4) {
      _homeFields(i)(j) = HomeField(j, COLOR_ORDER(i))
    }
    _homeFields
  }

  private def generateOutFields(): Array[Array[OutField]] = {
    val _outFields: Array[Array[OutField]] = Array.ofDim(4, 4)
    for (i <- 0 until 4;
         j <- 0 until 4) {
      _outFields(i)(j) = OutField(j, COLOR_ORDER(i))
    }
    _outFields
  }

  def lookupField(field: Field): (Int, Int) = {
    field match {
      case f: NormalField => lookupTableFields(f.id)
      case f: StartField => lookupTableFields(f.id)
      case f: OutField => lookupTableOutFields(f.color.toInt())(f.id)
      case f: HomeField => lookupTableHomeFields(f.color.toInt())(f.id)
      case _ => (3, 3)
    }
  }

  override def toString: String = {
    // create array for text representation
    var fieldChars = Array.fill(11)(Array.fill(21)(" "))

    // add normal and start fields
    for ( field <- fields ) {
      var (x, y) = lookupField(field)
      fieldChars(y)(x*2) = field.toString(None)
    }

    // add home fields
    for ( colors <- homeFields ) {
      for ( field <- colors) {
        var (x, y) = lookupField(field)
        fieldChars(y)(x*2) = field.toString(None)
      }
    }

    // add out fields
    for ( colors <- outFields ) {
      for ( field <- colors) {
        var (x, y) = lookupField(field)
        fieldChars(y)(x*2) = field.toString(None)
      }
    }

    // convert array to string
    var boardString = ""
    for (row <- fieldChars) {
      boardString += row.mkString + "\n"
    }

    //remove last newline before returning
    boardString.dropRight(1)
  }
}
