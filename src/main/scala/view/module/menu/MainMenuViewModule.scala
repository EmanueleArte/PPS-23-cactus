package view.module.menu

import control.module.menu.MainMenuControllerModule
import scalafx.application.Platform
import scalafx.scene.Scene
import view.module.cactus.AppPane.{windowHeight, windowWidth}
import view.ScalaFXStageManager
import view.module.ViewModule

/** Represents the view module for the menu. */
object MainMenuViewModule extends ViewModule:
  override type ViewType = View

  override type Requirements = MainMenuControllerModule.Provider

  trait MainMenuView extends View:
    /** Shows the main menu from the final screen. */
    def showFromFinalScreen(): Unit

  /** Represents the view component for the menu. */
  trait Component:
    context: Requirements =>

    /** Implementation of the main menu view using ScalaFx. */
    class MainMenuScalaFxView extends MainMenuView:

      private def setScene(showScene: Boolean): Unit =
        ScalaFXStageManager.setScene(
          new Scene(windowWidth, windowHeight):
            content = List(MainMenuPane(context.controller, this.width, this.height).pane)
          ,
          showScene
        )

      override def show(): Unit =
        Platform.startup(() => setScene(false))
        ScalaFXStageManager.show()

      override def showFromFinalScreen(): Unit =
        setScene(true)

  /** Interface of the view module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
