package model.bot

import model.bot.Bots.{CactusBotImpl, DiscardMethods, DrawMethods}
import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.PokerCard
import model.card.CardsData.PokerCardName.Ace
import model.card.CardsData.PokerSuit.Spades
import model.deck.Decks.PokerDeck
import model.deck.Piles.{DiscardPile, PokerPile}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

@SuppressWarnings(Array("org.wartremover.warts.All"))
class CactusBotTest extends AnyFlatSpec:
  private val deck: PokerDeck = PokerDeck()

  "Bot " should "know Ace of Spades" in:
    val cactusBot: CactusBotImpl = CactusBotImpl("", List.empty[PokerCard], DrawMethods.Deck, DiscardMethods.Known, model.bot.Bots.Memory.Optimal)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    cactusBot.knownCards.head should be (Ace of Spades)

  "Bot " should "not have a card in the known cards list" in :
    val cactusBot: CactusBotImpl = CactusBotImpl("", List.empty[PokerCard], DrawMethods.Deck, DiscardMethods.Known, model.bot.Bots.Memory.Optimal)
    cactusBot.draw(deck)
    cactusBot.discard(0)
    cactusBot.knownCards.isEmpty shouldBe true

  "Bot's index card to discard " should "be 3" in:
    val cactusBot: CactusBotImpl = CactusBotImpl("", List.empty[PokerCard], DrawMethods.Deck, DiscardMethods.Known, model.bot.Bots.Memory.Optimal)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(1)
    cactusBot.seeCard(3)
    cactusBot.chooseDiscard() shouldBe 3

  "Bot's index card to discard " should "be 1" in:
    val cactusBot: CactusBotImpl = CactusBotImpl("", List.empty[PokerCard], DrawMethods.Deck, DiscardMethods.Unknown, model.bot.Bots.Memory.Optimal)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    cactusBot.chooseDiscard() shouldBe 1

  "Bot  " should "draw from pile" in :
    val cactusBot: CactusBotImpl = CactusBotImpl("", List.empty[PokerCard], DrawMethods.PileSmartly, DiscardMethods.Unknown, model.bot.Bots.Memory.Optimal)
    var discardPile: DiscardPile[PokerCard] = PokerPile()
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.draw(deck)
    cactusBot.seeCard(0)
    discardPile = discardPile.put(cactusBot.discard(2))
    cactusBot.chooseDraw(discardPile) shouldBe true