package view.module.menu

import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ListView}
import scalafx.scene.layout.VBox
import view.module.ViewModule

/** Represents the view module for the menu. */
object MainMenuViewModule extends ViewModule:
  override type ViewType = JFXApp3

  override type Requirements = Int

  /** Represents the view component for the menu. */
  trait Component:
    context: Requirements =>

    class MainMenuView extends JFXApp3:
      override def start(): Unit =
        stage = new JFXApp3.PrimaryStage:
          title.value = "Main Menu"
          scene = new Scene:
            root = new VBox:
              alignment = Pos.Center
              spacing = 10
              children = Seq(
                new Label("Main Menu"):
                  style = "-fx-font-size: 24pt; -fx-text-alignment: center;"
                , new ListView[String]:
                  items = ObservableBuffer("Option 1", "Option 2", "Option 3")
                  prefHeight = 150
              )

  /** Interface of the view module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
