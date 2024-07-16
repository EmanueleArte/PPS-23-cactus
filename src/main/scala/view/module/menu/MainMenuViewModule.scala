package view.module.menu

import control.module.menu.MainMenuControllerModule
import model.bot.CactusBotsData
import model.game.GamesList
import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Button, ComboBox, Label, ListView, Spinner}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Screen
import view.module.ViewModule

/** Represents the view module for the menu. */
object MainMenuViewModule extends ViewModule:
  override type ViewType = JFXApp3

  override type Requirements = MainMenuControllerModule.Provider

  /** Represents the view component for the menu. */
  trait Component:
    context: Requirements =>

    /** Implementation of the main menu view using ScalaFx. */
    class MainMenuFxView extends JFXApp3:
      private var _players: Seq[VBox] = Seq.empty
      private val _playersBox: HBox = new HBox:
        alignment = Pos.Center
        spacing = 50
        margin = new scalafx.geometry.Insets(Insets(0, 0, 30, 0))

      private def updatePlayersDisplay(players: Seq[VBox], diff: Int): Unit =
        _playersBox.children.clear()
        _players = diff match
          case diff if diff < 0 => _players.dropRight(-diff)
          case _                => _players ++ players
        _playersBox.children = _players

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
            ,
            new Label("Discard method:"),
            new ComboBox[String]:
              items = ObservableBuffer.from(CactusBotsData.DiscardMethods.values.map(_.toString))
              promptText = "Select a discard method"
              prefWidth = 200
            ,
            new Label("Memory:"),
            new ComboBox[String]:
              items = ObservableBuffer.from(CactusBotsData.Memory.values.map(_.toString))
              promptText = "Select a memory quality"
              prefWidth = 200
          )

      override def start(): Unit =
        val initialPlayers = Seq(
          new VBox:
            alignment = Pos.Center
            spacing = 10
            children = Seq(new Label("Player 1"))
          ,
          createBotBox("Player 2")
        )
        updatePlayersDisplay(initialPlayers, 2)

        val primaryScreenBounds = Screen.primary.visualBounds
        stage = new PrimaryStage:
          title.value = "Main Menu"
          width = primaryScreenBounds.width
          height = primaryScreenBounds.height
          scene = new Scene:
            root = new VBox:
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

  /** Interface of the view module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
