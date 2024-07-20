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

object ViewDSL:

  def Button: Button = new Button:
    prefWidth = Buttons.buttonWidth
    prefHeight = Buttons.buttonHeight
    style = s"-fx-background-color: ${Buttons.buttonBgColor.toRgbString};" +
      s"-fx-text-color: ${Buttons.buttonColor.toRgbString};" +
      s"-fx-border-radius: 3px"

  def Card: Pane = new Pane().long(CardsPane.paneWidth).tall(CardsPane.paneHeight)
  extension [T <: Node] (node: T)
    def at(position: (Int, Int)): T =
      node.setLayoutX(position._1.toDouble)
      node.setLayoutY(position._2.toDouble)
      node

    def at(position: ViewPosition): T = node at (position.x, position.y)

    def reacting(handler: EventHandler[MouseEvent]): T =
      node.setOnMouseClicked(handler)
      node

    def colored(color: Color): T =
      node.style = node.style() + s";-fx-background-color: ${color.toRgbString};"
      node

  extension [T <: Pane] (pane: T)
    def long(width: Int): T =
      pane.setPrefWidth(width)
      pane

    def tall(height: Int): T =
      pane.setPrefHeight(height)
      pane

    def of(dimension: (Int, Int)): T = pane.long(dimension._1).tall(dimension._2)

    def containing(element: Node): T =
      pane.children.add(element)
      pane

    def containing(elements: Seq[Node]): T =
      elements.foreach(pane.children.add(_))
      pane

    def covered: T =
      val filename: String = CardsPane.backsFolderPath + CardsPane.defaultBack
      pane.children.add(imageView(filename))
      pane

    def showing(card: Card): Pane =
      if pane.children.isEmpty then
        val filename: String = CardsPane.frontsFolderPath + s"/${card.suit.toString.toLowerCase()}_${card.value}.png"
        pane.children.add(imageView(filename))
      pane

    def showing(cardOption: Option[Card]): Pane = cardOption match
      case Some(card) => pane showing card
      case _ if pane.children.isEmpty => pane.children.add(placeholder); pane
      case _ => pane

  extension [T <: Button] (button: T)
    def saying(text: String): T =
      button.setText(text)
      button

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