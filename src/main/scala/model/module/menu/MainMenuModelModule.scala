package model.module.menu

import model.module.ModelModule

/** Represents the model module for the menu. */
object MainMenuModelModule extends ModelModule:
  override type ModelType = MainMenuModel

  /** Represents the main menu model. */
  trait MainMenuModel:
    var selectedGame: String = ""
    var nPlayers: Int        = 2

  /** Represents the model component for the menu. */
  trait Component:
    /** Implementation of [[MainMenuModel]]. */
    class MainMenuModelImpl extends MainMenuModel

  /** Interface of the model module of the menu. */
  trait Interface extends Provider with Component
