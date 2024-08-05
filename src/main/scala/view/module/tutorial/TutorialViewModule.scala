package view.module.tutorial

import control.module.menu.MainMenuControllerModule
import control.module.tutorial.TutorialControllerModule
import mvc.PlayableGame
import mvc.PlayableGame.Cactus
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import view.ScalaFXStageManager
import view.module.ViewModule
import view.module.cactus.AppPane.{mainPaneHeight, mainPaneWidth, windowHeight, windowWidth}

/** Represents the view module for the tutorial. */
object TutorialViewModule extends ViewModule:
  override type ViewType = TutorialView

  override type Requirements = TutorialControllerModule.Provider

  /** Represents the tutorial view. */
  trait TutorialView extends View:
    /** Shows the tutorial view. */
    def show(game: PlayableGame): Unit

  /** Represents the view component for the menu. */
  trait Component:
    context: Requirements =>

    /** Implementation of the tutorial view using ScalaFx. */
    class TutorialScalaFxView extends TutorialView:
      private var _currentGame: PlayableGame = Cactus
      override def show(): Unit =
        ScalaFXStageManager.newStage(
          new Scene(mainPaneWidth, mainPaneHeight):
            content = List(TutorialPane(_currentGame, context.controller, this.width, this.height).pane)
        )

      override def show(game: PlayableGame): Unit =
        _currentGame = game
        show()


  /** Interface of the view module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
