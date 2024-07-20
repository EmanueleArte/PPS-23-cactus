package view

import scalafx.scene.control.Button
import view.Utils.toRgbString
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.card.Cards.Card
import scalafx.scene.Node
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

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

  /**
   * Creates a new card's pane with the dimension already set.
   * @return new card's pane.
   */
  def Card: Pane = new Pane().long(CardsPane.paneWidth).tall(CardsPane.paneHeight)

  extension [T <: Node] (node: T)

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

  extension [T <: Pane] (pane: T)

    /**
     * Sets the width of a [[Pane]].
     * @param width to set for the pane.
     * @return pane with the width set.
     */
    def long(width: Int): T =
      pane.setPrefWidth(width)
      pane

    /**
     * Sets the height of a [[Pane]].
     * @param height to set for the pane.
     * @return pane with the height set.
     */
    def tall(height: Int): T =
      pane.setPrefHeight(height)
      pane

    /**
     * Sets both the width and height of a [[Pane]].
     * @param dimensions to set for the pane, provided as a couple of [[Int]].
     * @return pane with the dimensions set.
     */
    def of(dimensions: (Int, Int)): T = pane.long(dimensions._1).tall(dimensions._2)

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
    def showing(card: Card): Pane =
      if pane.children.isEmpty then
        val filename: String = CardsPane.frontsFolderPath + s"/${card.suit.toString.toLowerCase()}_${card.value}.png"
        pane.children.add(imageView(filename))
      pane

    /**
     * Sets as child the image of the given [[Option]] containing a card.
     * @param cardOption containing the card to display in the pane.
     * @return pane displaying the card.
     */
    def showing(cardOption: Option[Card]): Pane = cardOption match
      case Some(card) => pane showing card
      case _ if pane.children.isEmpty => pane.children.add(placeholder); pane
      case _ => pane

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

  private def imageView(filepath: String): ImageView = new ImageView(
    new Image(getClass.getResourceAsStream(filepath))
  ):
    fitWidth = CardsPane.paneWidth - CardsPane.margin
    preserveRatio = true

  private def placeholder: Rectangle = new Rectangle:
    width = CardsPane.paneWidth
    height = CardsPane.paneHeight
    fill = CardsPane.placeholderColor