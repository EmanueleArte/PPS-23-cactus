package view.module.cactus

import control.module.cactus.CactusControllerModule.CactusController
import control.module.cactus.CactusControllerModule
import model.card.Cards.{Card, Coverable, PokerCard}
import model.logic.{CactusTurnPhase, TurnPhase}
import model.player.Players.{CactusPlayer, Player}
import mvc.TutorialMVC
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, ScrollPane}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox, Pane, Priority, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text
import view.Utils.turnPhaseDescription
import view.ViewDSL.{Button as ButtonElement, Card as CardElement, Text as TextElement, *}
import view.ViewPosition
import view.module.cactus.AppPane.*
import view.module.cactus.CardsPane.*
import view.module.cactus.PlayersPane.*
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

  private val leftPosition: Int                                   = 0
  private val topLeftCorner: ViewPosition                         = ViewPosition(topPosition, leftPosition)
  private def paneCenter: ViewPosition                            = ViewPosition(paneWidth, paneHeight) / 2
  private val humanPlayer: Player                                 = controller.humanPlayer
  private val currentPlayerProperty: ObjectProperty[CactusPlayer] = ObjectProperty(controller.currentPlayer)
  private val pileCardsProperty: ObjectProperty[Option[PokerCard & Coverable]] = ObjectProperty(controller.pilesHead)
  private val playerCardsProperty: ObjectProperty[List[Card]] = ObjectProperty(
    currentPlayerProperty.value.cards
  )

  private val turnIndicators: List[Circle] = controller.players.indices.toList.map(_ =>
    new Circle:
      radius = PlayersPane.turnIndicatorRadius
  )

  private val playersPanes: List[PlayerPane] = controller.players.zipWithIndex
    .map((player, index) => new PlayerPane(player, calculatePlayerPosition(index)))

  override def pane: Pane = new Pane()
    .tall(paneHeight)
    .long(paneWidth)
    .colored(AppPane.mainPaneColor)
    .containing(playersPanes.map(_.pane))
    .containing(List(new TableCenterPane().pane))

  /** Updates the graphic of the discard pile. */
  def updateDiscardPile(): Unit = pileCardsProperty.setValue(controller.pilesHead)

  /** Updates the current player in the view. */
  def updateCurrentPlayer(): Unit = currentPlayerProperty.value = controller.currentPlayer

  def updatePlayersCards(player: Player): Unit = playerCardsProperty.setValue(player.cards)
  private def calculatePlayerPosition(i: Int): ViewPosition =
    val theta: Double = 2 * Math.PI / controller.players.length
    val x: Int =
      (Math.sin(theta * i) * dispositionRadius * horizontalRatio).toInt + paneCenter.x - PlayersPane.paneWidth / 2
    val y: Int = (Math.cos(theta * i) * dispositionRadius).toInt + paneCenter.y - PlayersPane.paneHeight / 2
    ViewPosition(x, y)

  currentPlayerProperty.onChange((_, oldValue, newValue) =>
    // Update previous player's turn indicator
    val oldPlayerIndex: Int = controller.players.indexOf(oldValue)
    turnIndicators(oldPlayerIndex).fill = PlayersPane.turnIndicatorFillColorDisabled
    turnIndicators(oldPlayerIndex).stroke = PlayersPane.turnIndicatorStrokeColorDisabled
    playersPanes(oldPlayerIndex).updateTurnIndicator()

    // update new player's turn indicator
    val newPlayerIndex: Int = controller.players.indexOf(newValue)
    turnIndicators(newPlayerIndex).fill = PlayersPane.turnIndicatorFillColorEnabled
    turnIndicators(newPlayerIndex).stroke = PlayersPane.turnIndicatorStrokeColorEnabled
    playersPanes(newPlayerIndex).updateTurnIndicator()
  )

  /**
   * Representation of the player's pane.
   * This consists in a turn indicator, a label with the name of the player and a list of cards of the player.
   * @param player represented by the pane.
   * @param position of the pane.
   */
  private class PlayerPane(player: Player, override val position: ViewPosition)
      extends ScalaFXPane: // with PlayersPane:
    override def paneWidth: Int  = PlayersPane.paneWidth
    override def paneHeight: Int = PlayersPane.paneHeight

    override def pane: Pane = new Pane()
      .at(position)
      .containing(header)
      .containing(cardsContainer)(playerCardsProperty.value.nonEmpty)
      .reacting(_ =>
        val playerIndex = controller.players.indexOf(player)
        controller.currentPhase match
          case CactusTurnPhase.AceEffect if playerIndex != 0 =>
            controller.handlePlayerInput(playerIndex)
          case _ => ()
      )

    playerCardsProperty.onChange((_, _, _) => updatePlayerCards())

    /** Updates the turn indicator of the player. */
    def updateTurnIndicator(): Unit = header.left = turnIndicatorContainer(
      turnIndicators(controller.players.indexOf(player))
    )

    private def cardsNumberText: VBox = new VBox:
      children = List(
        TextElement
          .telling(player.cards.size.toString)
          .whenHovered("Number of cards in player's hand")
          .small
      )
      alignment = Pos.CenterRight

    private def turnIndicatorContainer(turnIndicator: Node): Pane =
      val pane = new VBox:
        alignment = Pos.Center
        children = List(turnIndicator)

      pane.whenHovered(currentPlayerProperty.value match
        case p if p.isEqualsTo(player) => s"${player.name}'s turn"
        case _                         => s"Not ${player.name}'s turn"
      )

    private def playerHand: VBox = new VBox()
      .colored(AppPane.mainPaneColor)
      .long((CardsPane.paneWidth + CardsPane.margin) * PlayersPane.maxCardsPerLine)
      .containing(
        player.cards
          .map(card => CardElement showing card reacting (_ => cardClickHandler(card)))
          .grouped(PlayersPane.maxCardsPerLine)
          .toList
          .map(pack => new HBox() containing pack)
      )

    private def updatePlayerCards(): Unit =
      cardsContainer.content = playerHand
      header.right = cardsNumberText

    private def cardClickHandler(card: Card): Unit =
      if player.isEqualsTo(humanPlayer) && controller.currentPhase != CactusTurnPhase.AceEffect then
        val index: Int = player.cards.indexOf(card)
        controller.handlePlayerInput(index)
        updatePlayerCards()
        updateDiscardPile()

    private def cardPosition(i: Int): ViewPosition = ViewPosition(
      (i % PlayersPane.maxCardsPerLine) * CardsPane.paneWidth + CardsPane.margin,
      (i / PlayersPane.maxCardsPerLine) * CardsPane.paneHeight
    )

    private val header: BorderPane =
      val nameText: HBox = new HBox:
        padding = Insets(PlayersPane.padding)
        children = List(TextElement telling player.name)

      val turnIndicator: Circle = turnIndicators(controller.players.indexOf(player))
      turnIndicator.setCenterX(position.x + PlayersPane.turnIndicatorRadius)
      turnIndicator.setCenterX(position.y - PlayersPane.turnIndicatorRadius)
      turnIndicator.fill =
        if currentPlayerProperty.value.isEqualsTo(player)
        then PlayersPane.turnIndicatorFillColorEnabled
        else PlayersPane.turnIndicatorFillColorDisabled
      turnIndicator.stroke =
        if currentPlayerProperty.value.isEqualsTo(player)
        then PlayersPane.turnIndicatorStrokeColorEnabled
        else PlayersPane.turnIndicatorStrokeColorDisabled

      new BorderPane():
        prefWidth = paneWidth
        left = turnIndicatorContainer(turnIndicator)
        center = nameText
        right = cardsNumberText

    private val cardsContainer: ScrollPane = new ScrollPane()
      .at((leftPosition, topPosition + normalFontSize * 2))
      .long((CardsPane.paneWidth + CardsPane.margin) * PlayersPane.maxCardsPerLine)
      .tallAtMost(CardsPane.paneHeight * PlayersPane.maxCardsLines)
      .colored(Color.Transparent)
      .containing(playerHand)
      .withoutVBar

  /**
   * Representation of the center of the table.
   * It consists in a deck and a discard pile.
   */
  private class TableCenterPane extends ScalaFXPane:

    override def paneWidth: Int = CardsPane.paneWidth * 2

    override def paneHeight: Int = CardsPane.paneHeight

    override def position: ViewPosition = paneCenter - ViewPosition(paneWidth, paneHeight) / 2

    override def pane: Pane = new Pane()
      .at(position)
      .long(paneWidth)
      .tall(paneHeight)
      .containing(deckPane)
      .containing(pilePane)

    pileCardsProperty.onChange((_, _, newValue) =>
      pilePane.children.clear()
      pilePane.children.add(CardElement at topLeftCorner showing newValue)
    )

    private val deckPane: Pane = new Pane()
      .at((leftPosition, topPosition))
      .containing(CardElement at topLeftCorner covered)
      .reacting(_ => controller.draw(true))
      .whenHovered("Deck")

    private val pilePane: Pane = new Pane()
      .at((CardsPane.paneWidth, topPosition))
      .containing(CardElement at topLeftCorner showing controller.pilesHead)
      .reacting(_ =>
        if controller.pilesHead.isDefined then
          controller.draw(false)
          updateDiscardPile()
      )
      .whenHovered("Discard pile")

