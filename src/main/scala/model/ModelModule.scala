package model

import model.logic.Logics.Logic

/** Represents the model component. */
object ModelModule:
  /** Type representing the model of the game. */
  type ModelType <: Logic
  
  /** Provider a [[Model]]. */
  trait Provider:
    val model: ModelType

  /** Interface of the game. */
  trait Interface extends Provider