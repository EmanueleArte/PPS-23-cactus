package view.module.cactus.tutorial

import mvc.PlayableGame
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.layout.{Pane, VBox}
import TutorialDescriptions.GameTutorialValue
import view.ViewUtils.CustomStackPane
import view.module.cactus.{AppPane, ScalaFXPane}
import view.ViewDSL.{
  aligned,
  bold,
  colored,
  containing,
  spaced,
  telling,
  veryBig,
  withMargin,
  textFlow,
  dynamicLong,
  Label as LabelElement
}
import view.ViewPosition

import scala.language.postfixOps

/**
 * ScalaFX tutorial pane for the cactus game.
 *
 * @param sceneWidth width of the scene.
 * @param sceneHeight height of the scene
 */
class TutorialPane(
    game: PlayableGame,
    sceneWidth: ReadOnlyDoubleProperty,
    sceneHeight: ReadOnlyDoubleProperty
) extends ScalaFXPane:
  override def paneWidth: Int         = AppPane.mainPaneWidth
  override def paneHeight: Int        = AppPane.mainPaneHeight
  override def position: ViewPosition = ViewPosition(paneWidth / 2, 0)

  override def pane: Pane = new CustomStackPane(sceneWidth, sceneHeight)
    .colored(AppPane.mainPaneColor)
    .containing(
      new VBox()
        .aligned(Pos.TopCenter)
        .spaced(20)
        .containing(
          (LabelElement telling TutorialDescriptions.gamesDescriptions(game)(GameTutorialValue.Name) bold).veryBig
          .aligned(Pos.TopCenter)
          .withMargin(new scalafx.geometry.Insets(Insets(50, 0, 50, 0)))
        )
        .containing(
          (LabelElement telling TutorialDescriptions
            .gamesDescriptions(game)(GameTutorialValue.Description)).dynamicLong(sceneWidth).aligned(Pos.TopLeft).textFlow
        )
        .containing(
          (LabelElement telling TutorialDescriptions
            .gamesDescriptions(game)(GameTutorialValue.Rules)).dynamicLong(sceneWidth).aligned(Pos.TopLeft).textFlow
        )
        .containing(
          (LabelElement telling TutorialDescriptions
            .gamesDescriptions(game)(GameTutorialValue.Points)).dynamicLong(sceneWidth).aligned(Pos.TopLeft).textFlow
        )
    )
