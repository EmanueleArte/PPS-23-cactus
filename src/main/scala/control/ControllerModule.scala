package control

import model.ModelModule
import view.ViewModule

/** Represents the controller component. */
object ControllerModule:
  /** Type representing the controller of the game. */
  type ControllerType <: Controller

  /** Controller of the game. */
  trait Controller

  /** Provider a [[Controller]]. */
  trait Provider:
    val controller: ControllerType

  type Requirements = ModelModule.Provider with ViewModule.Provider

  /** Component of the game. */
  trait Component:
    context: Requirements =>

  /** Interface of the game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
