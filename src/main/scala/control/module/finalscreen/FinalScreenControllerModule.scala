package control.module.finalscreen

import control.module.ControllerModule
import model.module.finalscreen.FinalScreenModelModule
import view.module.finalscreen.FinalScreenViewModule

object FinalScreenControllerModule extends ControllerModule:
  override type ControllerType = FinalScreenController
  override type Requirements = FinalScreenModelModule.Provider with FinalScreenViewModule.Provider

  trait FinalScreenController extends Controller

  trait Component:
    context: Requirements =>

    class FinalScreenControllerImpl extends FinalScreenController

  trait Interface extends Provider with Component:
    self: Requirements =>