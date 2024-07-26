package view.module.cactus

import control.module.cactus.CactusControllerModule
import scalafx.scene.Scene
import view.AppPane.{windowHeight, windowWidth}
import view.{AppPane, ScalaFXStageManager}
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
      override def show(): Unit =
        ScalaFXStageManager.setScene(
          new Scene(windowWidth, windowHeight):
            content = List(MainPane(context.controller).pane, AsidePane(context.controller).pane)
          ,
          true
        )

  /** Interface of the view module of game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
