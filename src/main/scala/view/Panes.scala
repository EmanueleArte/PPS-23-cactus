package view

import scalafx.scene.layout.Pane
import control.module.CactusControllerModule
import control.module.CactusControllerModule.CactusController
import model.card.CardBuilder.PokerDSL
import model.card.Cards.{Card, PokerCard}
import model.deck.Drawable
import model.player.Players.{CactusPlayer, Player}
import org.scalactic.TypeCheckedTripleEquals.convertToCheckingEqualizer
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.{Font, Text}

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
 * @param context of the application, containing the controller.
 */
class MainPane(controller: CactusController) extends ScalaFXPane:
  override def paneWidth: Int         = Panes.mainPaneWidth
  override def paneHeight: Int        = Panes.mainPaneHeight
  override def position: ViewPosition = topLeftCorner
  private def dispositionRadius: Int  = paneHeight / 2 - PlayersPane.paneHeight / 2
  private def horizontalRatio: Double = paneWidth.toDouble / paneHeight.toDouble

  private val topPosition: Int = 0

  private val leftPosition: Int           = 0
  private val topLeftCorner: ViewPosition = ViewPosition(topPosition, leftPosition)
  private def center: ViewPosition        = ViewPosition(paneWidth, paneHeight) / 2
  private val currentPlayer: Player       = controller.players(0)
  // TODO: check only the first card of the pile, maybe with an `Option`
  private val pileCardsProperty: ObjectProperty[Option[PokerCard]] = ObjectProperty(controller.pilesHead)
  private val playerCardsProperty: ObjectProperty[List[Card]] = ObjectProperty(
    currentPlayer.cards // .map(card => PokerCard(card.value.##, card.suit))
  )

  override def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    prefWidth = paneWidth
    prefHeight = paneHeight
    style = s"-fx-background-color: ${Panes.mainPaneColor};"
    children = controller.players.zipWithIndex
      .map((player, index) => new PlayerPane(player, calculatePlayerPosition(index)).pane)
      ++ List(new TableCenterPane().pane)
  private def updateDiscardPile(): Unit  = pileCardsProperty.setValue(controller.pilesHead)
  private def updatePlayersCards(): Unit = playerCardsProperty.setValue(currentPlayer.cards)
  private def calculatePlayerPosition(i: Int): ViewPosition =
    val theta: Double = 2 * Math.PI / controller.players.length
    val x: Int =
      (Math.sin(theta * i) * dispositionRadius * horizontalRatio).toInt + center.x - PlayersPane.paneWidth / 2
    val y: Int = (Math.cos(theta * i) * dispositionRadius).toInt + center.y - PlayersPane.paneHeight / 2
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

    override def pane: Pane = new Pane:
      layoutX = position.x
      layoutY = position.y
      children = List(
        new VBox:
          children = List(header, cardsContainer)
      )

    private val header: HBox =
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

    private val cardsContainer: Pane = new Pane:
      layoutX = leftPosition
      layoutY = topPosition
      children = player.buildCardsPane

    private def cardPosition(i: Int): ViewPosition = ViewPosition(
      (i % PlayersPane.maxCardsPerLine) * CardsPane.paneWidth + CardsPane.margin,
      (i / PlayersPane.maxCardsPerLine) * CardsPane.paneHeight
    )

    private def updatePlayerCards(): Unit =
      cardsContainer.children.clear()
      player.buildCardsPane.foreach(pane => cardsContainer.children.add(pane))

    extension (player: Player)
      private def buildCardsPane: List[Pane] =
        player.cards.zipWithIndex.map((card, index) => new PlayerCardPane(card, cardPosition(index)).pane)

    /**
     * Representation of the player's card.
     * @param card represented by the pane.
     * @param position of the pane.
     */
    private class PlayerCardPane(card: Card, override val position: ViewPosition) extends ScalaFXPane with CardPane:
      override def paneWidth: Int = CardsPane.paneWidth

      override def paneHeight: Int = CardsPane.paneHeight

      override def pane: Pane = new Pane:
        layoutX = position.x
        layoutY = position.y
        prefWidth = paneWidth
        prefHeight = paneHeight
        children = List(imageView)
        onMouseClicked = _ =>
          if player.isEqualsTo(currentPlayer) then
            val index: Int = player.cards.indexOf(card)
            controller.discard(index) // playerDiscards(player, index)
            updatePlayerCards()
            updateDiscardPile()
          else ()
      override def filename: String = s"${card.suit.toString.toLowerCase()}_${card.value}"
      override def imageView: ImageView = new ImageView(image):
        fitWidth = CardsPane.paneWidth - CardsPane.margin
        preserveRatio = true

      private def image: Image = new Image(
        getClass.getResourceAsStream(CardsPane.frontsFolderPath + s"/${filename}.png")
      )

  /**
   * Representation of a generic card of the game.
   * @param card represented by the pane.
   * @param position of the pane.
   * @param covered `true` if the card is covered, `false` if the front is visible.
   */
  private class BasicCardPane(card: Option[PokerCard], override val position: ViewPosition, covered: Boolean)
      extends ScalaFXPane
      with CardPane:
    override def paneWidth: Int = CardsPane.paneWidth

    override def paneHeight: Int = CardsPane.paneHeight

    override def pane: Pane = new Pane:
      layoutX = position.x
      layoutY = position.y
      prefWidth = paneWidth
      prefHeight = paneHeight
      children = List(if covered || card.isDefined then imageView else rectangle)

    override def filename: String = s"/${card.get.suit.toString.toLowerCase()}_${card.get.value.toString}.png"

    override def imageView: ImageView =
      new ImageView(image):
        fitWidth = CardsPane.paneWidth - CardsPane.margin
        preserveRatio = true

    private def image: Image =
      new Image(
        if covered then CardsPane.backsFolderPath + CardsPane.defaultBack else CardsPane.frontsFolderPath + filename
      )

    private def rectangle: Rectangle = new Rectangle:
      width = CardsPane.paneWidth
      height = CardsPane.paneHeight
      fill = CardsPane.placeholderColor

  /**
   * Representation of the center of the table.
   * It consists in a deck and a discard pile.
   */
  private class TableCenterPane() extends ScalaFXPane:
    pileCardsProperty.onChange((_, oldValue, newValue) =>
      pilePane.children.clear()
      pilePane.children.add(new BasicCardPane(newValue, topLeftCorner, false).pane)
    )

    override def paneWidth: Int = CardsPane.paneWidth * 2

    override def paneHeight: Int = CardsPane.paneHeight

    override def position: ViewPosition = center - ViewPosition(paneWidth, paneHeight) / 2

    override def pane: Pane = new Pane:
      layoutX = position.x
      layoutY = position.y
      prefWidth = paneWidth
      prefHeight = paneHeight
      children = List(deckPane, pilePane)

    private val deckPane: Pane = new Pane:
      layoutX = leftPosition
      layoutY = topPosition
      children = List(new BasicCardPane(Option.empty, topLeftCorner, true).pane)
      onMouseClicked = _ =>
        controller.draw(true) // (controller.currentPlayer)
        updatePlayersCards()

    private val pilePane: Pane = new Pane:
      layoutX = CardsPane.paneWidth
      layoutY = topPosition
      children = List(new BasicCardPane(controller.pilesHead, topLeftCorner, false).pane)
      onMouseClicked = _ =>
        controller.draw(false) // (controller.currentPlayer)
        updatePlayersCards()
        updateDiscardPile()

/**
 * Representation of the lateral portion of the view.
 * Contains the buttons to continue the game and to call "Cactus".
 * @param context of the application, containing the controller.
 */
class AsidePane(controller: CactusController) extends ScalaFXPane:
  override def paneWidth: Int = Panes.asidePaneWidth

  override def paneHeight: Int = Panes.asidePaneHeight

  override def position: ViewPosition = ViewPosition(Panes.mainPaneWidth, 0)

  private val nextButton: Button = new Button:
    text = "Continue"
    layoutX = 0
    layoutY = 0
    prefWidth = 100
    prefHeight = 50
    onAction = _ => controller.continue()

  private val cactusButton: Button = new Button:
    text = "Cactus!"
    layoutX = 0
    layoutY = 100
    prefWidth = 200
    prefHeight = 50
    onAction = _ => println("Cactus")

  override def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    prefWidth = paneWidth
    prefHeight = paneHeight
    style = s"-fx-background-color: ${Panes.asidePaneColor};"
    children = List(nextButton, cactusButton)
