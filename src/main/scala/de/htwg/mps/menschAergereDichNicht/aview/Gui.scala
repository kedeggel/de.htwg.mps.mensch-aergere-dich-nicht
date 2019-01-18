package de.htwg.mps.menschAergereDichNicht.aview
import java.awt
import java.awt.Image
import java.io.File

import de.htwg.mps.menschAergereDichNicht.actor.GuiActor
import de.htwg.mps.menschAergereDichNicht.model.Color.{Blue, Green, Red, Yellow}
import de.htwg.mps.menschAergereDichNicht.model.{Color => _, _}
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.border.EmptyBorder

import scala.swing.event.{ButtonClicked, Key}
import scala.swing.{
  Action,
  BoxPanel,
  Button,
  Color,
  Component,
  Dialog,
  Dimension,
  Frame,
  Graphics2D,
  GridPanel,
  Label,
  Menu,
  MenuBar,
  MenuItem,
  Orientation,
  Panel
}
import scala.util.{Success, Try}

case class RoundButton(color: Color, var peg: Option[Peg] = None)
    extends Button {
  var isMovable = false
  var highlight = false
  var idx = 0
  contentAreaFilled = false

  var move: Option[Option[Int] => Unit] = None

  reactions += {
    case ButtonClicked(_) if isMovable =>
      move match {
        case Some(f) => f(Some(idx))
        case None    => println("No move function defined")
      }
  }

  def update() {
    if (peg.isDefined) {
      setIcon(peg.get.toString)
    } else {
      icon = null
    }
  }

  def setIcon(colorString: String): Unit = {
    def img =
      ImageIO.read(new File("./src/main/resources/peg-" + colorString + ".png"))
    val imageScaled = img getScaledInstance ((50 * 0.6).toInt, 50, Image.SCALE_REPLICATE)
    icon = new ImageIcon(imageScaled)
  }

  override def paintComponent(g: Graphics2D): Unit = {
    val colorWithAlpha =
      new Color(color.getRed, color.getGreen, color.getBlue, 255)
    g.setColor(colorWithAlpha)
    val diameter = ((Math.min(size.width, size.height) - 1) * 0.8).toInt
    if (highlight) {
      g.setColor(new Color(255, 0, 0))

      g.fillOval(
        (size.width - diameter) / 2,
        (size.height - diameter) / 2,
        diameter,
        diameter
      )
      g.setColor(colorWithAlpha)
      val smallDiameter = (diameter * 0.9).toInt
      g.fillOval(
        (size.width - smallDiameter) / 2,
        (size.height - smallDiameter) / 2,
        smallDiameter,
        smallDiameter
      )
    } else {
      g.fillOval(
        (size.width - diameter) / 2,
        (size.height - diameter) / 2,
        diameter,
        diameter
      )
    }
    super.paintComponent(g)
  }

  override def paintBorder(g: Graphics2D): Unit = {
    g.setColor(new Color(0, 0, 0))
    val diameter = ((Math.min(size.width, size.height) - 1) * 0.8).toInt
    g.drawOval(
      (size.width - diameter) / 2,
      (size.height - diameter) / 2,
      diameter,
      diameter
    )
  }
}

class DiceButton(var dots: Int) extends Button {
  private val dicesImages =
    for {
      i <- 1 to 6
    } yield ImageIO.read(new File("./src/main/resources/dice-" + i + ".png"))

  def setDots(dots: Int): Unit = {
    this.dots = dots
    if (6 < dots) {
      this.dots = 1
    }

    val imageScaled = dicesImages(this.dots - 1) getScaledInstance (80, 80, Image.SCALE_REPLICATE)
    preferredSize = new Dimension(80, 80)
    minimumSize = new Dimension(80, 80)
    maximumSize = new Dimension(80, 80)
    icon = new ImageIcon(imageScaled)
  }

  setDots(dots)
}

class Gui(actor: GuiActor) extends Frame {
  private val BACKGROUND_COLOR: Color = new Color(255, 222, 173)
  val movablePegs: Array[Option[RoundButton]] = Array.ofDim(4)
  var lastDice = 1
  val diceButton = new DiceButton(lastDice)
  val fieldButtons: Array[RoundButton] = Array.ofDim(40)
  val homeFieldButtons: Array[Array[RoundButton]] = Array.ofDim(4, 4)
  val outFieldButtons: Array[Array[RoundButton]] = Array.ofDim(4, 4)
  var components: Array[Array[Component]] = generateFields()

  val gamePanel: GridPanel = new GridPanel(11, 11) {
    listenTo(this)
    resizable = false
    border = new EmptyBorder(10, 10, 10, 10)
    background = BACKGROUND_COLOR
    var homeFieldCounter = 0
    var outFieldCounter = 0
    for {
      i <- 0 to 10
      j <- 0 to 10
    } {
      contents += components(i)(j)
    }
  }

  private val currentPlayerLabel = new Label("Welcome") {
    xLayoutAlignment = awt.Component.CENTER_ALIGNMENT
    font = font.deriveFont(20f)
  }
  listenTo(diceButton)

  minimumSize = new Dimension(880, 880 + 2 * menuBar.size.height)
  title = "HTWG - Mensch ärgere dich nicht"
  menuBar = new MenuBar {
    contents += new Menu("File") {
      mnemonic = Key.F
      contents += new MenuItem(Action("New Game") {
        actor.newGame
      })
    }
  }

  visible = true
  private var diceRequested = false
  private var diceRolled = false

  contents = new BoxPanel(Orientation.Vertical) {
    contents += gamePanel
    contents += currentPlayerLabel
  }

