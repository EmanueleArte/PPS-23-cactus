package mvc

import control.module.cactus.finalscreen.FinalScreenControllerModule.ControllerType
import model.player.Players.CactusPlayer
import view.module.cactus.finalscreen.FinalScreenViewModule
import FinalScreenViewModule.ViewType
import control.module.cactus.finalscreen.FinalScreenControllerModule
import model.game.Scores

/** Represents the main module for the final screen. */
object FinalScreenMVC extends FinalScreenControllerModule.Interface with FinalScreenViewModule.Interface:

  override val controller: ControllerType = FinalScreenControllerImpl()
  override val view: ViewType             = FinalScreenScalaFxView()

  /** Shows the final screen. */
  def run(): Unit = view.show()

  /**
   * Sets up the players with their scores.
   * @param playersScores a [[Map]] with the players and their scores
   */
  def setup(playersScores: Scores): Unit =
    controller.setupPlayersScores(playersScores)
