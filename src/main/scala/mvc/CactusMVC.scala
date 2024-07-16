package mvc

import control.module.CactusControllerModule
import model.logic.Logics.CactusLogic
import model.module.CactusModelModule
import view.module.ScalaFXViewModule

/** Represents the main module for the Cactus game. */
object CactusMVC
    extends MVC
    with CactusModelModule.Interface
    with CactusControllerModule.Interface
    with ScalaFXViewModule.Interface:

  override type ModelType      = CactusLogic
  override type ControllerType = CactusControllerImpl
  override type ViewType       = ScalaFXView

  override lazy val model: ModelType      = CactusLogic(nPlayers)
  override val controller: ControllerType = CactusControllerImpl()
  override val view: ViewType             = ScalaFXView()

  override def setup(nPlayers: Int): Unit =
    nPlayers match
      case _ if nPlayers < _minPlayers => super.setup(_minPlayers)
      case _ if nPlayers > _maxPlayers => super.setup(_maxPlayers)
      case _                           => super.setup(nPlayers)

  @main def main(): Unit =
    view.show()
