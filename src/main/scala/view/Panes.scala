package view

import view.Utils.toRgbString
import control.module.CactusControllerModule
import control.module.CactusControllerModule.CactusController
import model.card.Cards.{Card, PokerCard}
import model.player.Players.Player
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control.{Button, ScrollPane, TextArea, TitledPane, Tooltip}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox, Pane, Region, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, Text}
import view.ModelPhases.{Discard, Draw}
import view.ViewDSL.{bold, wrapped, at, colored, containing, covered, doing, long, reacting, saying, showing, small, tall, tallAtMost, telling, whenHovered, withoutVBar, Button as ButtonElement, Card as CardElement, Text as TextElement}

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
      .containing(cardsContainer)(player.cards.nonEmpty)

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
      .at((leftPosition, topPosition + PlayersPane.normalFontSize * 2))
      .long((CardsPane.paneWidth + CardsPane.margin) * PlayersPane.maxCardsPerLine)
      .tallAtMost(CardsPane.paneHeight * PlayersPane.maxCardsLines)
      .colored(Color.Transparent)
      .containing(playerHand)
      .withoutVBar

    private def playerHand: VBox = new VBox()
      .colored(Panes.mainPaneColor)
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
        controller.discard(index)
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
  private val currentPhase: Phases = phaseBuilder(controllerPhase)
  override def paneWidth: Int = Panes.asidePaneWidth

  override def paneHeight: Int = Panes.asidePaneHeight

  override def position: ViewPosition = ViewPosition(Panes.mainPaneWidth, 0)

  private val nextButton: Button = ButtonElement saying "Continue" doing (_ =>
    println("Continue")
    controller.continue()
  )
  private val cactusButton: Button = ButtonElement saying "Cactus" doing (_ => println("Cactus!"))
  private val phaseText: HBox = new HBox()
    .containing(TextElement telling "Current phase: " bold)
    .containing(TextElement telling currentPhase.name)
  phaseText.setAlignment(Pos.BaselineLeft)

  private val phaseDescription: VBox = new VBox()
    .containing(TextElement telling "Phase description" bold)
    .containing(TextElement telling currentPhase.description wrapped)


  override def pane: Pane = new VBox()
    .at(position)
    .long(paneWidth)
    .tall(paneHeight)
    .colored(Panes.asidePaneColor)
    .containing(phaseText)
    .containing(phaseDescription)
    .containing(nextButton)
    .containing(cactusButton)

enum ModelPhases:
  case Draw, Discard, SpecialEffects

def controllerPhase: ModelPhases = Discard

def phaseBuilder(phase: ModelPhases): Phases = phase match
  case ModelPhases.Draw => Phases.Draw
  case ModelPhases.Discard => Phases.Discard

enum Phases(phase: ModelPhases, _name: String, _description: String):
  case Draw extends Phases(ModelPhases.Draw, "draw", "Click on the deck or on the discard pile to draw a card.")
  case Discard extends Phases(ModelPhases.Discard, "discard", "Choose a card to put on the discard pile.")

  def name: String = _name
  def description: String = _description