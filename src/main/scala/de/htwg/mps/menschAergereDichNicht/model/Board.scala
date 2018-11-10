package de.htwg.mps.menschAergereDichNicht.model

import scala.collection.mutable.ListBuffer

case class Board() {
  private val NUMBER_OF_FIELDS = 40
  private val COLOR_ORDER =
    Array(Color.yellow, Color.red, Color.blue, Color.green)

  val fields: Array[Field] = generateFields()
  val homeFields: Array[Array[HomeField]] = generateHomeFields()
  val outFields: Array[Array[OutField]] = generateOutFields()

  private def generateFields(): Array[Field] = {
    var _fields: ListBuffer[Field] = new ListBuffer()
    var _colorCounter = 0
    for (i <- 0 until NUMBER_OF_FIELDS) {
      i % 10 match {
        case 0 =>
          _fields += StartField(COLOR_ORDER(_colorCounter))
          _colorCounter += 1
        case _ => _fields += NormalField()
      }
    }
    _fields.toArray
  }

  private def generateHomeFields(): Array[Array[HomeField]] = {
    val _homeFields: Array[Array[HomeField]] = Array.ofDim(4, 4)
    for (i <- 0 until 4;
         j <- 0 until 4) {
      _homeFields(i)(j) = HomeField(COLOR_ORDER(i))
    }
    _homeFields
  }

  private def generateOutFields(): Array[Array[OutField]] = {
    val _outFields: Array[Array[OutField]] = Array.ofDim(4, 4)
    for (i <- 0 until 4;
         j <- 0 until 4) {
      _outFields(i)(j) = OutField(COLOR_ORDER(i))
    }
    _outFields
  }

  override def toString: String = {
    val emptyField = "  "
    val shortSep = emptyField + emptyField
    val longSep = "        "
    val newLine = "\n"
    val longSepWithNewLine = "       " + newLine
    val fieldWithSep: Field => String = _.toString + " "
    val fieldWithNewLine: Field => String = _.toString + newLine

    var boardString =
      // 1st row
      fieldWithSep(outFields(1)(0)) + fieldWithSep(outFields(1)(1)) + shortSep +
      fieldWithSep(fields(18)) + fieldWithSep(fields(19)) + fieldWithSep(fields(20)) + shortSep +
      fieldWithSep(outFields(2)(0)) + fieldWithNewLine(outFields(2)(1)) +
      // 2nd row
      fieldWithSep(outFields(1)(2)) + fieldWithSep(outFields(1)(3)) + shortSep +
      fieldWithSep(fields(17)) + fieldWithSep(homeFields(2)(0)) + fieldWithSep(fields(21)) + shortSep +
      fieldWithSep(outFields(2)(2)) + fieldWithNewLine(outFields(2)(3)) +
      // 3rd row
      longSep + fieldWithSep(fields(16)) + fieldWithSep(homeFields(2)(1)) + fieldWithSep(fields(22)) +
      longSepWithNewLine +
      // 4th row
      longSep + fieldWithSep(fields(15)) + fieldWithSep(homeFields(2)(2)) + fieldWithSep(fields(23)) +
      longSepWithNewLine
    // 5th row
    for (i <- 10 to 28 if i <= 14 || i >= 24) {
      boardString += fieldWithSep(fields(i))
      if (i == 14)  boardString += fieldWithSep(homeFields(2)(3))
    }
    boardString = boardString.dropRight(1) + newLine
    // 6th row
    boardString += fieldWithSep(fields(9))
    for (i <- 1 to 3 if i != 2;
         j <- 0 until 4) {
      if (i == 3 && j == 0) boardString += emptyField           // add empty center field
      if (i == 1) boardString += fieldWithSep(homeFields(i)(j)) // red home row
      else boardString += fieldWithSep(homeFields(i)(3 - j))    // green home row
    }
    boardString += fieldWithNewLine(fields(29))
    // 7th row
    val start = 43
    val fieldsInBetween = 4
    val end = 30
    for (i <- start to end by -1 if i >= start - fieldsInBetween || i <= end + fieldsInBetween) {
      boardString += fieldWithSep(fields(i % (end + fieldsInBetween + 1)))
      if (i == start - fieldsInBetween) boardString += fieldWithSep(homeFields(0)(3))
    }
    boardString = boardString.dropRight(1) + newLine
    boardString +=
      // 8th row
      longSep + fieldWithSep(fields(3)) + fieldWithSep(homeFields(0)(2)) + fieldWithSep(fields(35)) +
      longSepWithNewLine +
      // 9th row
      longSep + fieldWithSep(fields(2)) + fieldWithSep(homeFields(0)(1)) + fieldWithSep(fields(36)) +
      longSepWithNewLine +
      // 10th row
      fieldWithSep(outFields(0)(2)) + fieldWithSep(outFields(1)(3)) + shortSep +
      fieldWithSep(fields(1)) + fieldWithSep(homeFields(0)(0)) + fieldWithSep(fields(37)) + shortSep +
      fieldWithSep(outFields(0)(2)) + fieldWithNewLine(outFields(0)(3)) +
      // 11th row
      fieldWithSep(outFields(1)(0)) + fieldWithSep(outFields(1)(1)) + shortSep +
      fieldWithSep(fields(0)) + fieldWithSep(fields(39)) + fieldWithSep(fields(38)) + shortSep +
      fieldWithSep(outFields(3)(0)) + outFields(3)(1) // no newline at string's end

    boardString
  }
}
