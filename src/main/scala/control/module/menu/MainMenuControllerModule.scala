package control.module.menu

import control.module.ControllerModule
import model.module.menu.MainMenuModelModule
import view.module.menu.MainMenuViewModule

object MainMenuControllerModule extends ControllerModule:
  override type ControllerType = MainMenuController
  override type Requirements   = MainMenuModelModule.Provider with MainMenuViewModule.Provider

  /** Represents the main menu controller. */
  trait MainMenuController extends Controller

  /** Represents the controller component for the menu. */
  trait Component:
    context: Requirements =>

    /** Implementation of [[MainMenuController]]. */
    class MainMenuControllerImpl extends MainMenuController:
      def selectGame(game: String): Unit = context.model.selectedGame = game

      def setNPlayers(n: Int): Unit = context.model.nPlayers = n

  /** Interface of the controller module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
