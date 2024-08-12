package mvc

import mvc.PlayableGame.Cactus
import view.module.cactus.tutorial.TutorialViewModule
import TutorialViewModule.ViewType

/** Represents the main module for the tutorial. */
object TutorialMVC extends TutorialViewModule.Interface:

  override val view: ViewType = TutorialScalaFxView()

  def run(game: PlayableGame): Unit = view.show(game)