/**
 * Representation of the lateral portion of the view.
 * Contains the buttons to continue the game and to call "Cactus".
 * @param controller of the application.
 */
class AsidePane(controller: CactusController) extends ScalaFXPane:
  import view.module.cactus.AppPane.AsidePaneModule.*

  private val turnPhaseProperty: ObjectProperty[TurnPhase] = ObjectProperty(controller.currentPhase)
  turnPhaseProperty.onChange((_, _, newValue) =>
    updatePane()
    newValue match
      case CactusTurnPhase.CallCactus => cactusButton.setDisable(false)
      case _                          => cactusButton.setDisable(true)
  )

  override def paneWidth: Int = AppPane.asidePaneWidth

  override def paneHeight: Int = asidePaneHeight

  override def position: ViewPosition = ViewPosition(mainPaneWidth, 0)

  /** Updates the turn phase in the view. */
  def updateViewTurnPhase(): Unit = turnPhaseProperty.setValue(controller.currentPhase)

  private val nextButton: Button = ButtonElement saying continueButtonText doing (_ => controller.continue())
  private val cactusButton: Button =
    val button: Button = ButtonElement saying cactusButtonText doing (_ => controller.callCactus())
    button.setDisable(true)
    button
  private val tutorialButton: Button = ButtonElement saying "tutorial" doing (_ => controller.showTutorial())

  private def phaseText: VBox = new VBox()
    .containing(TextElement telling AppPane.AsidePaneModule.phaseText bold)
    .containing(TextElement telling turnPhaseDescription(turnPhaseProperty.value).name wrapped)
  phaseText.setAlignment(Pos.BaselineLeft)

  private def phaseDescription: VBox = new VBox()
    .containing(TextElement telling phaseDescriptionText bold)
    .containing(TextElement telling turnPhaseDescription(turnPhaseProperty.value).description wrapped)

  private val phaseContainer: VBox = new VBox()
    .containing(tutorialButton)
    .containing(phaseText)
    .containing(phaseDescription)
  phaseContainer.spacing = AppPane.spacing

  private val buttonsContainer: VBox = new VBox()
    .containing(nextButton)
    .containing(cactusButton)
  buttonsContainer.spacing = AppPane.spacing

  private val _pane: BorderPane = new BorderPane()
    .at(position)
    .tall(paneHeight)
    .colored(Gradient.Vertical)(List(AppPane.asidePaneColor2, AppPane.asidePaneColor))
    .^(phaseContainer)
    .v(buttonsContainer)
  _pane.padding = Insets(AppPane.spacing)
  VBox.setVgrow(_pane, Priority.Always)

  private def updatePane(): Unit =
    phaseContainer.children.clear()
    phaseContainer
      .containing(tutorialButton)
      .containing(phaseText)
      .containing(phaseDescription)
    _pane.top = phaseContainer

  override def pane: Pane = _pane
