package view.module

import control.module.ControllerModule

/** Represents the view component. */
object ViewModule:
  /** Type representing the view of the game. */
  type ViewType

  /** Provider for a [[View]]. */
  trait Provider:
    val view: ViewType

  /** Dependencies for the view. */
  type Requirements
  
  /** Represents the view component for the Cactus game. */
  trait Component:
    context: Requirements =>

  /** Interface of the view module of game. */
  trait Interface extends Provider
