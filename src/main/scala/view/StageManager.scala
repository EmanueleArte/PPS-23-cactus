package view

import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.stage.Stage
import view.module.cactus.AppPane

/** Represents the manager of the stages of the gui. */
trait StageManager:
  /** Represents the type of the scene to show. */
  type SceneType

  /** Shows the gui. */
  def show(): Unit

  /**
   * Sets the scene.
   *
   * @param scene the scene to show.
   * @param showScene `true` if the scene should be immediately shown, `false` otherwise.
   */
  def setScene(scene: SceneType, showScene: Boolean): Unit

/** Represents the manager of the stages of the gui using ScalaFX. */
object ScalaFXStageManager extends StageManager:
  override type SceneType = Scene

  override def show(): Unit = ScalaFXWindow.main(Array.empty)

  override def setScene(scene: SceneType, showScene: Boolean): Unit =
    ScalaFXWindow.currentScene = scene
    if showScene then ScalaFXWindow.showWindow()

  /** Represents the window of the gui. */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  private object ScalaFXWindow extends JFXApp3:
    var currentScene: Scene = _

    /** Shows the window. */
    def showWindow(): Unit = Platform.runLater:
      stage.scene = currentScene

    override def start(): Unit =
      stage = new PrimaryStage:
        title = "Cactus & Co."
        width = AppPane.windowWidth
        height = AppPane.windowHeight
        minWidth = AppPane.mainPaneWidth
        minHeight = AppPane.mainPaneHeight
        scene = new Scene
      showWindow()
