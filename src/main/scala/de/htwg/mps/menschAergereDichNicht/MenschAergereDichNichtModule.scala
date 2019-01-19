package de.htwg.mps.menschAergereDichNicht

import com.google.inject.AbstractModule
import de.htwg.mps.menschAergereDichNicht.model.DiceInterface
import de.htwg.mps.menschAergereDichNicht.model.{diceBaseImpl, diceFakeImpl}
import net.codingwell.scalaguice.ScalaModule

class MenschAergereDichNichtModule extends AbstractModule with ScalaModule  {
  override def configure(): Unit = {
//    bind[DiceInterface].toInstance(new diceFakeImpl.Dice(Array(1,2,3,4,5,6)))
    bind[DiceInterface].to[diceBaseImpl.Dice]
  }
}
