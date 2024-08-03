package view.module.finalscreen

import control.module.finalscreen.FinalScreenControllerModule.FinalScreenController
import control.module.menu.MainMenuControllerModule.MainMenuController
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Spinner
import scalafx.scene.layout.{HBox, Pane, VBox}
import view.ViewPosition
import view.module.cactus.{AppPane, ScalaFXPane}
import view.module.menu.CustomStackPane
import view.ViewDSL.{
  aligned,
  baseWidth,
  bold,
  colored,
  containing,
  doing,
  initialValue,
  prompt,
  saying,
  spaced,
  telling,
  veryBig,
  withMargin,
  Button as ButtonElement,
  ComboBox as ComboBoxElement,
  Label as LabelElement
}

import scala.language.postfixOps

class FinalScreenPane(
    controller: FinalScreenController,
    sceneWidth: ReadOnlyDoubleProperty,
    sceneHeight: ReadOnlyDoubleProperty
) extends ScalaFXPane:
  override def paneWidth: Int         = AppPane.mainPaneWidth
  override def paneHeight: Int        = AppPane.mainPaneHeight
  override def position: ViewPosition = hCenter
  private def hCenter: ViewPosition   = ViewPosition(paneWidth / 2, 0)

  override def pane: Pane = new CustomStackPane(sceneWidth, sceneHeight)
    .colored(AppPane.mainPaneColor)
    .containing(
      new VBox()
        .aligned(Pos.TopCenter)
        .spaced(20)
        .containing(
          (LabelElement telling "Cactus & Co." bold).veryBig
          .aligned(Pos.TopCenter)
          .withMargin(new scalafx.geometry.Insets(Insets(50, 0, 50, 0)))
        )
        .containing(
          new HBox()
            .aligned(Pos.Center)
            .spaced(10)
            .containing((LabelElement telling "Final Score:").veryBig)
        )
        .containing(
          new HBox()
            .aligned(Pos.Center)
            .spaced(5)
            .containing(LabelElement telling "Player:")
            .containing(LabelElement telling "???")
        )
        // .containing(playersPane.pane)
        .containing(ButtonElement saying "Return to main menu" doing (_ => returnToMainMenu()))
    )

  private def returnToMainMenu(): Unit = ()
