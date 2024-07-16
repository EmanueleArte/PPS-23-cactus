package view.module

import control.module.{CactusControllerModule, ControllerModule}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import view.{AsidePane, MainPane, Panes}

/** Represents the view component. */
object ScalaFXViewModule extends ViewModule:
  override type Requirements = CactusControllerModule.Provider
  
  /** Represents the view component for the Cactus game. */
  trait Component:
    context: Requirements =>

    class ScalaFXView extends View:
      override def show(): Unit = ScalaFXWindow.main(Array.empty)

      object ScalaFXWindow extends JFXApp3:
        def width: Int = Panes.windowWidth

        def height: Int = Panes.windowHeight

        override def start(): Unit =
          stage = new JFXApp3.PrimaryStage:
            title.value = "Cactus"
            scene = new Scene(ScalaFXWindow.width, ScalaFXWindow.height):
              content = List(MainPane(context.controller).pane, AsidePane(context).pane)


  /** Interface of the view module of game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
