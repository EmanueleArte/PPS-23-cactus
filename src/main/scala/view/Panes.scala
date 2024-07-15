package view

import model.card.Cards.{Card, PokerCard}
import model.deck.Drawable
import player.Players.CactusPlayer
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

/** Contains the basic parameters for the application's panes. */
object Panes:
  /**
   * Width of the application's window.
   * @return width of the application's window.
   */
  def windowWidth: Int = 1200

  /**
   * Height of the application's window.
   * @return height of the application's window.
   */
  def windowHeight: Int = 800

  /**
   * Width of the main pane.
   * @return width of the main pane.
   */
  def mainPaneWidth: Int = 1000

  /**
   * Height of the main pane.
   * @return height of the main pane.
   */
  def mainPaneHeight: Int = windowHeight

  /**
   * Width of the side pane.
   * @return width of the side pane.
   */
  def asidePaneWidth: Int = windowWidth - mainPaneWidth

  /**
   * Height of the side pane.
   * @return height of the side pane.
   */
  def asidePaneHeight: Int = windowHeight

/** Contains the basic parameters for the player's panes. */
object PlayersPane:
  /**
   * Width of the player's pane.
   * @return width of the player's pane.
   */
  def paneWidth: Int = CardsPane.paneWidth * maxCardsPerLine

  /**
   * Height of the player's pane.
   * @return height of the player's pane.
   */
  def paneHeight: Int = CardsPane.paneHeight * maxCardsLines + 18

  /**
   * Font size of the texts in the pane.
   * @return font size.
   */
  def fontSize: Int = 18

  /**
   * Radius of the circle representing the turn indicator.
   * @return radius of turn indicator.
   */
  def turnIndicatorRadius: Int = 5

  /**
   * Color of the turn indicator.
   * @return color of the turn indicator.
   */
  def turnIndicatorColor: Color = Color.Red

  /**
   * Maximum number of cards disposable on a line.
   * @return number of cards per line.
   */
  def maxCardsPerLine: Int = 4

  /**
   * Maximum number of lines.
   * @return number of lines.
   */
  def maxCardsLines: Int = 3

/** Contains the basic parameters for the card's panes. */
object CardsPane:
  private def aspectRatio: Double = 3.0 / 2.0

  /**
   * Width of the card's pane.
   * @return width of the card's pane.
   */
  def paneWidth: Int = 50 + margin

  /**
   * Height of the card's pane.
   * @return height of the card's pane.
   */
  def paneHeight: Int = (paneWidth.toDouble * aspectRatio).toInt + margin

  /**
   * Space between each card, both horizontal and vertical.
   * @return margin between the cards.
   */
  def margin: Int = 5

  private def cardsFolderPath: String = "/cards"

  /**
   * Path for the folder of card's backs.
   * @return path for card's backs folder.
   */
  def backsFolderPath: String = cardsFolderPath + "/backs"

  /**
   * Path for the folder of card's fronts.
   * @return path for card's fronts folder.
   */
  def frontsFolderPath: String = cardsFolderPath + "/fronts"

  /**
   * Default back for the cards.
   * @return filename of the default back.
   */
  def defaultBack: String = "/red.png"

  /**
   * Placeholder color to put when a card is not present.
   * @return placeholder color.
   */
  def placeholderColor: Color = Color.SlateBlue

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
class MainPane(context: ControllerModule.Provider) extends ScalaFXPane:
  override def paneWidth: Int                                    = Panes.mainPaneWidth
  override def paneHeight: Int                                   = Panes.mainPaneHeight
  override def position: ViewPosition                            = ViewPosition(0, 0)
  private def dispositionRadius: Int                             = paneHeight / 2 - PlayersPane.paneHeight / 2
  private def horizontalRatio: Double                            = paneWidth.toDouble / paneHeight.toDouble
  private def center: ViewPosition                               = (ViewPosition(paneWidth, paneHeight) / 2)
  private val pileCardsProperty: ObjectProperty[List[PokerCard]] = ObjectProperty(context.controller.pile.cards)
  private val playerCardsProperty: ObjectProperty[List[PokerCard]] = ObjectProperty(
    context.controller.currentPlayer.cards
  )

  override def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    prefWidth = paneWidth
    prefHeight = paneHeight
    style = "-fx-background-color: lime;"
    children = context.controller.players.zipWithIndex
      .map((player, index) => new PlayerPane(player, calculatePlayerPosition(index)).pane)
      ++ List(new TableCenterPane().pane)
  private def updateDiscardPile(): Unit  = pileCardsProperty.setValue(context.controller.pile.cards)
  private def updatePlayersCards(): Unit = playerCardsProperty.setValue(context.controller.currentPlayer.cards)
  private def calculatePlayerPosition(i: Int): ViewPosition =
    val theta: Double = 2 * Math.PI / context.controller.players.length
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
  private class PlayerPane(player: CactusPlayer, override val position: ViewPosition)
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
      layoutX = 0
      layoutY = 0
      children = player.buildCardsPane

    private def cardPosition(i: Int): ViewPosition = ViewPosition(
      (i % PlayersPane.maxCardsPerLine) * CardsPane.paneWidth + CardsPane.margin,
      (i / PlayersPane.maxCardsPerLine) * CardsPane.paneHeight
    )

    private def updatePlayerCards(): Unit =
      cardsContainer.children.clear()
      player.buildCardsPane.foreach(pane => cardsContainer.children.add(pane))

    extension (player: CactusPlayer)
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
          // TODO: use player.isHuman or something similar
          val index: Int = player.cards.indexOf(card)
          context.controller.playerDiscards(player, index)
          updatePlayerCards()
          updateDiscardPile()
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
      children = List(if card.isDefined then imageView else rectangle)

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
      pilePane.children.add(new BasicCardPane(newValue.headOption, ViewPosition(0, 0), false).pane)
    )

    override def paneWidth: Int = CardsPane.paneWidth * 2

    override def paneHeight: Int = CardsPane.paneHeight

    override def position: ViewPosition = center - ViewPosition(paneWidth / 2, paneHeight / 2)

    override def pane: Pane = new Pane:
      layoutX = position.x
      layoutY = position.y
      prefWidth = paneWidth
      prefHeight = paneHeight
      children = List(deckPane, pilePane)

    private val deckPane: Pane = new Pane:
      layoutX = 0
      layoutY = 0
      children = List(new BasicCardPane(context.controller.deck.cards.headOption, ViewPosition(0, 0), true).pane)
      onMouseClicked = _ =>
        context.controller.playerDrawFromDeck(context.controller.currentPlayer)
        updatePlayersCards()

    private val pilePane: Pane = new Pane:
      layoutX = CardsPane.paneWidth
      layoutY = 0
      children = List(new BasicCardPane(context.controller.pile.cards.headOption, ViewPosition(0, 0), false).pane)
      onMouseClicked = _ =>
        context.controller.playerDrawFromPile(context.controller.currentPlayer)
        updatePlayersCards()
        updateDiscardPile()

/**
 * Representation of the lateral portion of the view.
 * Contains the buttons to continue the game and to call "Cactus".
 * @param context of the application, containing the controller.
 */
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
