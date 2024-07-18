package mvc

import control.module.menu.MainMenuControllerModule
import control.module.menu.MainMenuControllerModule.ControllerType
import model.module.menu.MainMenuModelModule
import model.module.menu.MainMenuModelModule.ModelType
import view.module.menu.MainMenuViewModule
import view.module.menu.MainMenuViewModule.ViewType

/** Represents the main module for the main menu. */
object MainMenuMVC
    extends MVC
    with MainMenuModelModule.Interface
    with MainMenuControllerModule.Interface
    with MainMenuViewModule.Interface:

  override lazy val model: ModelType      = MainMenuModelImpl()
  override val controller: ControllerType = MainMenuControllerImpl()
  override val view: ViewType             = MainMenuScalaFxView()

  override def setup(nPlayers: Int): Unit =
    nPlayers match
      case _ if nPlayers < _minPlayers => super.setup(_minPlayers)
      case _ if nPlayers > _maxPlayers => super.setup(_maxPlayers)
      case _                           => super.setup(nPlayers)

  @main def main(): Unit = view.show()
