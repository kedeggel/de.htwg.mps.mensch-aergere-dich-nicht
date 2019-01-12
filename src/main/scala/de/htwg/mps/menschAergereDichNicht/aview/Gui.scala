package de.htwg.mps.menschAergereDichNicht.aview
import java.awt.{Image, Point}
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.io.File

import de.htwg.mps.menschAergereDichNicht.actor.GuiActor
import javax.imageio.ImageIO

import scala.swing.event.{Key, MouseClicked}
import scala.swing.{Action, Dialog, Dimension, Frame, Graphics2D, Menu, MenuBar, MenuItem, Panel}
import scala.util.{Success, Try}

class Gui(actor: GuiActor) extends Frame {

  var lastDice = 1
  val backgroundImage = ImageIO.read(new File("./src/main/resources/Board.png"))
  val dicesImages =
    for {
      i <- 1 to 6
    } yield ImageIO.read(new File("./src/main/resources/dice-" + i + ".png"))

  val imgDim = backgroundImage.getWidth() // board is a square, so width == height
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

  def gamePanel = new Panel {


    val borderSize = 5

    listenTo(mouse.clicks)

    var scaledX = 0.0
    var scaledY = 0.0
    var centerX = 0.0
    var centerY = 0.0

    var ascale = 10.0


    def transformPoint(point: Point): Point = new Point(((point.x - centerX) / ascale).toInt, ((point.y - centerY) / ascale).toInt)

    reactions += {
      case e: MouseClicked => println(" Mouse clicked at " + e.point + " transformed: " + transformPoint(e.point))
    }



    override def paintComponent(g: Graphics2D): Unit = {
      val height = bounds.getHeight - (borderSize * 2)
      val width = bounds.getWidth - (borderSize * 2)
      val scale = (if (height < width) height else width) / imgDim
      super.paintComponent(g)

      scaledX = (bounds.getWidth - scale * imgDim) / 2
      scaledY = (bounds.getHeight - scale * imgDim) / 2
      val at = new AffineTransform()
      at.scale(scale, scale)
      ascale = scale
      val diceImg = dicesImages(lastDice - 1)

      val diceImgWidth = diceImg.getWidth()
      val diceImgHeight = diceImg.getHeight()
      val diceScale = scale / 2

      var diceScaledX = diceScale * diceImgWidth
      var diceScaledY = diceScale * diceImgHeight

      centerX = bounds.getWidth / 2
      centerY = bounds.getHeight / 2

      val xDim = (diceImgWidth * diceScale).asInstanceOf[Int]
      val yDim = (diceImgHeight * diceScale).asInstanceOf[Int]

      val imageScaled =
        diceImg.getScaledInstance(xDim, yDim, Image.SCALE_REPLICATE)

      g.drawImage(
        backgroundImage,
        new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR),
        scaledX.toInt,
        scaledY.toInt
      )

      g.drawImage(imageScaled, (centerX - diceScaledX/2).toInt , (centerY - diceScaledY/2).toInt, null)

    }

  }

  minimumSize = new Dimension(500, 500 + 2 * menuBar.size.height)

  contents = gamePanel

  def redraw = {}

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

 def update = redraw
}
