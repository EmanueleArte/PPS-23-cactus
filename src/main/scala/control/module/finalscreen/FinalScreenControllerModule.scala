package control.module.finalscreen

import control.module.ControllerModule
import model.player.Players.CactusPlayer
import view.module.finalscreen.FinalScreenViewModule

object FinalScreenControllerModule extends ControllerModule:
  override type ControllerType = FinalScreenController
  override type Requirements = FinalScreenViewModule.Provider

  /** Represents the final screen controller. */
  trait FinalScreenController extends Controller:
    /**
     * The players with their scores.
     * @return a [[Map]] with the players and their scores
     */
    def playersScores: Map[CactusPlayer, Integer]

    /**
     * Sets up the players with their scores.
     * @param playersScores a [[Map]] with the players and their scores
     */
    def setupPlayersScores(playersScores: Map[CactusPlayer, Integer]): Unit

  /** Represents the controller component for the final screen. */
  trait Component:
    context: Requirements =>

    /** Implementation of [[FinalScreenController]]. */
    class FinalScreenControllerImpl extends FinalScreenController:
      private var _playersScores: Map[CactusPlayer, Integer] = Map.empty

      override def playersScores: Map[CactusPlayer, Integer] = _playersScores

      override def setupPlayersScores(playersScores: Map[CactusPlayer, Integer]): Unit =
        _playersScores = playersScores


  /** Interface of the controller module of the final screen. */
  trait Interface extends Provider with Component:
    self: Requirements =>