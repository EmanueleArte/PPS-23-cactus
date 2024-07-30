package view.module.cactus

import control.module.cactus.CactusControllerModule.CactusController
import control.module.cactus.CactusControllerModule
import model.card.Cards.{Card, Coverable, PokerCard}
import model.logic.{CactusTurnPhase, TurnPhase}
import model.player.Players.Player
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, ScrollPane}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox, Pane, Priority, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text
import view.Utils.turnPhaseDescription
import view.ViewDSL.{^, at, bold, colored, containing, covered, doing, long, reacting, saying, showing, tall, tallAtMost, telling, v, whenHovered, withoutVBar, wrapped, Button as ButtonElement, Card as CardElement, Text as TextElement}
import view.ViewPosition
import view.module.cactus.CardsPane.*
import view.module.cactus.PlayersPane.*
import view.module.cactus.AppPane.*
import view.module.cactus.Text.*

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
 * Contains the representation of the table game, with the AppPane for the players, the deck and the discard pile.
 * @param controller of the application.
 */
class MainPane(controller: CactusController) extends ScalaFXPane:
  override def paneWidth: Int         = mainPaneWidth
  override def paneHeight: Int        = mainPaneHeight
  override def position: ViewPosition = topLeftCorner
  private def dispositionRadius: Int  = paneHeight / 2 - PlayersPane.paneHeight / 2
  private def horizontalRatio: Double = paneWidth.toDouble / paneHeight.toDouble

  private val topPosition: Int = 0

  private val leftPosition: Int                                    = 0
  private val topLeftCorner: ViewPosition                          = ViewPosition(topPosition, leftPosition)
  private def paneCenter: ViewPosition                                 = ViewPosition(paneWidth, paneHeight) / 2
  private val currentPlayer: Player                                = controller.players(0)
  private val pileCardsProperty: ObjectProperty[Option[PokerCard & Coverable]] = ObjectProperty(controller.pilesHead)
  private val playerCardsProperty: ObjectProperty[List[Card]] = ObjectProperty(
    currentPlayer.cards
  )

  override def pane: Pane = new Pane()
//    .at(position)
    .tall(paneHeight)
    .long(paneWidth)
    .colored(AppPane.mainPaneColor)
    .containing(
      controller.players.zipWithIndex
        .map((player, index) => new PlayerPane(player, calculatePlayerPosition(index)).pane)
    )
    .containing(List(new TableCenterPane().pane))

  def updateDiscardPile(): Unit  = pileCardsProperty.setValue(controller.pilesHead)
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

    private val cardsContainer: ScrollPane = new ScrollPane()
      .at((leftPosition, topPosition + normalFontSize * 2))
      .long((CardsPane.paneWidth + CardsPane.margin) * PlayersPane.maxCardsPerLine)
      .tallAtMost(CardsPane.paneHeight * PlayersPane.maxCardsLines)
      .colored(Color.Transparent)
      .containing(playerHand)
      .withoutVBar

    private def playerHand: VBox = new VBox()
      .colored(AppPane.mainPaneColor)
      .long((CardsPane.paneWidth + CardsPane.margin) * PlayersPane.maxCardsPerLine)
      .containing(player.cards
            .map(card => CardElement showing card reacting (_ => cardClickHandler(card)))
            .grouped(PlayersPane.maxCardsPerLine)
            .toList
            .map(pack => new HBox() containing pack)
      )

    private def cardClickHandler(card: Card): Unit =
      if player.isEqualsTo(currentPlayer) then
        val index: Int = player.cards.indexOf(card)
        controller.handlePlayerInput(index)
        updatePlayerCards()
        updateDiscardPile()

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
      pilePane.children.add(CardElement at topLeftCorner showing newValue)
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
      .containing(CardElement at topLeftCorner covered)
      .reacting(_ =>
        controller.draw(true)
        updatePlayersCards()
      )

    private val pilePane: Pane = new Pane()
      .at((CardsPane.paneWidth, topPosition))
      .containing(CardElement at topLeftCorner showing controller.pilesHead)
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
  private val turnPhaseProperty: ObjectProperty[TurnPhase] = ObjectProperty(controller.currentPhase)
  turnPhaseProperty.onChange((_, oldValue, newValue) => updatePane())

  def updateViewTurnPhase(): Unit = turnPhaseProperty.setValue(controller.currentPhase)

  override def paneWidth: Int = AppPane.asidePaneWidth

  override def paneHeight: Int = asidePaneHeight

  override def position: ViewPosition = ViewPosition(mainPaneWidth, 0)

  private val _nextButton: Button = ButtonElement saying "Continue" doing (_ => controller.continue())
  private val _cactusButton: Button = ButtonElement saying "Cactus" doing (_ => controller.callCactus())
  private def _phaseText: VBox = new VBox()
    .containing(TextElement telling "Current phase: " bold)
    .containing(TextElement telling turnPhaseDescription(turnPhaseProperty.value)._1 wrapped)
  _phaseText.setAlignment(Pos.BaselineLeft)

  private def _phaseDescription: VBox = new VBox()
    .containing(TextElement telling "Phase description" bold)
    .containing(TextElement telling turnPhaseDescription(turnPhaseProperty.value)._2 wrapped)

  private val _phaseContainer: VBox = new VBox()
    .containing(_phaseText)
    .containing(_phaseDescription)
  private val _buttonsContainer: VBox = new VBox()
    .containing(_nextButton)
    .containing(_cactusButton)

  private val _pane: BorderPane = new BorderPane()
    .at(position)
    .tall(paneHeight)
    .colored(AppPane.asidePaneColor)
    .^(_phaseContainer)
    .v(_buttonsContainer)
  VBox.setVgrow(_pane, Priority.Always)

  private def updatePane(): Unit =
    _phaseContainer.children.clear()
    _phaseContainer.children.add(_phaseText)
    _phaseContainer.children.add(_phaseDescription)
    _pane.top = _phaseContainer

  override def pane: Pane = _pane
