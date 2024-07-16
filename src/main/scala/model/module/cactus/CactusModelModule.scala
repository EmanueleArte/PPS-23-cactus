package model.module.cactus

import model.logic.Logics.CactusLogic
import model.module.ModelModule

/** Represents the model module for the Cactus game. */
object CactusModelModule extends ModelModule:
  override type ModelType = CactusLogic

  /** Interface of the model module of Cactus game. */
  trait Interface extends Provider
