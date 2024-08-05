package mvc

import control.module.tutorial.TutorialControllerModule
import control.module.tutorial.TutorialControllerModule.ControllerType
import mvc.PlayableGame.Cactus
import view.module.tutorial.TutorialViewModule
import view.module.tutorial.TutorialViewModule.ViewType

/** Represents the main module for the tutorial. */
object TutorialMVC
  extends TutorialControllerModule.Interface
    with TutorialViewModule.Interface:

  override val controller: ControllerType = TutorialControllerImpl()
  override val view: ViewType             = TutorialScalaFxView()

  def run(game: PlayableGame): Unit = view.show(game)