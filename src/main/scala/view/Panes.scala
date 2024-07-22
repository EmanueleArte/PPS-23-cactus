package view

import view.Utils.toRgbString
import control.module.CactusControllerModule
import control.module.CactusControllerModule.CactusController
import model.card.Cards.{Card, PokerCard}
import model.player.Players.Player
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control.{Button, ScrollPane, Tooltip}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox, Pane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, Text}
import view.ViewDSL.{at, colored, containing, covered, doing, long, reacting, saying, showing, tall, telling, whenHovered, Button as ButtonPane, Card as CardPaneDSL, Text as TextElement}

import scala.language.postfixOps

/** Basic interface for a pane used in a ScalaFX application. */
trait ScalaFXPane:
  /**
   * Width of the [[Pane]].
   * @return width of the pane.
   */
  def paneWidth: Int

  /**
   * Height of the [[Pane]].
   * @return height of the pane.
   */
  def paneHeight: Int

  /**
   * Position of the [[Pane]] in the container. The position refers to the top-left corner of the pane.
   * If the pane is contained in an element, the position is relative to that element.
   * @return position of the pane in the container.
   */
  def position: ViewPosition

  /**
   * Returns the [[Pane]] represented by the implementation.
   * @return the [[Pane]] object.
   */
  def pane: Pane

/** Basic structure of a card's pane. */
trait CardPane:
  /**
   * Image to print.
   * @return [[ImageView]] to print.
   */
  def imageView: ImageView

  /**
   * Name of the file of the card to print.
   * @return filename of the card.
   */
  def filename: String

/**
 * Representation of the main portion of the view of the application.
 * Contains the representation of the table game, with the panes for the players, the deck and the discard pile.
 * @param controller of the application.
 */
