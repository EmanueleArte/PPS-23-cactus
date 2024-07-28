package control.module

import model.logic.Logics.CactusLogic
import model.module.ModelModule
import org.scalactic.Requirements
import view.module.cactus.CactusViewModule

/** Represents the controller module. */
trait ControllerModule:
  /** Type representing the controller of the game. */
  type ControllerType <: Controller

  /** Controller of the game. */
  trait Controller

  /** Provider for a [[Controller]]. */
  trait Provider:
    val controller: ControllerType

  /** Dependencies for the controller. */
  type Requirements
