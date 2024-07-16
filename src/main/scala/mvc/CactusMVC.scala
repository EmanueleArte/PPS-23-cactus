package mvc

import control.module.CactusControllerModule
import model.logic.Logics.CactusLogic
import model.module.CactusModelModule
import view.module.ViewModule

/** Represents the main module for the Cactus game. */
object CactusMVC
    extends MVC
    with CactusModelModule.Interface
    with CactusControllerModule.Interface
    with ViewModule.Interface:

  override type ModelType      = CactusLogic
  override type ControllerType = CactusController
  override type ViewType

  override lazy val model: ModelType      = CactusLogic(nPlayers)
  override val controller: ControllerType = CactusController()
  override val view: ViewType             = ???

  override def setup(nPlayers: Int): Unit =
    nPlayers match
      case _ if nPlayers < _minPlayers => super.setup(_minPlayers)
      case _ if nPlayers > _maxPlayers => super.setup(_maxPlayers)
      case _                           => super.setup(nPlayers)

  @main def main(): Unit = ???
