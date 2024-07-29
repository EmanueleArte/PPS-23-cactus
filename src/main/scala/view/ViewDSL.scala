package view

import scalafx.scene.control.{Button, ScrollPane, Tooltip}
import view.Utils.toRgbString
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.input.MouseEvent
import model.card.Cards.{Card, Coverable, PokerCard}
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Pane, Region}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight, Text}
import view.module.cactus.{AppPane, Buttons, CardsPane, PlayersPane}
import view.module.cactus.Text.*

/**
 * DSL for creating view elements in a more agile way.
 */
object ViewDSL:

  /**
   * Creates a new button with some default options, like:
   * - background color.
   * - text color.
   * - border radius.
   * - dimensions.
   * @return new button.
   */
  def Button: Button = new Button:
    prefWidth = Buttons.buttonWidth
    prefHeight = Buttons.buttonHeight
    style = s"-fx-background-color: ${Buttons.buttonBgColor.toRgbString};" +
      s"-fx-text-color: ${Buttons.buttonColor.toRgbString};" +
      s"-fx-border-radius: 3px"
    margin = Insets(10)

  /**
   * Creates a new card's pane with the dimension already set.
   * @return new card's pane.
   */
  def Card: Pane = new Pane().long(CardsPane.paneWidth).tall(CardsPane.paneHeight)

  def Text: Text = new Text():
    font = Font.font(normalFontSize)
    fill = textColor

  extension [T <: Region](node: T)
    
    /**
     * Sets the position of a [[Node]], using a couple of [[Int]].
     * @param position to place the node.
     * @return node with the position set.
     */
    def at(position: (Int, Int)): T =
      node.setLayoutX(position._1.toDouble)
      node.setLayoutY(position._2.toDouble)
      node

    /**
     * Sets the position of a [[Node]], using a [[ViewPosition]].
     * @param position to place the node.
     * @return node with the position set.
     */
    def at(position: ViewPosition): T = node at (position.x, position.y)

    /**
     * Sets the handler to use when the [[Node]] is clicked.
     * @param handler to use when node is clicked.
     * @return node with the handler set.
     */
    def reacting(handler: EventHandler[MouseEvent]): T =
      node.setOnMouseClicked(handler)
      node

    /**
     * Sets the background color of the [[Node]].
     * @param color to use as background color.
     * @return node with the background color set.
     */
    def colored(color: Color): T =
      node.style = node.style() + s";-fx-background-color: ${color.toRgbString};"
      node

    /**
     * Sets the preferred width of a [[Pane]].
     * @param width to set for the pane.
     * @return pane with the width set.
     */
    def long(width: Double): T =
      node.setPrefWidth(width)
      node

    /**
     * Sets the maximum width of a [[Pane]].
     * @param width to set for the pane.
     * @return pane with the maximum width set.
     */
    def longAtMost(width: Double): T =
      node.setMaxWidth(width)
      node

    /**
     * Sets the minimum width of a [[Pane]].
     * @param width to set for the pane.
     * @return pane with the minimum width set.
     */
    def longAtLeast(width: Double): T =
      node.setMinWidth(width)
      node

    /**
     * Sets the preferred height of a [[Pane]].
     * @param height to set for the node.
     * @return node with the height set.
     */
    def tall(height: Double): T =
      node.setPrefHeight(height)
      node

    /**
     * Sets the maximum height of a [[Pane]].
     * @param height to set for the node.
     * @return node with the maximum height set.
     */
    def tallAtMost(height: Double): T =
      node.setMaxHeight(height)
      node

    /**
     * Sets the minimum height of a [[Pane]].
     * @param height to set for the node.
     * @return node with the minimum height set.
     */
    def tallAtLeast(height: Double): T =
      node.setMinHeight(height)
      node

    /**
     * Sets both the width and height of a [[Pane]].
     * @param dimensions to set for the node, provided as a couple of [[Int]].
     * @return node with the dimensions set.
     */
    def of(dimensions: (Int, Int)): T = node.long(dimensions._1).tall(dimensions._2)

  extension [T <: Pane] (pane: T)
    /**
     * Sets a child for a [[Pane]].
     * @param element to set as a child.
     * @return pane with the element set as a child.
     */
    def containing(element: Node): T =
      pane.children.add(element)
      pane

    /**
     * Sets elements as children for a [[Pane]].
     * @param elements to set as children.
     * @return pane with the elements set as children.
     */
    def containing(elements: Seq[Node]): T =
      elements.foreach(pane.children.add(_))
      pane

    /**
     * Sets a child for a [[Pane]].
     * @param element to set as a child.
     * @param condition to be passed in order to add the element.
     * @return pane with the element set as a child.
     */
    def containing(element: Node)(condition: Boolean): T =
      if condition then pane.children.add(element)
      pane

    /**
     * Sets as child the image of a covered card.
     * @return pane with covered card image set as child.
     */
    def covered: T =
      val filename: String = CardsPane.backsFolderPath + CardsPane.defaultBack
      pane.containing(imageView(filename))

    /**
     * Sets as child the image of the given card.
     * @param card to display in the pane.
     * @return pane displaying the card.
     */
    def showing(card: Card & Coverable): Pane =
      if pane.children.isEmpty && !card.isCovered then
        val filename: String = CardsPane.frontsFolderPath + s"/${card.suit.toString.toLowerCase()}_${card.value}.png"
        pane.children.add(imageView(filename))
      else if card.isCovered then 
        val filename: String = CardsPane.backsFolderPath + CardsPane.defaultBack
        pane.children.add(imageView(filename))
      pane

    /**
     * Sets as child the image of the given [[Option]] containing a card.
     * @param cardOption containing the card to display in the pane.
     * @return pane displaying the card.
     */
    def showing(cardOption: Option[Card & Coverable]): Pane = cardOption match
      case Some(card) => pane showing card
      case _ if pane.children.isEmpty =>
        pane.children.add(placeholder)
        pane
      case _ => pane

  extension [T <: ScrollPane] (pane: T)
    /**
     * Adds the content for a [[ScrollPane]].
     * @param element to add to the pane.
     * @return pane with the element as content.
     */
    def containing(element: Node): T =
      pane.setContent(element)
      pane

    /**
     * Adds the content for a [[ScrollPane]].
     *
     * @param element to add to the pane.
     * @param condition to be passed in order to add the element.
     * @return pane with the element as content.
     */
    def containing(element: Node)(condition: Boolean): T =
      if condition then pane.setContent(element)
      pane

    /**
     * Hides the vertical scrollbar.
     * @return pane with the vertical scrllbar hidden.
     */
    def withoutVBar: T =
      pane.setHbarPolicy(ScrollBarPolicy.NEVER)
      pane

  extension [T <: Button] (button: T)

    /**
     * Sets the text of a [[Button]].
     * @param text to display.
     * @return button with the text set.
     */
    def saying(text: String): T =
      button.setText(text)
      button

    /**
     * Sets the handler to use when the [[Button]] is clicked.
     * @param handler to use when button is clicked.
     * @return button with the handler set.
     */
    def doing(handler: EventHandler[ActionEvent]): T =
      button.setOnAction(handler)
      button

  extension [T <: Text] (text: T)
    /**
     * Sets the text of a [[Text]].
     * @param message to display.
     * @return [[Text]] with the text set.
     */
    def telling(message: String): T =
      text.setText(message)
      text

    /**
     * Display a message when the element is hovered.
     * @param message to display when hovered.
     * @return [[Text]] with the tooltip set.
     */
    def whenHovered(message: String): T =
      val tooltip: Tooltip = new Tooltip(message)
      text.setOnMouseMoved(e => if !tooltip.isShowing then tooltip.show(text, e.getScreenX + 10, e.getScreenY + 10))
      text.onMouseExited = _ => if tooltip.isShowing then tooltip.hide()
      text

    /**
     * Sets the font size at small size.
     * @return [[Text]] with the font size updated.
     */
    def small: T =
      text.setFont(Font.font(smallFontSize))
      text

    /**
     * Sets the font size at default size.
     * @return [[Text]] with the font size updated.
     */
    def normal: T =
      text.setFont(Font.font(normalFontSize))
      text

    /**
     * Sets the font size at big size.
     * @return [[Text]] with the font size updated.
     */
    def big: T =
      text.setFont(Font.font(bigFontSize))
      text

    /**
     * Makes the text wrappable.
     * @return [[Text]] wrappable.
     */
    def wrapped: T =
      text.wrappingWidth = AppPane.asidePaneWidth
      text

    /**
     * Sets text weight to bold.
     * @return [[Text]] in bold.
     */
    def bold: T =
      text.font = Font.font(text.font.value.getFamily, FontWeight.Bold, text.font.value.getSize)
      text

  private def imageView(filepath: String): ImageView = new ImageView(
    new Image(getClass.getResourceAsStream(filepath))
  ):
    fitWidth = CardsPane.paneWidth - CardsPane.margin
    preserveRatio = true

  private def placeholder: Rectangle = new Rectangle:
    width = CardsPane.paneWidth
    height = CardsPane.paneHeight
    fill = CardsPane.placeholderColor