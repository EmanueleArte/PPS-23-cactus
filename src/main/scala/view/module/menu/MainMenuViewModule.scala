package view.module.menu

import control.module.menu.MainMenuControllerModule
import model.game.GamesList
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{ComboBox, Label, ListView}
import scalafx.scene.layout.VBox
import scalafx.stage.Screen
import view.module.ViewModule

/** Represents the view module for the menu. */
object MainMenuViewModule extends ViewModule:
  override type ViewType = JFXApp3

  override type Requirements = MainMenuControllerModule.Provider

  /** Represents the view component for the menu. */
  trait Component:
    context: Requirements =>

    class MainMenuView extends JFXApp3:
      override def start(): Unit =
        val primaryScreenBounds = Screen.primary.visualBounds
        stage = new PrimaryStage:
          title.value = "Main Menu"
          width = primaryScreenBounds.width
          height = primaryScreenBounds.height
          scene = new Scene:
            root = new VBox:
              alignment = Pos.Center
              spacing = 10
              children = Seq(
                new Label("Cactus & Co."):
                  style = "-fx-font-size: 60pt; -fx-font-weight: bold; -fx-text-alignment: center;"
                ,
                new ComboBox[String]:
                  items = ObservableBuffer.from(GamesList.games)
                  promptText = "Select a game"
                  prefWidth = 200
              )

  /** Interface of the view module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
