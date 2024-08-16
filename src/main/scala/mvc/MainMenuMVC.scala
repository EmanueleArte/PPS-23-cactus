package mvc

import control.module.menu.MainMenuControllerModule
import control.module.menu.MainMenuControllerModule.ControllerType
import model.module.menu.MainMenuModelModule
import model.module.menu.MainMenuModelModule.ModelType
import view.module.menu.MainMenuViewModule
import view.module.menu.MainMenuViewModule.ViewType

/** Represents the main module for the main menu. */
object MainMenuMVC
    extends MainMenuModelModule.Interface
    with MainMenuControllerModule.Interface
    with MainMenuViewModule.Interface:

  override lazy val model: ModelType      = MainMenuModelImpl()
  override val controller: ControllerType = MainMenuControllerImpl()
  override val view: ViewType             = MainMenuScalaFxView()

  /** Shows the main menu */
  def run(): Unit = view.show()
