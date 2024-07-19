package view.module.menu

import control.module.menu.MainMenuControllerModule.MainMenuController
import model.bot.BotBuilder.CactusBotDSL.{discarding, drawing, withMemory}
import model.bot.CactusBotsData
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.game.GamesList
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, ComboBox, Label, Spinner}
import scalafx.scene.layout.{HBox, Pane, StackPane, VBox}
import view.{AppPane, ViewPosition}
import view.ScalaFXPane
import view.Utils.value

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

  private val gameSelected: ComboBox[String] = new ComboBox[String]:
    items = ObservableBuffer.from(GamesList.games)
    promptText = "Select a game"
    prefWidth = 200

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
          gameSelected,
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
            onAction = _ =>
              controller.selectGame(value(gameSelected))
              controller.startGame(4)
//              controller.startGame(
//                playersPane.drawMethods.map(value),
//                playersPane.discardMethods.map(value),
//                playersPane.memoryList.map(value)
//              )
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

    var drawMethods: Seq[ComboBox[DrawMethods]]       = Seq.empty
    var discardMethods: Seq[ComboBox[DiscardMethods]] = Seq.empty
    var memoryList: Seq[ComboBox[Memory]]             = Seq.empty

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
          drawMethods = drawMethods.dropRight(-diff)
          discardMethods = discardMethods.dropRight(-diff)
          memoryList = memoryList.dropRight(-diff)
          _players.dropRight(-diff)
        case _ => _players ++ players
      _playersBox.children = _players

    /**
     * Creates a box with the input fields for a bot.
     * @param name name of the bot.
     * @return the bot box.
     */
    @SuppressWarnings(Array("org.wartremover.warts.All"))
    def createBotBox(name: String): VBox =
      val drawMethod = new ComboBox[DrawMethods]:
        items = ObservableBuffer.from(CactusBotsData.DrawMethods.values)
        promptText = "Select a draw method"
        prefWidth = 200
        value = CactusBotsData.DrawMethods.values(Random.nextInt(CactusBotsData.DrawMethods.values.length))
      drawMethods :+= drawMethod
      val discardMethod = new ComboBox[DiscardMethods]:
        items = ObservableBuffer.from(CactusBotsData.DiscardMethods.values)
        promptText = "Select a discard method"
        prefWidth = 200
        value = CactusBotsData.DiscardMethods
          .values(Random.nextInt(CactusBotsData.DiscardMethods.values.length))
      discardMethods :+= discardMethod
      val memory = new ComboBox[Memory]:
        items = ObservableBuffer.from(CactusBotsData.Memory.values)
        promptText = "Select a memory quality"
        prefWidth = 200
        value = CactusBotsData.Memory.values(Random.nextInt(CactusBotsData.Memory.values.length))
      memoryList :+= memory
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
