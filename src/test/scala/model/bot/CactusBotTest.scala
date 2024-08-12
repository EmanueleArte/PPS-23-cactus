package model.bot

import model.bot.BotBuilder.CactusBotDSL.{discarding, drawing, withMemory}
import model.bot.Bots.CactusBotImpl
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods}
import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.{Coverable, PokerCard}
import model.card.CardsData.PokerCardName.Ace
import model.card.CardsData.PokerSuit.Spades
import model.deck.Decks.PokerDeck
import model.deck.Piles.PokerPile
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

@SuppressWarnings(Array("org.wartremover.warts.All"))
class CactusBotTest extends AnyFlatSpec:
  private val deck: PokerDeck = PokerDeck()

  "Bot " should "know Ace of Spades" in:
    val cactusBot: CactusBotImpl = CactusBotImpl(
      "",
      List.empty[PokerCard & Coverable],
      DrawMethods.Deck,
      DiscardMethods.Known,
      model.bot.CactusBotsData.Memory.Optimal
    )
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    cactusBot.knownCards.head should be(Ace of Spades)

  "Bot " should "not have a card in the known cards list" in:
    val cactusBot: CactusBotImpl = CactusBotImpl(
      "",
      List.empty[PokerCard & Coverable],
      DrawMethods.Deck,
      DiscardMethods.Known,
      model.bot.CactusBotsData.Memory.Optimal
    )
    cactusBot.draw(deck)
    cactusBot.discard(0)
    cactusBot.knownCards.isEmpty shouldBe true

  "Bot's index card to discard " should "be 3" in:
    val cactusBot: CactusBotImpl = CactusBotImpl(
      "",
      List.empty[PokerCard & Coverable],
      DrawMethods.Deck,
      DiscardMethods.Known,
      model.bot.CactusBotsData.Memory.Optimal
    )
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(1)
    cactusBot.seeCard(3)
    cactusBot.chooseDiscard() shouldBe 3

  "Bot's index card to discard " should "be 1" in:
    val cactusBot: CactusBotImpl = CactusBotImpl(
      "",
      List.empty[PokerCard & Coverable],
      DrawMethods.Deck,
      DiscardMethods.Unknown,
      model.bot.CactusBotsData.Memory.Optimal
    )
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    cactusBot.chooseDiscard() shouldBe 1

  "Bot " should "draw from pile" in:
    val cactusBot: CactusBotImpl = CactusBotImpl(
      "",
      List.empty[PokerCard & Coverable],
      DrawMethods.PileSmartly,
      DiscardMethods.Unknown,
      model.bot.CactusBotsData.Memory.Optimal
    )
    var discardPile: PokerPile = PokerPile()
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    discardPile = discardPile.put(cactusBot.discard(2))
    cactusBot.chooseDraw(discardPile) shouldBe true

  "Bot " should "call cactus" in:
    val cactusBot: CactusBotImpl = CactusBotImpl(
      "",
      List.empty[PokerCard & Coverable],
      DrawMethods.PileSmartly,
      DiscardMethods.Unknown,
      model.bot.CactusBotsData.Memory.Optimal
    )
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    cactusBot.seeCard(1)
    cactusBot.seeCard(2)
    cactusBot.shouldCallCactus() shouldBe true

  "Bot " should "not call cactus" in:
    val cactusBot: CactusBotImpl = CactusBotImpl(
      "",
      List.empty[PokerCard & Coverable],
      DrawMethods.PileSmartly,
      DiscardMethods.Unknown,
      model.bot.CactusBotsData.Memory.Optimal
    )
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    cactusBot.seeCard(1)
    cactusBot.seeCard(2)
    cactusBot.shouldCallCactus() shouldBe false

  "Bot " should "be created" in:
    val cactusBot =
      "Bot" drawing DrawMethods.Deck discarding DiscardMethods.Known withMemory model.bot.CactusBotsData.Memory.Good
    cactusBot shouldBe a[CactusBotImpl]
