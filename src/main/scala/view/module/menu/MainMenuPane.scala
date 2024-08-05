package view.module.menu

import control.module.menu.MainMenuControllerModule.MainMenuController
import model.bot.CactusBotsData
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import mvc.PlayableGame
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{ComboBox, Spinner}
import scalafx.scene.layout.{HBox, Pane, StackPane, VBox}
import view.module.cactus.{AppPane, ScalaFXPane}
import view.ViewPosition
import view.Utils.{CustomStackPane, value}
import view.ViewDSL.{aligned, baseWidth, bold, colored, containing, doing, initialValue, prompt, saying, spaced, telling, veryBig, withMargin, Button as ButtonElement, ComboBox as ComboBoxElement, Label as LabelElement}

import scala.language.postfixOps
import scala.util.Random

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

  private val gameSelected: ComboBox[PlayableGame] = ComboBoxElement[PlayableGame]
    .containing(PlayableGame.values)
    .prompt("Select a game")
    .baseWidth(200)
    .initialValue(PlayableGame.Cactus)
    .doing(_ => playersPane = createPlayersPane(value(gameSelected)))

  private def createPlayersPane(game: PlayableGame): PlayersPane = game match
    case PlayableGame.Cactus => new CactusPlayersPane(position)
    case _                   => new CactusPlayersPane(position)

  override def pane: Pane = new CustomStackPane(sceneWidth, sceneHeight)
    .colored(AppPane.mainPaneColor)
    .containing(
      new VBox()
        .aligned(Pos.TopCenter)
        .spaced(20)
        .containing(
          (LabelElement telling "Cactus & Co." bold).veryBig
          .aligned(Pos.TopCenter)
          .withMargin(new scalafx.geometry.Insets(Insets(50, 0, 50, 0)))
        )
        .containing(
          new HBox()
            .aligned(Pos.Center)
            .spaced(10)
            .containing(LabelElement telling "Selected game:")
            .containing(gameSelected)
        )
        .containing(
          new HBox()
            .aligned(Pos.Center)
            .spaced(10)
            .containing(LabelElement telling "Number of players:")
            .containing(
              new Spinner[Int](2, 6, 2):
                editable = false
                prefWidth = 200
                value.onChange((_, old, n) =>
                  val newPlayers = for i <- old until n yield playersPane.createBotBox(s"Bot $i")
                  playersPane.updatePlayersDisplay(newPlayers, n - old)
                )
            )
        )
        .containing(playersPane.pane)
        .containing(ButtonElement saying "Start game" doing (_ => startGame()))
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
    private val _playersBox: HBox = new HBox()
      .aligned(Pos.Center)
      .spaced(50)
      .withMargin(new scalafx.geometry.Insets(Insets(0, 0, 30, 0)))

    var drawMethods: Seq[ComboBox[DrawMethods]]       = Seq.empty
    var discardMethods: Seq[ComboBox[DiscardMethods]] = Seq.empty
    var memoryList: Seq[ComboBox[Memory]]             = Seq.empty

    override def pane: Pane = new StackPane()
      .containing(_playersBox)

    /** Initial config of main menu. */
    private def menuInit(): Unit =
      val initialPlayers = Seq(
        new VBox()
          .aligned(Pos.Center)
          .spaced(10)
          .containing(LabelElement telling "Player"),
        createBotBox("Bot 1")
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
      val drawMethod = ComboBoxElement[DrawMethods]
        .containing(CactusBotsData.DrawMethods.values)
        .prompt("Select a draw method")
        .baseWidth(200)
        .initialValue(CactusBotsData.DrawMethods.values(Random.nextInt(CactusBotsData.DrawMethods.values.length)))
      drawMethods :+= drawMethod
      val discardMethod = ComboBoxElement[DiscardMethods]
        .containing(CactusBotsData.DiscardMethods.values)
        .prompt("Select a discard method")
        .baseWidth(200)
        .initialValue(CactusBotsData.DiscardMethods
          .values(Random.nextInt(CactusBotsData.DiscardMethods.values.length)))
      discardMethods :+= discardMethod
      val memory = ComboBoxElement[Memory]
        .containing(CactusBotsData.Memory.values)
        .prompt("Select a memory quality")
        .baseWidth(200)
        .initialValue(CactusBotsData.Memory.values(Random.nextInt(CactusBotsData.Memory.values.length)))
      memoryList :+= memory
      new VBox()
        .aligned(Pos.Center)
        .spaced(10)
        .containing(LabelElement telling s"$name")
        .containing(LabelElement telling "Draw method:")
        .containing(drawMethod)
        .containing(LabelElement telling "Discard method:")
        .containing(discardMethod)
        .containing(LabelElement telling "Memory:")
        .containing(memory)
