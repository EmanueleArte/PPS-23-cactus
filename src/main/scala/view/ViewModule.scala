package view

import control.ControllerModule

/** Represents the view component. */
object ViewModule:
  /** Type representing the view of the game. */
  type ViewType

  /** Provider a [[View]]. */
  trait Provider:
    val view: ViewType

  type Requirements = ControllerModule.Provider
  
  trait Component:
    context: Requirements =>

  /** Interface of the game. */
  trait Interface extends Provider
