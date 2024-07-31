package view.module.cactus

import scalafx.geometry.Rectangle2D
import scalafx.scene.paint.Color
import scalafx.stage.Screen
import view.module.cactus.Text.normalFontSize

/** Contains the basic parameters for the application's panes. */
object AppPane:
  private val primaryScreenBounds: Rectangle2D = Screen.primary.visualBounds

  /** Width of the application's window. */
  val windowWidth: Int = primaryScreenBounds.width.toInt

  /** Height of the application's window. */
  val windowHeight: Int = primaryScreenBounds.height.toInt

  /**
   * Ratio of the main pane to the window.
   *
   * @example if `panesRatio` is set to 0.6 it means that the main pane is 60% of the window, while the aside pane is 40%.
   */
  val mainPaneRatio: Double = 0.8

  /** Width of the main pane. */
  val mainPaneWidth: Int = (windowWidth * mainPaneRatio).toInt

  /** Height of the main pane. */
  val mainPaneHeight: Int = (windowHeight * 0.9).toInt

  /** Background color of the main pane, expressed in rgb format. */
  val mainPaneColor: Color = Color.DarkGreen

  /** Width of the side pane. */
  val asidePaneWidth: Int = windowWidth - mainPaneWidth

  /** Height of the side pane. */
  val asidePaneHeight: Int = mainPaneHeight

  /** Background color of the side pane. */
  val asidePaneColor: Color = mainPaneColor

/** Contains the basic parameters for the texts. */
object Text:
  /** Font size for small texts. */
  val smallFontSize: Int = 14

  /** Font size of the texts in the pane. */
  val normalFontSize: Int = 18

  /** Font size for big texts. */
  val bigFontSize: Int = 22

  /** Font size for very big texts. */
  val veryBigFontSize: Int = 60

  /** Color of the texts. */
  val textColor: Color = Color.GhostWhite

/** Contains the basic parameters for the player's panes. */
object PlayersPane:
  /** Maximum number of cards disposable on a line. */
  val maxCardsPerLine: Int = 4

  /** Maximum number of lines. */
  val maxCardsLines: Double = 2.3

  /** Width of the player's pane. */
  val paneWidth: Int = CardsPane.paneWidth * maxCardsPerLine

  /** Height of the player's pane. */
  val paneHeight: Int = (CardsPane.paneHeight * maxCardsLines).toInt + normalFontSize

  /** Radius of the circle representing the turn indicator. */
  val turnIndicatorRadius: Int = 6

  /** Color of the turn indicator. */
  val turnIndicatorColor: Color = Color.GhostWhite

/** Contains the basic parameters for the card's panes. */
object CardsPane:
  private val aspectRatio: Double     = 3.0 / 2.0
  private val cardsFolderPath: String = "/cards"

  /** Space between each card, both horizontal and vertical. */
  val margin: Int = 5

  /** Width of the card's pane. */
  val paneWidth: Int = 50 + margin

  /** Height of the card's pane. */
  val paneHeight: Int = (paneWidth.toDouble * aspectRatio).toInt + margin

  /** Path for the folder of card's backs. */
  val backsFolderPath: String = cardsFolderPath + "/backs"

  /** Path for the folder of card's fronts. */
  val frontsFolderPath: String = cardsFolderPath + "/fronts"

  /** valault back for the cards. */
  val defaultBack: String = "/red.png"

  /** Placeholder color to put when a card is not present. */
  val placeholderColor: Color = Color.Transparent

/** Contains the basic parameters for the buttons. */
object Buttons:
  /** Margins around a button. */
  val margin: Int = 20

  /** Width of a button in the game screen. */
  val buttonWidth: Int = AppPane.asidePaneWidth - margin * 2

  /** Height of a button in the game screen. */
  val buttonHeight: Int = 50

  /** Background color of a button in the game screen. */
  val buttonBgColor: Color = Color.FloralWhite

  /** Foreground color of a button in the game screen. */
  val buttonColor: Color = Color.DarkSlateGray