class MainPane(controller: CactusController) extends ScalaFXPane:
  override def paneWidth: Int         = Panes.mainPaneWidth
  override def paneHeight: Int        = Panes.mainPaneHeight
  override def position: ViewPosition = topLeftCorner
  private def dispositionRadius: Int  = paneHeight / 2 - PlayersPane.paneHeight / 2
  private def horizontalRatio: Double = paneWidth.toDouble / paneHeight.toDouble

  private val topPosition: Int = 0

  private val leftPosition: Int                                    = 0
  private val topLeftCorner: ViewPosition                          = ViewPosition(topPosition, leftPosition)
  private def paneCenter: ViewPosition                                 = ViewPosition(paneWidth, paneHeight) / 2
  private val currentPlayer: Player                                = controller.players(0)
  private val pileCardsProperty: ObjectProperty[Option[PokerCard]] = ObjectProperty(controller.pilesHead)
  private val playerCardsProperty: ObjectProperty[List[Card]] = ObjectProperty(
    currentPlayer.cards
  )

  override def pane: Pane = new Pane()
    .at(position)
    .tall(paneHeight)
    .long(paneWidth)
    .colored(Panes.mainPaneColor)
    .containing(
      controller.players.zipWithIndex
        .map((player, index) => new PlayerPane(player, calculatePlayerPosition(index)).pane)
    )
    .containing(List(new TableCenterPane().pane))

  private def updateDiscardPile(): Unit  = pileCardsProperty.setValue(controller.pilesHead)
  private def updatePlayersCards(): Unit = playerCardsProperty.setValue(currentPlayer.cards)
  private def calculatePlayerPosition(i: Int): ViewPosition =
    val theta: Double = 2 * Math.PI / controller.players.length
    val x: Int =
      (Math.sin(theta * i) * dispositionRadius * horizontalRatio).toInt + paneCenter.x - PlayersPane.paneWidth / 2
    val y: Int = (Math.cos(theta * i) * dispositionRadius).toInt + paneCenter.y - PlayersPane.paneHeight / 2
    ViewPosition(x, y)

  /**
   * Representation of the player's pane.
   * This consists in a turn indicator, a label with the name of the player and a list of cards of the player.
   * @param player represented by the pane.
   * @param position of the pane.
   */
  private class PlayerPane(player: Player, override val position: ViewPosition)
      extends ScalaFXPane: // with PlayersPane:
    playerCardsProperty.onChange((_, oldValue, newValue) => updatePlayerCards())
    override def paneWidth: Int  = PlayersPane.paneWidth
    override def paneHeight: Int = PlayersPane.paneHeight

    override def pane: Pane = new Pane()
      .at(position)
      .containing(header)
      .containing(cardsContainer)

    private def cardsNumberText: Text = TextElement
      .telling(player.cards.size.toString)
      .whenHovered("Number of cards in player's hand")

    private val header: BorderPane =
      val nameText: Text = TextElement telling player.name

      val turnIndicator: Circle = new Circle:
        centerX = position.x + PlayersPane.turnIndicatorRadius
        centerY = position.y - PlayersPane.turnIndicatorRadius
        radius = PlayersPane.turnIndicatorRadius
        fill = if currentPlayer.isEqualsTo(player) then PlayersPane.turnIndicatorColor else Color.Transparent
        stroke = PlayersPane.turnIndicatorColor

      new BorderPane():
        prefWidth = paneWidth
        left = turnIndicator
        center = nameText
        right = cardsNumberText

    private val cardsContainer: ScrollPane = new ScrollPane():
      layoutX = leftPosition
      layoutY = topPosition + PlayersPane.fontSize * 2
      style = s"-fx-background-color: transparent;"
      maxHeight = CardsPane.paneHeight * PlayersPane.maxCardsLines
      prefWidth = (CardsPane.paneWidth + CardsPane.margin) * PlayersPane.maxCardsPerLine
      hbarPolicy = ScrollBarPolicy.Never
      content = playerHand

    private def playerHand: VBox = new VBox():
      style = s"-fx-background-color: ${Panes.mainPaneColor.toRgbString};"
      prefWidth = (CardsPane.paneWidth + CardsPane.margin) * PlayersPane.maxCardsPerLine
      children = player.cards
        .map(card => CardPaneDSL showing card reacting (_ =>
            if player.isEqualsTo(currentPlayer) then
              val index: Int = player.cards.indexOf(card)
              controller.discard(index)
              updatePlayerCards()
              updateDiscardPile()
          ))
        .grouped(PlayersPane.maxCardsPerLine)
        .toList
        .map(pack => new HBox():
          children = pack
        )

    private def cardPosition(i: Int): ViewPosition = ViewPosition(
      (i % PlayersPane.maxCardsPerLine) * CardsPane.paneWidth + CardsPane.margin,
      (i / PlayersPane.maxCardsPerLine) * CardsPane.paneHeight
    )

    private def updatePlayerCards(): Unit =
      cardsContainer.content = playerHand
      header.right = cardsNumberText

  /**
   * Representation of the center of the table.
   * It consists in a deck and a discard pile.
   */
  private class TableCenterPane() extends ScalaFXPane:
    pileCardsProperty.onChange((_, oldValue, newValue) =>
      pilePane.children.clear()
      pilePane.children.add(CardPaneDSL at topLeftCorner showing newValue)
    )

    override def paneWidth: Int = CardsPane.paneWidth * 2

    override def paneHeight: Int = CardsPane.paneHeight

    override def position: ViewPosition = paneCenter - ViewPosition(paneWidth, paneHeight) / 2

    override def pane: Pane = new Pane()
      .at(position)
      .long(paneWidth)
      .tall(paneHeight)
      .containing(deckPane)
      .containing(pilePane)

    private val deckPane: Pane = new Pane()
      .at((leftPosition, topPosition))
      .containing(CardPaneDSL at topLeftCorner covered)
      .reacting(_ =>
        controller.draw(true)
        updatePlayersCards()
      )

    private val pilePane: Pane = new Pane()
      .at((CardsPane.paneWidth, topPosition))
      .containing(CardPaneDSL at topLeftCorner showing controller.pilesHead)
      .reacting(_ =>
        if controller.pilesHead.isDefined then
          controller.draw(false)
          updatePlayersCards()
          updateDiscardPile()
      )

/**
 * Representation of the lateral portion of the view.
 * Contains the buttons to continue the game and to call "Cactus".
 * @param controller of the application.
 */
class AsidePane(controller: CactusController) extends ScalaFXPane:
  override def paneWidth: Int = Panes.asidePaneWidth

  override def paneHeight: Int = Panes.asidePaneHeight

  override def position: ViewPosition = ViewPosition(Panes.mainPaneWidth, 0)

  private val nextButton: Button = ButtonPane at (0, 0) saying "Continue" doing (_ =>
    println("Continue")
    controller.continue()
  )
  private val cactusButton: Button = ButtonPane at (0, 100) saying "Cactus" doing (_ => println("Cactus!"))

  override def pane: Pane = new Pane()
    .at(position)
    .long(paneWidth)
    .tall(paneHeight)
    .colored(Panes.asidePaneColor)
    .containing(nextButton)
    .containing(cactusButton)
