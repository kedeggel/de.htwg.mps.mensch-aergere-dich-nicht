package de.htwg.mps.menschAergereDichNicht

import de.htwg.mps.menschAergereDichNicht.model._

object Tui {
  def render(): Unit = {
    var a_out = 4
    var b_out = 4
    var c_out = 4
    var d_out = 4
    var a_home = Array(HomeFields(Color.yellow), HomeFields(Color.yellow), HomeFields(Color.yellow), HomeFields(Color.yellow))
    var b_home = Array(HomeFields(Color.blue), HomeFields(Color.blue), HomeFields(Color.blue), HomeFields(Color.blue))
    var c_home = Array(HomeFields(Color.green), HomeFields(Color.green), HomeFields(Color.green), HomeFields(Color.green))
    var d_home = Array(HomeFields(Color.red), HomeFields(Color.red), HomeFields(Color.red), HomeFields(Color.red))

    val field = Array(
      StartField(Color.yellow), NormalField(), NormalField(), NormalField(), NormalField(),
      NormalField(), NormalField(), NormalField(), NormalField(), NormalField(),
      StartField(Color.blue), NormalField(), NormalField(), NormalField(), NormalField(),
      NormalField(), NormalField(), NormalField(), NormalField(), NormalField(),
      StartField(Color.green), NormalField(), NormalField(), NormalField(), NormalField(),
      NormalField(), NormalField(), NormalField(), NormalField(), NormalField(),
      StartField(Color.red), NormalField(), NormalField(), NormalField(), NormalField(),
      NormalField(), NormalField(), NormalField(), NormalField(), NormalField(),
    )
    println("a:" + a_out + "     " + field(8) + " " + field(9) + " " + field(10) + "     " + "b:" + b_out)
    println("        " + field(7) + " " + b_home(0) + " " + field(11))
    println("        " + field(6) + " " + b_home(1) + " " + field(12))
    println("        " + field(5) + " " + b_home(2) + " " + field(13))

    for ( i <- 0 to 4) {
      print(field(i) + " ")
    }
    print(b_home(3) + " ")
    for ( i <- 14 to 17) {
      print(field(i) + " ")
    }
    println(field(18))

    print(field(39) + " ")
    for ( i <- 0 to 3) {
      print(a_home(i) + " ")
    }
    print("  ")
    for( i <- 0 to 3) {
      print(c_home(3-i) + " ")
    }
    println(field(19))

    for ( i <- 0 to 4) {
      print(field(38-i) + " ")
    }
    print(d_home(3) + " ")
    for ( i <- 0 to 3) {
      print(field(24-i) + " ")
    }
    println(field(20))

    println("        " + field(33) + " " + d_home(2) + " " + field(25))
    println("        " + field(32) + " " + d_home(1) + " " + field(26))
    println("        " + field(31) + " " + d_home(0) + " " + field(27))
    println("d:" + d_out + "     " + field(30) + " " + field(29) + " " + field(28) + "     " + "c:" + c_out)

  }
}
