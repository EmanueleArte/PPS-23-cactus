package view.module.cactus.finalscreen

import control.module.cactus.finalscreen.FinalScreenControllerModule.FinalScreenController
import mvc.MainMenuMVC
import scalafx.application.Platform
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.layout.{HBox, Pane, VBox}
import view.ViewPosition
import view.module.cactus.{AppPane, ScalaFXPane}
import view.ViewUtils.CustomStackPane
import view.ViewDSL.{aligned, bold, colored, containing, doing, saying, spaced, telling, veryBig, withMargin, Button as ButtonElement, Label as LabelElement}
import view.module.menu.MainMenuViewModule.MainMenuView

import scala.language.postfixOps

/**
 * ScalaFX final screen pane.
 * @param controller controller of the final screen.
 */
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
        .containing(playersPane)
        .containing(ButtonElement saying "Return to main menu" doing (_ => returnToMainMenu()))
        .containing(ButtonElement saying "Close application" doing (_ => Platform.exit()))
    )

  private def returnToMainMenu(): Unit =
    controller.returnToMainMenu()

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  private def playersPane: VBox =
    val vbox = new VBox()
      .aligned(Pos.TopCenter)
      .spaced(10)
    var hboxes: Seq[HBox] = Seq.empty
    controller.playersScores.foreach((p, s) => {
      val hbox = Seq.fill(1)(new HBox()
        .aligned(Pos.Center)
        .spaced(50)
        .containing(LabelElement telling p.name bold)
        .containing(LabelElement telling s.toString))
      hboxes = hboxes ++ hbox
    })
    vbox.children = hboxes
    vbox