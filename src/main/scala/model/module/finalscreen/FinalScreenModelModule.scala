package model.module.finalscreen

import model.module.ModelModule

object FinalScreenModelModule extends ModelModule:
  override type ModelType = FinalScreenModel

  trait FinalScreenModel

  trait Component:
    class FinalScreenModelModuleImpl extends FinalScreenModel

  trait Interface extends Provider with Component
