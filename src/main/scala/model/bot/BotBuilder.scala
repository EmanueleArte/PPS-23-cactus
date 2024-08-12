package model.bot

import model.bot.Bots.CactusBotImpl
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.card.Cards.{Coverable, PokerCard}

/** Builder for creating a cactus bot. */
object BotBuilder:
  class CactusBotImplWithDrawMethod(val name: String, val drawMethod: DrawMethods)
  class CactusBotImplWithDiscardMethod(val c: CactusBotImplWithDrawMethod, val discardMethod: DiscardMethods)

  object CactusBotDSL:
    extension (name: String)
      def drawing(drawMethod: DrawMethods): CactusBotImplWithDrawMethod = CactusBotImplWithDrawMethod(name, drawMethod)

    extension (c: CactusBotImplWithDrawMethod)
      def discarding(discardMethod: DiscardMethods): CactusBotImplWithDiscardMethod =
        CactusBotImplWithDiscardMethod(c, discardMethod)

    extension (c: CactusBotImplWithDiscardMethod)
      def withMemory(memory: Memory): CactusBotImpl =
        CactusBotImpl(c.c.name, List.empty[PokerCard & Coverable], c.c.drawMethod, c.discardMethod, memory)
