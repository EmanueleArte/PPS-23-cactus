package view.module.finalscreen

import view.module.menu.MainMenuViewModule.Requirements
import view.module.ViewModule

object FinalScreenViewModule extends ViewModule:

  override type ViewType = View

  override type Requirements = FinalScreenViewModule.Provider

  trait FinalScreenView extends View

  trait Component:
    context: Requirements =>

    /** Implementation of the final screen view using ScalaFx. */
    class FinalScreenScalaFxView extends FinalScreenView