package view.module.menu

import control.module.menu.MainMenuControllerModule.MainMenuController
import model.bot.BotBuilder.CactusBotDSL.{discarding, drawing, withMemory}
import model.bot.CactusBotsData
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import mvc.PlayableGame
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
 * Custom [[StackPane]] with basic parameters already set and responsive size.
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
  private var playersPane: PlayersPane = new CactusPlayersPane(position)

  private val gameSelected: ComboBox[PlayableGame] = new ComboBox[PlayableGame]:
    items = ObservableBuffer.from(PlayableGame.values)
    promptText = "Select a game"
    prefWidth = 200
    value = PlayableGame.Cactus
    onAction = _ => playersPane = createPlayersPane(view.Utils.value(this))

  private def createPlayersPane(game: PlayableGame): PlayersPane = game match
    case PlayableGame.Cactus => new CactusPlayersPane(position)
    case _                   => new CactusPlayersPane(position)

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
          new HBox:
            alignment = Pos.Center
            spacing = 10
            children = Seq(
              new Label("Selected game:"),
              gameSelected
            )
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
            onAction = _ => startGame()
        )
    )

  private def startGame(): Unit =
    controller.selectGame(value(gameSelected))
    playersPane match
      case p: CactusPlayersPane =>
        controller.startCactusGameWithBots(
          p.drawMethods.map(value),
          p.discardMethods.map(value),
          p.memoryList.map(value)
        )

  private trait PlayersPane(override val position: ViewPosition) extends ScalaFXPane:
    /**
     * Creates a box with the input fields for a bot.
     *
     * @param name name of the bot.
     * @return the bot box.
     */
    def createBotBox(name: String): VBox

    /**
     * Updates the players input forms.
     *
     * @param players players to add.
     * @param diff    if positive, adds the players to the list, if negative, removes the last `-diff` players.
     */
    def updatePlayersDisplay(players: Seq[VBox], diff: Int): Unit

  /** Creates a pane with the boxes for players. */
  private class CactusPlayersPane(override val position: ViewPosition) extends PlayersPane(position):
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

    override def updatePlayersDisplay(players: Seq[VBox], diff: Int): Unit =
      _playersBox.children.clear()
      _players = diff match
        case diff if diff < 0 =>
          drawMethods = drawMethods.dropRight(-diff)
          discardMethods = discardMethods.dropRight(-diff)
          memoryList = memoryList.dropRight(-diff)
          _players.dropRight(-diff)
        case _ => _players ++ players
      _playersBox.children = _players

    @SuppressWarnings(Array("org.wartremover.warts.All"))
    override def createBotBox(name: String): VBox =
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
