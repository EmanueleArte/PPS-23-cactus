package view.module.cactus

import control.module.cactus.CactusControllerModule
import scalafx.scene.Scene
import view.module.cactus.AppPane._
import view.ScalaFXStageManager
import view.module.ViewModule

/** Represents the view component. */
object CactusViewModule extends ViewModule:
  override type ViewType     = CactusView
  override type Requirements = CactusControllerModule.Provider

  trait CactusView extends View:
    def updateViewTurnPhase(): Unit

  /** Represents the view component for the Cactus game. */
  trait Component:
    context: Requirements =>

    class CactusScalaFXView extends CactusView:
      private val asidePane = AsidePane(context.controller)
      override def show(): Unit =
        ScalaFXStageManager.setScene(
          new Scene(windowWidth, windowHeight):
            content = List(MainPane(context.controller).pane, asidePane.pane)
          ,
          true
        )

      override def updateViewTurnPhase(): Unit = asidePane.updateViewTurnPhase

  /** Interface of the view module of game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
