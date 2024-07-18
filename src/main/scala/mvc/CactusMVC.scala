package mvc

import control.module.cactus.CactusControllerModule
import control.module.cactus.CactusControllerModule.ControllerType
import model.logic.Logics.CactusLogic
import model.module.cactus.CactusModelModule
import model.module.cactus.CactusModelModule.ModelType
import view.module.cactus.ScalaFXViewModule.ViewType
import view.module.cactus.ScalaFXViewModule

/** Represents the main module for the Cactus game. */
object CactusMVC
    extends MVC
    with CactusModelModule.Interface
    with CactusControllerModule.Interface
    with ScalaFXViewModule.Interface:

  override lazy val model: ModelType      = CactusLogic(nPlayers)
  override val controller: ControllerType = CactusControllerImpl()
  override val view: ViewType             = ScalaFXViewImpl()

  override def setup(nPlayers: Int): Unit =
    nPlayers match
      case _ if nPlayers < _minPlayers => super.setup(_minPlayers)
      case _ if nPlayers > _maxPlayers => super.setup(_maxPlayers)
      case _                           => super.setup(nPlayers)

  @main def main(): Unit =
    view.show()
