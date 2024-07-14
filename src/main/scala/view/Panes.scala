package view

import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.{Card, PokerCard}
import model.card.CardsData.PokerCardName.Ace
import model.card.CardsData.PokerSuit.Spades
import model.deck.Piles.{DiscardPile, PokerPile}
import player.Players.{CactusPlayer, Player}
import scalafx.application.JFXApp3
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, HBox, Pane, Priority, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, Text}

import scala.io.BufferedSource

trait ViewPosition:
  def x: Int
  def y: Int
final case class ViewPositionImpl(x: Int, y: Int) extends ViewPosition

object ViewPosition:
  def apply(x: Int, y: Int): ViewPosition = ViewPositionImpl(x, y)

  extension (position: ViewPosition)
    def /(divisor: Int): ViewPosition =
      require(divisor > 0)
      ViewPosition(Math.floorDiv(position.x, divisor), Math.floorDiv(position.y, divisor))

    def -(position2: ViewPosition): ViewPosition = ViewPosition(position.x - position2.x, position.y - position2.y)

trait ScalaFXPane:
  def paneWidth: Int
  def paneHeight: Int
  def position: ViewPosition
  def pane: Pane

object Panes:
  def windowWidth: Int     = 1200
  def windowHeight: Int    = 800
  def mainPaneWidth: Int   = 1000
  def mainPaneHeight: Int  = windowHeight
  def asidePaneWidth: Int  = windowWidth - mainPaneWidth
  def asidePaneHeight: Int = windowHeight

object PlayersPane:
  def paneWidth: Int = CardsPane.paneWidth * cardsPerLine

  def paneHeight: Int = CardsPane.paneHeight * maxCardsLines + 18

  def fontSize: Int = 18

  def turnIndicatorRadius: Int = 5

  def turnIndicatorColor: Color = Color.Red

  def cardsPerLine: Int = 4

  def maxCardsLines: Int = 3

object CardsPane:
  def aspectRatio: Double = 3.0 / 2.0

  def paneWidth: Int = 50 + margin

  def paneHeight: Int = (paneWidth.toDouble * aspectRatio).toInt + margin
  def margin: Int     = 5

  def cardsFolderPath: String  = "/cards"
  def backsFolderPath: String  = cardsFolderPath + "/backs"
  def frontsFolderPath: String = cardsFolderPath + "/fronts"

class MainPane(context: ControllerModule.Provider) extends ScalaFXPane:
  override def paneWidth: Int         = Panes.mainPaneWidth
  override def paneHeight: Int        = Panes.mainPaneHeight
  override def position: ViewPosition = ViewPosition(0, 0)
  private def dispositionRadius: Int  = paneHeight / 2 - PlayersPane.paneHeight / 2
  private def horizontalRatio: Double = paneWidth.toDouble / paneHeight.toDouble
  private def center: ViewPosition    = (ViewPosition(paneWidth, paneHeight) / 2)

  override def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    prefWidth = paneWidth
    prefHeight = paneHeight
    style = "-fx-background-color: lime;"
    children = context.controller.players.zipWithIndex
      .map((player, index) => new PlayerPane(player, calculatePosition(index)).pane)

  def calculatePosition(i: Int): ViewPosition =
    val theta: Double = 2 * Math.PI / context.controller.players.length
    val x: Int =
      (Math.sin(theta * i) * dispositionRadius * horizontalRatio).toInt + center.x - PlayersPane.paneWidth / 2
    val y: Int = (Math.cos(theta * i) * dispositionRadius).toInt + center.y - PlayersPane.paneHeight / 2
    ViewPosition(x, y)

  class PlayerPane(player: CactusPlayer, _position: ViewPosition) extends ScalaFXPane: // with PlayersPane:
    def playerCards: ObjectProperty[CactusPlayer] = ObjectProperty(this, "cards", player)
    playerCards.onChange((_, oldValue, newValue) => println(s"Cards changed: ${newValue}"))

    def header: HBox =
      val nameText: Text = new Text:
        text = player.name
        x = position.x + PlayersPane.turnIndicatorRadius * 3
        y = position.y
        font = Font.font(PlayersPane.fontSize)

      val turnIndicator: Circle = new Circle:
        centerX = position.x + PlayersPane.turnIndicatorRadius
        centerY = position.y - PlayersPane.turnIndicatorRadius
        radius = PlayersPane.turnIndicatorRadius
        fill = Color.Transparent
        stroke = PlayersPane.turnIndicatorColor

      new HBox:
        children = List(turnIndicator, nameText)

    val cardsContainer: Pane = new Pane:
      layoutX = 0
      layoutY = 0
      children = player.cards.zipWithIndex.map((card, index) => new CardPane(card, cardPosition(index)).pane)

    def cardPosition(i: Int): ViewPosition = ViewPosition(
      (i % PlayersPane.cardsPerLine) * CardsPane.paneWidth + CardsPane.margin,
      (i / PlayersPane.cardsPerLine) * CardsPane.paneHeight
    )


    def handleCardClick(card: Pane, name: String): Unit =
      println(name)
      println(player.cards)
      cardsContainer.children.clear()
      player.cards
        .zipWithIndex
        .map((card, index) => new CardPane(card, cardPosition(index)).pane)
        .foreach(pane => cardsContainer.children.add(pane))

    override def position: ViewPosition = _position

    def pane: Pane = new Pane:
      layoutX = position.x
      layoutY = position.y
      children = List(
        new VBox:
          children = List(header, cardsContainer)
      )

    override def paneWidth: Int = PlayersPane.paneWidth

    override def paneHeight: Int = PlayersPane.paneHeight

    class CardPane(card: Card, _position: ViewPosition) extends ScalaFXPane:
      override def position: ViewPosition = _position

      override def pane: Pane = new Pane:
        layoutX = position.x
        layoutY = position.y
        prefWidth = paneWidth
        prefHeight = paneHeight
        children = List(imageView)
        onMouseClicked = _ =>
          // TODO: use player.isHuman or something similar
          val index: Int = player.cards.indexOf(card)
          context.controller.playerDiscards(player, index)
          handleCardClick(this, s"${card.value.toString} of ${card.suit.toString}")

      override def paneWidth: Int = CardsPane.paneWidth

      override def paneHeight: Int = CardsPane.paneHeight

      def fileName: String = s"${card.suit.toString.toLowerCase()}_${card.value}"

      def imageView: ImageView =
        new ImageView(new Image(getClass.getResourceAsStream(CardsPane.frontsFolderPath + s"/${fileName}.png"))):
          fitWidth = CardsPane.paneWidth - CardsPane.margin
          preserveRatio = true

class AsidePane(context: ControllerModule.Provider) extends ScalaFXPane:
  override def paneWidth: Int = 200

  override def paneHeight: Int = 800

  override def position: ViewPosition = ViewPosition(MainPane(context).paneWidth, 0)

  override def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    prefWidth = paneWidth
    prefHeight = paneHeight
    style = "-fx-background-color: red;"
    children = List(new Button("Hello"))
