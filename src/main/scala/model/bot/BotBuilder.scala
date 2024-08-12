package model.bot

import model.bot.Bots.{CactusBotImpl, CactusBot}
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.card.Cards.{Coverable, PokerCard}

/** Builder for creating a cactus bot. */
object BotBuilder:
  /**
   * An intermediate class for the DSL of [[CactusBot]].
   * @param name the name of the bot.
   * @param drawMethod the draw method of the bot.
   */
  class CactusBotImplWithDrawMethod(val name: String, val drawMethod: DrawMethods)

  /**
   * An intermediate class for the DSL of [[CactusBot]].
   * @param c the [[CactusBotImplWithDrawMethod]] from which to start.
   * @param discardMethod the discard method of the bot.
   */
  class CactusBotImplWithDiscardMethod(val c: CactusBotImplWithDrawMethod, val discardMethod: DiscardMethods)

  /** A DSL definition for a [[CactusBot]]. */
  object CactusBotDSL:
    extension (name: String)
      /**
      * Creates a not complete [[CactusBot]]. This function has to be used with discarding and withMemory functions.
      * @param drawMethod the draw method of the bot.
      * @return a not complete [[CactusBot]].
      */
      def drawing(drawMethod: DrawMethods): CactusBotImplWithDrawMethod = CactusBotImplWithDrawMethod(name, drawMethod)

    extension (c: CactusBotImplWithDrawMethod)
      /**
      * Creates a not complete [[CactusBot]]. This function has to be used with the withMemory function.
      * @param discardMethod the discard method of the bot.
      * @return a not complete [[CactusBot]].
      */
      def discarding(discardMethod: DiscardMethods): CactusBotImplWithDiscardMethod =
        CactusBotImplWithDiscardMethod(c, discardMethod)

    extension (c: CactusBotImplWithDiscardMethod)
      /**
      * Creates a [[CactusBot]].
      * @param memory the [[Memory]] of the bot.
      * @return a [[CactusBot]].
      */
      def withMemory(memory: Memory): CactusBotImpl =
        CactusBotImpl(c.c.name, List.empty[PokerCard & Coverable], c.c.drawMethod, c.discardMethod, memory)
