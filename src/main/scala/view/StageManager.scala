package view

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

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
   */
  def setScene(scene: SceneType): Unit

/** Represents the manager of the stages of the gui using ScalaFX. */
object ScalaFXStageManager extends StageManager:
  override type SceneType = Scene

  override def show(): Unit = ScalaFXWindow.main(Array.empty)

  override def setScene(scene: SceneType): Unit = ScalaFXWindow.currentScene = scene

  /** Represents the window of the gui. */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  private object ScalaFXWindow extends JFXApp3:
    var currentScene: Scene = _

    override def start(): Unit = new PrimaryStage:
      title.value = "Cactus & Co."
      minWidth = AppPane.mainPaneWidth
      minHeight = AppPane.mainPaneHeight
      scene = currentScene
