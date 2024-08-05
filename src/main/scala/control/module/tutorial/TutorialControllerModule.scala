package control.module.tutorial

import control.module.ControllerModule
import view.module.tutorial.TutorialViewModule

import java.lang.ModuleLayer.Controller

object TutorialControllerModule extends ControllerModule:
  override type ControllerType = TutorialController
  override type Requirements   = TutorialViewModule.Provider

  /** Represents the tutorial controller. */
  trait TutorialController extends Controller

  /** Represents the controller component for the tutorial. */
  trait Component:
    context: Requirements =>

    /** Implementation of [[TutorialController]]. */
    class TutorialControllerImpl extends TutorialController

  /** Interface of the controller module of the tutorial. */
  trait Interface extends Provider with Component:
    self: Requirements =>
