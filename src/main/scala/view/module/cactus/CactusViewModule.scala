package view.module.cactus

import control.module.cactus.CactusControllerModule
import scalafx.scene.Scene
import scalafx.scene.layout.{Background, BackgroundFill, HBox, Priority}
import view.module.cactus.AppPane.*
import view.ScalaFXStageManager
import view.module.ViewModule
import view.ViewDSL.{containing, tall}

/** Represents the view component. */
object CactusViewModule extends ViewModule:
  override type ViewType     = CactusView
  override type Requirements = CactusControllerModule.Provider

  trait CactusView extends View:
    def updateViewTurnPhase(): Unit
    def updateDiscardPile(): Unit

  /** Represents the view component for the Cactus game. */
  trait Component:
    context: Requirements =>

    class CactusScalaFXView extends CactusView:
      private lazy val asidePane = AsidePane(context.controller)
      private lazy val mainPane  = MainPane(context.controller)
      override def show(): Unit =
        ScalaFXStageManager.setScene(
          new Scene(windowWidth, windowHeight):
            fill = AppPane.mainPaneColor
            content = List(mainPane.pane, asidePane.pane)
          ,
          true
        )

      override def updateViewTurnPhase(): Unit =
        mainPane.updateCurrentPlayer()
        asidePane.updateViewTurnPhase()

      override def updateDiscardPile(): Unit = mainPane.updateDiscardPile()

  /** Interface of the view module of game. */
  trait Interface extends Provider with Component:
    self: Requirements =>