  def numberOfPlayer(min: Int, max: Int): Int = {
    if (actor.handler.get.handled) {
      return -1
    }
    val r = Dialog.showInput(
      contents.head,
      "Number of Players [" + min + "-" + max + "]",
      title = "Number of Players",
      initial = "2"
    )

    r match {
      case Some(s) =>
        Try(s.toInt) match {
          case Success(i) if i >= min && i <= max => i
          case _                                  => numberOfPlayer(min, max)
        }
      case _ => numberOfPlayer(min, max)
    }
  }

  def rollDice(): Int = {
    diceRequested = true
    while (!diceRolled && !actor.handler.get.handled) {
      Thread.sleep(10)
    }
    if (actor.handler.get.restart) return -1
    val rem = actor.handler.get.handled
    for (_ <- 1 to 40) {
      lastDice = Dice.roll()
      Thread.sleep(15)
      diceButton.setDots(lastDice)
    }

    diceRolled = false
    diceRequested = false
    if (rem) return -1
    lastDice
  }

  def updateDice(value: Int) = {
    lastDice = value
    diceButton.setDots(lastDice)
  }

  def setCurrentPlayer(player: String) = {
    currentPlayerLabel.preferredSize = new Dimension(800, 800)
    currentPlayerLabel.text = "Current Player: " + player

  }

  def updatePegs(pegs: Array[Array[Peg]]): Unit = {
    fieldButtons foreach (_.peg = None)
    homeFieldButtons foreach (_ foreach (_ peg = None))
    outFieldButtons foreach (_ foreach (_ peg = None))
    components foreach (_ filter (_.isInstanceOf[RoundButton]) foreach { p =>
      val s = p.asInstanceOf[RoundButton]
      s.peg = None
      s.highlight = false
      s.isMovable = false
    })
    for (color <- pegs) {
      var outs = 0
      var homes = 1
      for (peg <- color) {
        peg.relativ_position() match {
          case Some(i) if i < 40 => // on start or normal field
            fieldButtons(peg.absolute_position().get).peg = Some(peg)
            fieldButtons(peg.absolute_position().get).update()
          case Some(i) if i >= 40 => // on home row
            homeFieldButtons(peg.color.toInt())(i - 40) =
              RoundButton(transformColor(peg.color), Some(peg))
            homeFieldButtons(peg.color.toInt())(homes).update()
            homes += 1
          case None => // on out row
            outFieldButtons(peg.color.toInt)(outs).peg = Some(peg)
            outFieldButtons(peg.color.toInt)(outs).update()
            outs += 1
        }
      }
    }

    for {
      row <- components
      comp <- row
      if comp.isInstanceOf[RoundButton]
    } {
      comp.asInstanceOf[RoundButton].update()
    }
  }

  def makePegsMovable(move: Option[Int] => Unit): Unit = {
    movablePegs.filter(_ isDefined).map(_ get).foreach { p =>
      p.move = Some(move)
      p.isMovable = true
      p.highlight = true
      p.update()
    }
  }
  def highlight(options: Array[Option[Peg]]): Unit = {
    var outs = 0
    var homes = 0
    for (i <- movablePegs.indices) movablePegs(i) = None
    for ((peg, idx) <- options.zipWithIndex) {
      peg match {
        case Some(peg) =>
          movablePegs(movablePegs.count(p => p.isDefined)) =
            peg.relativ_position() match {
              case Some(i) if i < 40 => // on start or normal field
                val j = peg.absolute_position.get
                fieldButtons(j).idx = idx + 1
                Some(fieldButtons(j))
              case Some(i) if i >= 40 => // on home row
                homes += 1
                homeFieldButtons(peg.color.toInt)(homes - 1).idx = idx + 1
                Some(homeFieldButtons(peg.color.toInt)(homes - 1))
              case None => // on out row
                outs += 1
                outFieldButtons(peg.color.toInt)(outs - 1).idx = idx + 1
                Some(outFieldButtons(peg.color.toInt)(outs - 1))
            }
        case None => // Do nothing
      }
    }
  }

  reactions += {
    case ButtonClicked(b) if b == diceButton => diceRolled = true
  }

  private def generateFields(): Array[Array[Component]] = {
    val componentMatrix: Array[Array[Component]] = Array.ofDim(11, 11)
    for {
      i <- 0 to 10
      j <- 0 to 10
    } {
      componentMatrix(i)(j) = if ((i, j) == (5, 5)) {
        diceButton
      } else {
        Board.lookupField(j, i) match {
          case Some((f, idx)) =>
            f match {
              case NormalField(_) =>
                val btn = RoundButton(new Color(255, 255, 255))
                fieldButtons(idx) = btn
                btn

              case StartField(_, color) =>
                val btn = RoundButton(transformColor(color))
                fieldButtons(idx) = btn
                btn

              case HomeField(_, color) =>
                val btn = RoundButton(transformColor(color))
                homeFieldButtons(color.toInt())(idx) = btn
                btn

              case OutField(_, color) =>
                val btn = RoundButton(transformColor(color))
                outFieldButtons(color.toInt())(idx) = btn
                btn
            }
          case None =>
            new Panel {
              background = new Color(255, 222, 173)
            }
        }
      }
    }
    componentMatrix
  }

  def showWinScreen(winner: Array[String]): Unit = {}
  def showEndGame(): Unit = {}

  def transformColor(
    color: de.htwg.mps.menschAergereDichNicht.model.Color.Value
  ): Color = {
    color match {
      case Blue   => new Color(77, 77, 255)
      case Yellow => new Color(220, 220, 0)
      case Green  => new Color(0, 255, 0)
      case Red    => new Color(255, 77, 77)
    }
  }

  centerOnScreen
}
