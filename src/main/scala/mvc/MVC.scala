package mvc

import control.ControllerModule
import model.ModelModule
import model.logic.Logics.CactusLogic
import view.ViewModule

object MVC extends ModelModule.Interface with ControllerModule.Interface with ViewModule.Interface:
  override type ModelType      = CactusLogic
  override type ControllerType
  override type ViewType

  override val model: ModelType           = CactusLogic(4)
  override val controller: ControllerType = ???
  override val view: ViewType             = ???
