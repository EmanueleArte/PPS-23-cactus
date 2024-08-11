package view

import scalafx.scene.control.{Button, ComboBox, Label, ScrollPane, Tooltip}
import view.ViewUtils.{toRgbString, tutorialHPadding}
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.input.MouseEvent
import model.card.Cards.{Card, Coverable}
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, BackgroundFill, BorderPane, HBox, Pane, Region, VBox}
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight, Text}
import view.module.cactus.{AppPane, Buttons, CardsPane}
import view.module.cactus.Text.*

import scala.annotation.targetName

/** DSL for creating view elements in a more agile way. */
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

  /**
   * Creates a new text with some default options, like:
   * - font size.
   * - text color.
   * @return new text.
   */
  def Text: Text = new Text():
    font = Font.font(normalFontSize)
    fill = textColor

  /**
   * Creates a new label with some default options, like:
   * - font size.
   * - text color.
   * @return new label.
   */
  def Label: Label = new Label():
    font = Font.font(normalFontSize)
    textFill = textColor

  /**
   * Creates a new [[ComboBox]].
   * @tparam A type of the items in the [[ComboBox]].
   * @return new [[ComboBox]].
   */
  def ComboBox[A]: ComboBox[A] = new ComboBox[A]():
    style = s"-fx-font-size: $smallFontSize;" +
      s"-fx-background-color: ${Buttons.buttonBgColor.toRgbString};" +
      s"-fx-text-color: ${Buttons.buttonColor.toRgbString};" +
      s"-fx-border-radius: 3px"

  /** Enumerator containing the types of gradients. */
  enum Gradient:
    case Vertical, Horizontal

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
     * Sets the background color of the [[Node]], using a linear gradient.
     * @param alignment of the linear gradient.
     * @param colors [[List]] of colors of the gradient.
     * @return node with the background color set.
     */
    def colored(alignment: Gradient)(colors: List[Color]): T =
      val start: ViewPosition = ViewPosition(0, 0)
      val end: ViewPosition = alignment match
        case Gradient.Horizontal => ViewPosition(1, 0)
        case Gradient.Vertical => ViewPosition(0, 1)

      node.setBackground(new Background(Array(
        new BackgroundFill(
          new LinearGradient(
            startX = start.x,
            startY = start.y,
            endX = end.x,
            endY = end.y,
            proportional = true,
            stops = Stops(colors: _*)
          ),
          null, null
        )
      )))
      node

    /**
     * Sets the preferred width of a [[Pane]].
     *
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

    /**
     * Display a message when the element is hovered.
     *
     * @param message to display when hovered.
     * @return [[Text]] with the tooltip set.
     */
    def whenHovered(message: String): T =
      val tooltip: Tooltip = new Tooltip(message)
      node.setOnMouseMoved(e => if !tooltip.isShowing then tooltip.show(node, e.getScreenX + 10, e.getScreenY + 10))
      node.onMouseExited = _ => if tooltip.isShowing then tooltip.hide()
      node
  
  extension [T <: Pane](pane: T)
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

    /**
     * Sets the margin of a [[Pane]].
     * @param margin to set.
     * @return pane with the margin set.
     */
    def withMargin(margin: Insets): T =
      pane.setPadding(margin)
      pane

  extension [T <: ScrollPane](pane: T)
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

  extension [T <: VBox](pane: T)
    /**
     * Sets the alignment of a [[VBox]].
     * @param alignment to set for the pane.
     * @return pane with the alignment set.
     */
    def aligned(alignment: Pos): T =
      pane.setAlignment(alignment)
      pane

    /**
     * Sets the spacing between elements of a [[VBox]].
     * @param spacing to set between elements.
     * @return pane with the spacing set.
     */
    def spaced(spacing: Double): T =
      pane.setSpacing(spacing)
      pane

  extension [T <: HBox](pane: T)
    /**
     * Sets the alignment of a [[HBox]].
     * @param alignment to set for the pane.
     * @return pane with the alignment set.
     */
    def aligned(alignment: Pos): T =
      pane.setAlignment(alignment)
      pane

    /**
     * Sets the spacing between elements of a [[HBox]].
     * @param spacing to set between elements.
     * @return pane with the spacing set.
     */
    def spaced(spacing: Double): T =
      pane.setSpacing(spacing)
      pane

  extension [T <: BorderPane](pane: T)
    /**
     * Sets the element in the right position of the [[BorderPane]].
     *
     * @param element to add in the pane.
     * @return pane with the child set.
     */
    @targetName("right")
    def -->(element: Node): T =
      pane.right = element
      pane

    /**
     * Sets the element in the left position of the [[BorderPane]].
     *
     * @param element to add in the pane.
     * @return pane with the child set.
     */
    @targetName("left")
    def <--(element: Node): T =
      pane.left = element
      pane

    /**
     * Sets the element in the top position of the [[BorderPane]].
     *
     * @param element to add in the pane.
     * @return pane with the child set.
     */
    @targetName("top")
    def ^(element: Node): T =
      pane.top = element
      pane

    /**
     * Sets the element in the bottom position of the [[BorderPane]].
     * @param element to add in the pane.
     * @return pane with the child set.
     */
    def v(element: Node): T =
      pane.bottom = element
      pane

  extension [T <: Button](button: T)

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

  extension [T <: Text](text: T)
    /**
     * Sets the text of a [[Text]].
     * @param message to display.
     * @return [[Text]] with the text set.
     */
    def telling(message: String): T =
      text.setText(message)
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
      text.wrappingWidth = AppPane.asidePaneWidth - AppPane.spacing * 2
      text

    /**
     * Sets text weight to bold.
     * @return [[Text]] in bold.
     */
    def bold: T =
      text.font = Font.font(text.font.value.getFamily, FontWeight.Bold, text.font.value.getSize)
      text

    /**
     * Display a message when the element is hovered.
     *
     * @param message to display when hovered.
     * @return [[Text]] with the tooltip set.
     */
    def whenHovered(message: String): T =
      val tooltip: Tooltip = new Tooltip(message)
      text.setOnMouseMoved(e => if !tooltip.isShowing then tooltip.show(text, e.getScreenX + 10, e.getScreenY + 10))
      text.onMouseExited = _ => if tooltip.isShowing then tooltip.hide()
      text
  
  extension [T <: Label](label: T)
    /**
     * Sets the text of a [[Label]].
     * @param message to display.
     * @return [[Label]] with the text set.
     */
    def telling(message: String): T =
      label.setText(message)
      label

    /**
     * Sets the margin of a [[Label]].
     * @param margin to set.
     * @return [[Label]] with the margin set.
     */
    def withMargin(margin: Insets): T =
      label.setPadding(margin)
      label

    /**
     * Sets the font size at big size.
     * @return [[Label]] with the font size updated.
     */
    def veryBig: T =
      label.setFont(Font.font(veryBigFontSize))
      label

    /**
     * Sets the font weight at bold weight.
     * @return [[Label]] with the font weight updated.
     */
    def bold: T =
      label.style = "-fx-font-weight: bold;"
      label

    /**
     * Sets the alignment of a [[Label]].
     * @param alignment to set for the label.
     * @return [[Label]] with the alignment set.
     */
    def aligned(alignment: Pos): T =
      label.setAlignment(alignment)
      label

    /**
     * Makes the text wrappable.
     * @return [[Label]] with wrappable text.
     */
    def textFlow: T =
      label.setWrapText(true)
      label

    /**
     * Sets the width of a [[Label]].
     * @param width to set for the label.
     * @return [[Label]] with the width set.
     */
    def long(width: Double): T =
      label.prefWidth = width
      label

    /**
     * Sets the width of a [[Label]] dynamically.
     * @param width to set for the label.
     * @return [[Label]] with the width set.
     */
    def dynamicLong(width: ReadOnlyDoubleProperty): T =
      label.prefWidth <== width - tutorialHPadding
      label

  extension [A, T <: ComboBox[A]](cbox: T)
    /**
     * Sets the items of a [[ComboBox]].
     * @param source to set as items.
     * @return [[ComboBox]] with the items set.
     */
    def containing(source: IterableOnce[A]): T =
      cbox.items = ObservableBuffer.from(source)
      cbox

    /**
     * Sets the prompt text of a [[ComboBox]].
     * @param text to display as prompt text.
     * @return [[ComboBox]] with the prompt text set.
     */
    def prompt(text: String): T =
      cbox.promptText = text
      cbox

    /**
     * Sets the initial value of a [[ComboBox]].
     * @param value to set as initial value.
     * @return [[ComboBox]] with the initial value set.
     */
    def initialValue(value: A): T =
      cbox.value = value
      cbox

    /**
     * Sets the preferred width of a [[ComboBox]].
     * @param width to set for the [[ComboBox]].
     * @return [[ComboBox]] with the width set.
     */
    def baseWidth(width: Double): T =
      cbox.prefWidth = width
      cbox

    /**
     * Sets the handler to use when the [[ComboBox]] is used.
     * @param handler to use when [[ComboBox]] is clicked.
     * @return [[ComboBox]] with the handler set.
     */
    def doing(handler: EventHandler[ActionEvent]): T =
      cbox.setOnAction(handler)
      cbox

  private def imageView(filepath: String): ImageView = new ImageView(
    new Image(getClass.getResourceAsStream(filepath))
  ):
    fitWidth = CardsPane.paneWidth - CardsPane.margin
    preserveRatio = true

  private def placeholder: Rectangle = new Rectangle:
    width = CardsPane.paneWidth
    height = CardsPane.paneHeight
    fill = CardsPane.placeholderColor
