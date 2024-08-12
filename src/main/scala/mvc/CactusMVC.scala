package mvc

import control.module.cactus.CactusControllerModule
import control.module.cactus.CactusControllerModule.ControllerType
import model.logic.Logics.CactusLogic
import model.module.cactus.CactusModelModule
import model.module.cactus.CactusModelModule.ModelType
import view.module.cactus.CactusViewModule.ViewType
import view.module.cactus.CactusViewModule

/** Represents the main module for the Cactus game. */
class CactusMVC
    extends GameMVC
    with CactusModelModule.Interface
    with CactusControllerModule.Interface
    with CactusViewModule.Interface:

  override lazy val model: ModelType      = if areBotsParamsSet then CactusLogic(botsParams) else CactusLogic(nPlayers)
  override val controller: ControllerType = CactusControllerImpl()
  override val view: ViewType             = CactusScalaFXView()

  def run(): Unit = view.show()

/** Companion object for Cactus game MVC. */
object CactusMVC:
  def apply(): CactusMVC = new CactusMVC()
