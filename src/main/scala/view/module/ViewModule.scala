package view.module

import control.module.ControllerModule

/** Represents the view component. */
trait ViewModule:
  /** Type representing the view of the game. */
  type ViewType

  /** Provider for a [[View]]. */
  trait Provider:
    val view: ViewType

  /** Dependencies for the view. */
  type Requirements

