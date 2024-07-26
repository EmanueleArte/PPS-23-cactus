package view

import scalafx.scene.paint.Color
import view.Utils.toRgbString

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
   * Background color of the main pane, expressed in rgb foramt.
   * @return background rgb color.
   */
  def mainPaneColor: Color = Color.DarkGreen

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

  /**
   * Background color of the side pane.
   * @return background color.
   */
  def asidePaneColor: Color = mainPaneColor

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
  def paneHeight: Int = (CardsPane.paneHeight * maxCardsLines).toInt + normalFontSize

  /**
   * Font size for small texts.
   * @return font size.
   */
  def smallFontSize: Int = 14
  
  /**
   * Font size of the texts in the pane.
   * @return font size.
   */
  def normalFontSize: Int = 18

  /**
   * Font size for big texts.
   * @return font size.
   */
  def bigFontSize: Int = 22

  /**
   * Color of the texts.
   * @return text color.
   */
  def textColor: Color = Color.GhostWhite

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
  def maxCardsLines: Double = 2.3

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
  def placeholderColor: Color = Color.Transparent

object Buttons:
  def margin: Int = 20
  def buttonWidth: Int     = Panes.asidePaneWidth - margin * 2
  def buttonHeight: Int    = 50
  def buttonBgColor: Color = Color.FloralWhite
  def buttonColor: Color   = Color.DarkSlateGray