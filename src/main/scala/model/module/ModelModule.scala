package model.module

import model.logic.Logics.Logic

/** Represents the model component. */
trait ModelModule:
  /** Type representing the model of the game. */
  type ModelType

  /** Provider for a [[Model]]. */
  trait Provider:
    lazy val model: ModelType
