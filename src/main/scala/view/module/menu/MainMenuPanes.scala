package view.module.menu

import control.module.menu.MainMenuControllerModule.MainMenuController
import model.bot.CactusBotsData
import model.game.GamesList
import scalafx.application.Platform
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos, Rectangle2D}
import scalafx.scene.control.{Button, ComboBox, Label, Spinner}
import scalafx.scene.layout.{HBox, Pane, StackPane, VBox}
import scalafx.stage.Screen
import view.{AppPane, ViewPosition}
import view.Panes.ScalaFXPane

import scala.util.Random

/**
 * Custom [[StackPane]] with basic parameters already set.
 *
 * @param paneWidth width of the pane.
 * @param paneHeight height of the pane.
 */
class CustomStackPane(paneWidth: ReadOnlyDoubleProperty, paneHeight: ReadOnlyDoubleProperty) extends StackPane:
  prefWidth <== paneWidth
  prefHeight <== paneHeight
  alignment = Pos.TopCenter

/**
 * ScalaFX main menu pane.
 * @param controller controller of the main menu.
 */
class MainMenuPane(
    controller: MainMenuController,
    sceneWidth: ReadOnlyDoubleProperty,
    sceneHeight: ReadOnlyDoubleProperty
) extends ScalaFXPane:
  override def paneWidth: Int          = AppPane.mainPaneWidth
  override def paneHeight: Int         = AppPane.mainPaneHeight
  override def position: ViewPosition  = hCenter
  private def hCenter: ViewPosition    = ViewPosition(paneWidth / 2, 0)
  private val playersPane: PlayersPane = new PlayersPane(position)

  override def pane: Pane = new CustomStackPane(sceneWidth, sceneHeight):
    children = Seq(
      new VBox:
        alignment = Pos.TopCenter
        spacing = 20
        children = Seq(
          new Label("Cactus & Co."):
            style = "-fx-font-size: 60pt; -fx-font-weight: bold; -fx-text-alignment: center;"
            margin = new scalafx.geometry.Insets(Insets(50, 0, 50, 0))
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
                  val newPlayers = for i <- old + 1 to n yield playersPane.createBotBox(s"Player $i")
                  playersPane.updatePlayersDisplay(newPlayers, n - old)
                )
            )
          ,
          playersPane.pane,
          new Button:
            text = "Start game"
            onAction = _ => Platform.exit()
        )
    )

  /** Creates a pane with the boxes for players. */
  private class PlayersPane(override val position: ViewPosition) extends ScalaFXPane:
    override def paneWidth: Int  = AppPane.windowWidth
    override def paneHeight: Int = 250

    private var _players: Seq[VBox] = Seq.empty
    private val _playersBox: HBox = new HBox:
      alignment = Pos.Center
      spacing = 50
      margin = new scalafx.geometry.Insets(Insets(0, 0, 30, 0))

    private var _drawMethods: Seq[ComboBox[String]]    = Seq.empty
    private var _discardMethods: Seq[ComboBox[String]] = Seq.empty
    private var _memory: Seq[ComboBox[String]]         = Seq.empty

    override def pane: Pane = new StackPane:
      children = List(_playersBox)

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
    def updatePlayersDisplay(players: Seq[VBox], diff: Int): Unit =
      _playersBox.children.clear()
      _players = diff match
        case diff if diff < 0 =>
          _drawMethods = _drawMethods.dropRight(-diff)
          _discardMethods = _discardMethods.dropRight(-diff)
          _memory = _memory.dropRight(-diff)
          _players.dropRight(-diff)
        case _ => _players ++ players
      _playersBox.children = _players
      println(_drawMethods)

    /**
     * Creates a box with the input fields for a bot.
     * @param name name of the bot.
     * @return the bot box.
     */
    @SuppressWarnings(Array("org.wartremover.warts.All"))
    def createBotBox(name: String): VBox =
      val drawMethod = new ComboBox[String]:
        items = ObservableBuffer.from(CactusBotsData.DrawMethods.values.map(_.toString))
        promptText = "Select a draw method"
        prefWidth = 200
        value = CactusBotsData.DrawMethods.values(Random.nextInt(CactusBotsData.DrawMethods.values.length)).toString
      _drawMethods :+= drawMethod
      val discardMethod = new ComboBox[String]:
        items = ObservableBuffer.from(CactusBotsData.DiscardMethods.values.map(_.toString))
        promptText = "Select a discard method"
        prefWidth = 200
        value = CactusBotsData.DiscardMethods
          .values(Random.nextInt(CactusBotsData.DiscardMethods.values.length))
          .toString
      _discardMethods :+= discardMethod
      val memory = new ComboBox[String]:
        items = ObservableBuffer.from(CactusBotsData.Memory.values.map(_.toString))
        promptText = "Select a memory quality"
        prefWidth = 200
        value = CactusBotsData.Memory.values(Random.nextInt(CactusBotsData.Memory.values.length)).toString
      _memory :+= memory
      new VBox:
        alignment = Pos.Center
        spacing = 10
        children = Seq(
          new Label(s"$name (Bot)"),
          new Label("Draw method:"),
          drawMethod,
          new Label("Discard method:"),
          discardMethod,
          new Label("Memory:"),
          memory
        )
