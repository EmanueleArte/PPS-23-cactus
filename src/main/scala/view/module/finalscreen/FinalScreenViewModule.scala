package view.module.finalscreen

import control.module.finalscreen.FinalScreenControllerModule
import model.player.Players.CactusPlayer
import scalafx.scene.Scene
import view.ScalaFXStageManager
import view.module.ViewModule
import view.module.cactus.AppPane.{windowHeight, windowWidth}

object FinalScreenViewModule extends ViewModule:

  override type ViewType = View

  override type Requirements = FinalScreenControllerModule.Provider

  trait FinalScreenView extends View:
    def setupPlayersScores(playersScores: Map[CactusPlayer, Integer]): Unit

  trait Component:
    context: Requirements =>

    /** Implementation of the final screen view using ScalaFx. */
    class FinalScreenScalaFxView(/*playersScores: Map[CactusPlayer, Integer]*/) extends FinalScreenView:
      private var _playersScores: Map[CactusPlayer, Integer] = Map.empty

      override def show(): Unit =
        ScalaFXStageManager.setScene(
          new Scene(windowWidth, windowHeight):
            content = FinalScreenPane(context.controller, this.width, this.height, _playersScores).pane
          ,
          true
        )

      override def setupPlayersScores(playersScores: Map[CactusPlayer, Integer]): Unit =
        _playersScores = playersScores

    /** Interface of the view module of game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
