package view.module.cactus

import control.module.cactus.CactusControllerModule
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import view.AppPane
import view.module.ViewModule

/** Represents the view component. */
object ScalaFXViewModule extends ViewModule:
  override type ViewType     = ScalaFXView
  override type Requirements = CactusControllerModule.Provider

  trait ScalaFXView extends View

  /** Represents the view component for the Cactus game. */
  trait Component:
    context: Requirements =>

    class ScalaFXViewImpl extends ScalaFXView:
      override def show(): Unit = ScalaFXWindow.main(Array.empty)

      object ScalaFXWindow extends JFXApp3:
        def width: Int = AppPane.windowWidth

        def height: Int = AppPane.windowHeight

        override def start(): Unit =
          stage = new PrimaryStage:
            title.value = "Cactus"
            scene = new Scene(ScalaFXWindow.width, ScalaFXWindow.height):
              content = List(MainPane(context.controller).pane, AsidePane(context.controller).pane)

  /** Interface of the view module of game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
