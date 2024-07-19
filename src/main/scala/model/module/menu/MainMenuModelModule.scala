package model.module.menu

import model.bot.BotBuilder.CactusBotDSL.{discarding, drawing, withMemory}
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.logic.Logics.Players
import model.module.ModelModule
import model.player.Players.CactusPlayer
import view.Utils.value

/** Represents the model module for the menu. */
object MainMenuModelModule extends ModelModule:
  override type ModelType = MainMenuModel

  /** Represents the main menu model. */
  trait MainMenuModel:
    /** Selected game to start. */
    var selectedGame: String = ""

    /**
     * Creates the players for the game according to the parameters.
     *
     * @param drawings the drawing params of the bots.
     * @param discardings the discarding params of the bots.
     * @param memories the memory options of the bots.
     */
    def createPlayers(drawings: Seq[DrawMethods], discardings: Seq[DiscardMethods], memories: Seq[Memory]): Players

  /** Represents the model component for the menu. */
  trait Component:
    /** Implementation of [[MainMenuModel]]. */
    class MainMenuModelImpl extends MainMenuModel:
      def createPlayers(drawings: Seq[DrawMethods], discardings: Seq[DiscardMethods], memories: Seq[Memory]): Players =
        CactusPlayer("Player", List.empty) :: drawings
          .lazyZip(discardings)
          .lazyZip(memories)
          .zipWithIndex
          .map { case ((drawMethod, discardMethod, memory), i) =>
            s"Bot ${i + 1}" drawing drawMethod discarding discardMethod withMemory memory
          }
          .toList

  /** Interface of the model module of the menu. */
  trait Interface extends Provider with Component
