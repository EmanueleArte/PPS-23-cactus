package mvc

import control.module.CactusControllerModule
import model.logic.Logics.CactusLogic
import model.module.CactusModelModule
import view.module.ViewModule

/** Represents the main module for the Cactus game. */
object CactusMVC extends CactusModelModule.Interface with CactusControllerModule.Interface with ViewModule.Interface:
  override type ModelType      = CactusLogic
  override type ControllerType = CactusController
  override type ViewType

  override val model: ModelType           = CactusLogic(4)
  override val controller: ControllerType = CactusController()
  override val view: ViewType             = ???

  @main def main () : Unit = ???