package view.module.finalscreen

import control.module.finalscreen.FinalScreenControllerModule
import scalafx.scene.Scene
import view.ScalaFXStageManager
import view.module.ViewModule
import view.module.cactus.AppPane.{windowHeight, windowWidth}

/** Represents the view component. */
object FinalScreenViewModule extends ViewModule:

  override type ViewType = FinalScreenView

  override type Requirements = FinalScreenControllerModule.Provider

  /** Represents the final screen view. */
  trait FinalScreenView extends View

  /** Represents the view component for the final screen. */
  trait Component:
    context: Requirements =>

    /** Implementation of the final screen view using ScalaFx. */
    class FinalScreenScalaFxView extends FinalScreenView:

      override def show(): Unit =
        ScalaFXStageManager.setScene(
          new Scene(windowWidth, windowHeight):
            content = FinalScreenPane(context.controller, this.width, this.height).pane
          ,
          true
        )

    /** Interface of the view module of final screen. */
  trait Interface extends Provider with Component:
    self: Requirements =>
