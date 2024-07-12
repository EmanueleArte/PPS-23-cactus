package view

import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.{Card, PokerCard}
import model.card.CardsData.PokerCardName.Ace
import model.card.CardsData.PokerSuit.Spades
import model.deck.Piles.{DiscardPile, PokerPile}
import player.Players.Player
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, HBox, Pane, Priority, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, Text}

import scala.io.BufferedSource

trait ViewPosition:
  def x: Int
  def y: Int

//  extension (dividend: ViewPosition)
//    def /(divisor: Int): ViewPosition

final case class ViewPositionImpl(x: Int, y: Int) extends ViewPosition

object ViewPosition:
  def apply(x: Int, y: Int): ViewPosition = ViewPositionImpl(x, y)

  extension (dividend: ViewPosition)
    def /(divisor: Int): ViewPosition =
      require(divisor > 0)
      ViewPosition(Math.floorDiv(dividend.x, divisor), Math.floorDiv(dividend.y, divisor))

trait ScalaFXPane:
  def width: Int
  def height: Int
  def position: ViewPosition
  def pane: Pane

class MainPane(context: ControllerModule.Provider) extends ScalaFXPane:
  override def width: Int             = 1400
  override def height: Int            = 900
  override def position: ViewPosition = ViewPosition(0, 0)
  def radius: Int = height / 2
  def horizontalRatio: Double = width.toDouble / height.toDouble
  def center: ViewPosition = ViewPosition(width, height) / 2

  override def pane: Pane =

    new Pane:
      children =
        context.controller.players
          .zipWithIndex
          .map((player, index) => PlayerPane(player, calculatePosition(index)).pane)

  def calculatePosition(i: Int): ViewPosition =
    val theta: Double = 2 * Math.PI / context.controller.players.length
    val x: Int = (Math.sin(theta * i) * radius * horizontalRatio).toInt + center.x
    val y: Int = (Math.cos(theta * i) * radius).toInt + center.y
    ViewPosition(x, y)

trait PlayersPane:
  def width: Int  = 100
  def height: Int = 80
  def fontSize: Int = 18
  def turnIndicatorRadius: Int = 5
  def turnIndicatorColor: Color = Color.Red

class PlayerPane(player: Player, _position: ViewPosition) extends ScalaFXPane with PlayersPane:
  def cardsPerLine: Int = 4
  def header: HBox =
    val nameText: Text = new Text:
      text = player.name
      x = position.x + turnIndicatorRadius * 3
      y = position.y
      font = Font.font(fontSize)

    val turnIndicator: Circle = new Circle:
      centerX = position.x + turnIndicatorRadius
      centerY = position.y - turnIndicatorRadius
      radius = turnIndicatorRadius
      fill = Color.Transparent
      stroke = turnIndicatorColor

    new HBox:
      children = List(turnIndicator, nameText)

  def cardsContainer: Pane = new Pane:
    children = player.cards.zipWithIndex.map((card, index) => CardPane(card, cardPosition(index)).pane)

  def cardPosition(i: Int): ViewPosition = ViewPosition(
    position.x + (i % cardsPerLine) * CardsPane.width,
    position.y + (i / cardsPerLine) * CardsPane.height
  )

  override def position: ViewPosition = _position
  def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    children = List(
      new VBox:
        children = List(header, cardsContainer)
    )

trait CardsPane:
  def aspectRatio: Double
  def width: Int
  def height: Int

object CardsPane:
  def aspectRatio: Double = 3 / 2

  def width: Int = 50

  def height: Int = (width.toDouble * aspectRatio).toInt

  def cardsFolderPath: String = "/cards"
  def backsFolderPath: String = cardsFolderPath + "/backs"
  def frontsFolderPath: String = cardsFolderPath + "/fronts"
class CardPane(card: Card, _position: ViewPosition) extends ScalaFXPane:
  override def position: ViewPosition = _position

  override def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    children = List(imageView)

  override def width: Int = 0

  override def height: Int = 0

  def fileName: String = s"${card.suit.toString.toLowerCase()}_${card.value}"
  def imageView: ImageView =
    new ImageView(new Image(getClass.getResourceAsStream(CardsPane.frontsFolderPath + s"/${fileName}.png"))):
      fitWidth = CardsPane.width
      preserveRatio = true



