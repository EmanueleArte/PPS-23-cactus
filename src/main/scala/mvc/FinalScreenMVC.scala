package mvc

import control.module.finalscreen.FinalScreenControllerModule
import control.module.finalscreen.FinalScreenControllerModule.ControllerType
import model.module.finalscreen.FinalScreenModelModule
import model.module.finalscreen.FinalScreenModelModule.ModelType
import view.module.finalscreen.FinalScreenViewModule
import view.module.finalscreen.FinalScreenViewModule.ViewType

object FinalScreenMVC
    extends FinalScreenModelModule.Interface
    with FinalScreenControllerModule.Interface
    with FinalScreenViewModule.Interface:

  override lazy val model: ModelType               = FinalScreenModelModuleImpl()
  override /*lazy*/ val controller: ControllerType = FinalScreenControllerImpl()
  override /*lazy*/ val view: ViewType             = FinalScreenScalaFxView()

  def run(): Unit = view.show()