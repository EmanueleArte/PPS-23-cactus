package model.module.menu

import model.module.ModelModule
import mvc.PlayableGame

/** Represents the model module for the menu. */
object MainMenuModelModule extends ModelModule:
  override type ModelType = MainMenuModel

  /** Represents the main menu model. */
  trait MainMenuModel:
    /** Selected game to start. */
    var selectedGame: PlayableGame

  /** Represents the model component for the menu. */
  trait Component:
    /** Implementation of [[MainMenuModel]]. */
    class MainMenuModelImpl extends MainMenuModel:
      var selectedGame: PlayableGame = PlayableGame.Cactus

  /** Interface of the model module of the menu. */
  trait Interface extends Provider with Component
