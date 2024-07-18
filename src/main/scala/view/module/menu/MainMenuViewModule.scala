package view.module.menu

import control.module.menu.MainMenuControllerModule
import model.bot.CactusBotsData
import model.game.GamesList
import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Button, ComboBox, Label, ListView, Spinner}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Screen
import view.AppPane
import view.module.ViewModule

import scala.util.Random

/** Represents the view module for the menu. */
object MainMenuViewModule extends ViewModule:
  override type ViewType = View

  override type Requirements = MainMenuControllerModule.Provider

  /** Represents the view component for the menu. */
  trait Component:
    context: Requirements =>

    /** Implementation of the main menu view using ScalaFx. */
    class MainMenuScalaFxView extends View:

      override def show(): Unit = ScalaFXWindow.main(Array.empty)

      object ScalaFXWindow extends JFXApp3:

        override def start(): Unit =
          val primaryScreenBounds = Screen.primary.visualBounds

          stage = new PrimaryStage:
            title.value = "Main Menu"
            scene = new Scene(primaryScreenBounds.width.toInt, primaryScreenBounds.height.toInt):
              content = List(MainMenuPane(context.controller).pane)

  /** Interface of the view module of the menu. */
  trait Interface extends Provider with Component:
    self: Requirements =>
