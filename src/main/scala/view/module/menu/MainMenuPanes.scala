package view.module.menu

import control.module.menu.MainMenuControllerModule.MainMenuController
import model.bot.CactusBotsData
import model.game.GamesList
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, ComboBox, Label, Spinner}
import scalafx.scene.layout.{HBox, Pane, VBox}
import view.{AppPane, ViewPosition}
import view.Panes.ScalaFXPane
import view.Utils.topLeftCorner

import scala.util.Random

/**
 * ScalaFX main menu pane.
 * @param controller controller of the main menu.
 */
class MainMenuPane(controller: MainMenuController) extends ScalaFXPane:
  override def paneWidth: Int         = AppPane.mainPaneWidth
  override def paneHeight: Int        = AppPane.mainPaneHeight
  override def position: ViewPosition = hCenter
  private def hCenter: ViewPosition   = ViewPosition(paneWidth / 2, 0)

  private var _players: Seq[VBox] = Seq.empty
  private val _playersBox: HBox = new HBox:
    alignment = Pos.Center
    spacing = 50
    margin = new scalafx.geometry.Insets(Insets(0, 0, 30, 0))

  override def pane: Pane = new Pane:
    layoutX = position.x
    layoutY = position.y
    prefWidth = paneWidth
    prefHeight = paneHeight
    children = Seq(
      new VBox:
        alignment = Pos.Center
        spacing = 20
        children = Seq(
          new Label("Cactus & Co."):
            style = "-fx-font-size: 60pt; -fx-font-weight: bold; -fx-text-alignment: center;"
            margin = new scalafx.geometry.Insets(Insets(0, 0, 50, 0))
          ,
          new ComboBox[String]:
            items = ObservableBuffer.from(GamesList.games)
            promptText = "Select a game"
            prefWidth = 200
          ,
          new HBox:
            alignment = Pos.Center
            spacing = 10
            children = Seq(
              new Label("Number of players:"),
              new Spinner[Int](2, 6, 2):
                editable = false
                prefWidth = 200
                value.onChange((_, old, n) =>
                  val newPlayers = for i <- old + 1 to n yield createBotBox(s"Player $i")
                  updatePlayersDisplay(newPlayers, n - old)
                )
            )
          ,
          _playersBox,
          new Button:
            text = "Start game"
            onAction = _ => Platform.exit()
        )
    )

  /** Initial config of main menu. */
  private def menuInit(): Unit =
    val initialPlayers = Seq(
      new VBox:
        alignment = Pos.Center
        spacing = 10
        children = Seq(new Label("Player 1"))
      ,
      createBotBox("Player 2")
    )
    updatePlayersDisplay(initialPlayers, 2)

  menuInit()

  /**
   * Updates the players input forms.
   * @param players players to add.
   * @param diff if positive, adds the players to the list, if negative, removes the last `-diff` players.
   */
  private def updatePlayersDisplay(players: Seq[VBox], diff: Int): Unit =
    _playersBox.children.clear()
    _players = diff match
      case diff if diff < 0 => _players.dropRight(-diff)
      case _                => _players ++ players
    _playersBox.children = _players

  /**
   * Creates a box with the input fields for a bot.
   * @param name name of the bot.
   * @return the bot box.
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  private def createBotBox(name: String): VBox =
    new VBox:
      alignment = Pos.Center
      spacing = 10
      children = Seq(
        new Label(s"$name (Bot)"),
        new Label("Draw method:"),
        new ComboBox[String]:
          items = ObservableBuffer.from(CactusBotsData.DrawMethods.values.map(_.toString))
          promptText = "Select a draw method"
          prefWidth = 200
          value = CactusBotsData.DrawMethods.values(Random.nextInt(CactusBotsData.DrawMethods.values.length)).toString
        ,
        new Label("Discard method:"),
        new ComboBox[String]:
          items = ObservableBuffer.from(CactusBotsData.DiscardMethods.values.map(_.toString))
          promptText = "Select a discard method"
          prefWidth = 200
          value = CactusBotsData.DiscardMethods
            .values(Random.nextInt(CactusBotsData.DiscardMethods.values.length))
            .toString
        ,
        new Label("Memory:"),
        new ComboBox[String]:
          items = ObservableBuffer.from(CactusBotsData.Memory.values.map(_.toString))
          promptText = "Select a memory quality"
          prefWidth = 200
          value = CactusBotsData.Memory.values(Random.nextInt(CactusBotsData.Memory.values.length)).toString
      )
