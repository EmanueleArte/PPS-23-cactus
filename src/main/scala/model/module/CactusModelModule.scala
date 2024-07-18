package model.module

import model.logic.Logics.CactusLogic

/** Represents the model module for the Cactus game. */
object CactusModelModule extends ModelModule:
  override type ModelType = CactusLogic

  /** Interface of the model module of Cactus game. */
  trait Interface extends Provider
