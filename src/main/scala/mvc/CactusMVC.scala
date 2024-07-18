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
    extends GameMVC
    with CactusModelModule.Interface
    with CactusControllerModule.Interface
    with ScalaFXViewModule.Interface:

  override lazy val model: ModelType      = if players.isEmpty then CactusLogic(nPlayers) else CactusLogic(players)
  override val controller: ControllerType = CactusControllerImpl()
  override val view: ViewType             = ScalaFXViewImpl()

  def run(): Unit = view.show()
