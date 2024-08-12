package mvc

import mvc.PlayableGame.Cactus
import view.module.cactus.tutorial.TutorialViewModule
import TutorialViewModule.ViewType

/** Represents the main module for the tutorial. */
object TutorialMVC extends TutorialViewModule.Interface:

  override val view: ViewType = TutorialScalaFxView()

  /** Shows the tutorial.
   * @param game the [[PlayableGame]] the tutorial refers to.
   */
  def run(game: PlayableGame): Unit = view.show(game)
