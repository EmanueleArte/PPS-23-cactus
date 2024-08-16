package view

import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.input.{KeyCode, KeyCombination}
import scalafx.stage.Stage
import view.module.cactus.AppPane

/** Represents the manager of the stages of the gui. */
trait StageManager:
  /** Represents the type of the scene to show. */
  type SceneType

  /** Shows the gui. */
  def show(): Unit

  /**
   * Sets the main stage scene.
   *
   * @param scene the scene to show.
   * @param showScene `true` if the scene should be immediately shown, `false` otherwise.
   */
  def setScene(scene: SceneType, showScene: Boolean): Unit

  /**
   * Create a new stage.
   *
   * @param newScene the new scene to show.
   */
  def newStage(newScene: SceneType): Unit

/** Represents the manager of the stages of the gui using ScalaFX. */
object ScalaFXStageManager extends StageManager:
  override type SceneType = Scene

  override def show(): Unit = ScalaFXWindow.main(Array.empty)

  override def setScene(scene: SceneType, showScene: Boolean): Unit =
    ScalaFXWindow.currentScene = scene
    if showScene then ScalaFXWindow.showWindow()

  override def newStage(newScene: SceneType): Unit = ScalaFXWindow.showExternalWindow(newScene)

  /** Represents the window of the gui. */
  private object ScalaFXWindow extends JFXApp3:
    var currentScene: Scene = new Scene

    /** Shows the main window. */
    def showWindow(): Unit = Platform.runLater:
      stage.scene = currentScene

    /**
     * Shows an external window unrelated to the main one.
     *
     * @param newScene the new scene to show.
     */
    def showExternalWindow(newScene: Scene): Unit = Platform.runLater:
      val stage = new Stage:
        scene = newScene
      stage.show()

    override def start(): Unit =
      stage = new PrimaryStage:
        title = "Cactus & Co."
        width = AppPane.windowWidth
        height = AppPane.windowHeight
        minWidth = AppPane.mainPaneWidth
        minHeight = AppPane.mainPaneHeight
        scene = new Scene
      showWindow()
