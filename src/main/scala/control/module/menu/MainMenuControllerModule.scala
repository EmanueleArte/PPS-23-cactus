package control.module.menu

import control.module.ControllerModule
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.module.menu.MainMenuModelModule
import mvc.PlayableGame
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
    def selectGame(game: PlayableGame): Unit

    /**
     * Starts a game with a given number of players.
     * @param nPlayers the number of players.
     */
    def startGame(nPlayers: Int): Unit

    /**
     * Starts a game with bot players.
     * @param drawings the drawing methods of the bots.
     * @param discardings the discarding methods of the bots.
     * @param memories the memory options of the bots.
     */
    def startCactusGameWithBots(
        drawings: Seq[DrawMethods],
        discardings: Seq[DiscardMethods],
        memories: Seq[Memory]
    ): Unit

  /** Represents the controller component for the menu. */
  trait Component:
    context: Requirements =>

    /** Implementation of [[MainMenuController]]. */
    class MainMenuControllerImpl extends MainMenuController:

      def selectGame(game: PlayableGame): Unit = context.model.selectedGame = game

      def startGame(nPlayers: Int): Unit =
        context.model.selectedGame.gameMVC.setup(nPlayers).run()

      def startCactusGameWithBots(
          drawings: Seq[DrawMethods],
          discardings: Seq[DiscardMethods],
          memories: Seq[Memory]
      ): Unit =
        context.model.selectedGame.gameMVC.setupWithBots((drawings, discardings, memories)).run()

  /** Interface of the controller module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
