package control.module.menu

import control.module.ControllerModule
import model.logic.Logics.Players
import model.module.menu.MainMenuModelModule
import view.module.menu.MainMenuViewModule

object MainMenuControllerModule extends ControllerModule:
  override type ControllerType = MainMenuController
  override type Requirements   = MainMenuModelModule.Provider with MainMenuViewModule.Provider

  /** Represents the main menu controller. */
  trait MainMenuController extends Controller:
    /**
     * Selects a game.
     * @param game the game selected.
     */
    def selectGame(game: String): Unit

    /**
     * Starts a game with a given number of players.
     * @param n the number of players.
     */
    def startGame(n: Int): Unit

    /**
     * Starts a game with the given players.
     * @param players the players of the game.
     */
    def startGame(players: Players): Unit

  /** Represents the controller component for the menu. */
  trait Component:
    context: Requirements =>

    /** Implementation of [[MainMenuController]]. */
    class MainMenuControllerImpl extends MainMenuController:
      def selectGame(game: String): Unit    = context.model.selectedGame = game
      def startGame(n: Int): Unit           = context.model.nPlayers = n
      def startGame(players: Players): Unit = context.model.nPlayers = 1

  /** Interface of the controller module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
