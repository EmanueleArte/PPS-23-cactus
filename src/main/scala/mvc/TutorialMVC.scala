package mvc

import view.module.tutorial.TutorialViewModule
import view.module.tutorial.TutorialViewModule.ViewType

/** Represents the main module for the tutorial. */
object TutorialMVC extends TutorialViewModule.Interface:

  override val view: ViewType = TutorialScalaFxView()

  def run(game: PlayableGame): Unit = view.show(game)
