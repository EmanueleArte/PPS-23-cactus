package control.module.finalscreen

import control.module.ControllerModule
import model.game.Scores
import mvc.MainMenuMVC
import view.module.finalscreen.FinalScreenViewModule
import view.module.menu.MainMenuViewModule.MainMenuView

object FinalScreenControllerModule extends ControllerModule:
  override type ControllerType = FinalScreenController
  override type Requirements   = FinalScreenViewModule.Provider

  /** Represents the final screen controller. */
  trait FinalScreenController extends Controller:
    /**
     * The players with their scores.
     * @return a [[Map]] with the players and their scores
     */
    def playersScores: Scores

    /**
     * Sets up the players with their scores.
     * @param playersScores a [[Map]] with the players and their scores
     */
    def setupPlayersScores(playersScores: Scores): Unit

    /** Makes the application return to main menu. */
    def returnToMainMenu(): Unit

  /** Represents the controller component for the final screen. */
  trait Component:
    context: Requirements =>

    /** Implementation of [[FinalScreenController]]. */
    class FinalScreenControllerImpl extends FinalScreenController:
      private var _playersScores: Scores = Map.empty

      override def playersScores: Scores = _playersScores

      override def setupPlayersScores(playersScores: Scores): Unit =
        _playersScores = playersScores

      override def returnToMainMenu(): Unit = MainMenuMVC.view match
        case view: MainMenuView => view.showFromFinalScreen()
        case _                  => ()

  /** Interface of the controller module of the final screen. */
  trait Interface extends Provider with Component:
    self: Requirements =>
