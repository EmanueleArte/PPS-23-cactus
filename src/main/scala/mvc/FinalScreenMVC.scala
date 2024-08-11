package mvc

import control.module.finalscreen.FinalScreenControllerModule
import control.module.finalscreen.FinalScreenControllerModule.ControllerType
import model.game.Scores
import model.player.Players.CactusPlayer
import view.module.finalscreen.FinalScreenViewModule
import view.module.finalscreen.FinalScreenViewModule.ViewType

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
