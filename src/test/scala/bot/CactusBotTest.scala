package bot

import bot.Bots.CactusBot
import card.Cards.Card
import card.CardsData
import model.deck.Decks.Deck
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec


class CactusBotTest extends AnyFlatSpec:

  "Bot " should "have seen 8 of Spades" in:
    val cactusBot: CactusBot = CactusBot(List(Card(8, CardsData.PokerSuit.Spades), Card(2, CardsData.PokerSuit.Diamonds)))
    cactusBot.seeCard(0)
    cactusBot.knownCards shouldBe List(Card(8, CardsData.PokerSuit.Spades))

  "Bot " should "have 5 of Clubs in his hand" in :
    val cactusBot: CactusBot = CactusBot(List.empty)
    val deck: Deck[Card] = Deck(5 to 5, List(CardsData.PokerSuit.Clubs), false)
    cactusBot.draw(deck)
    cactusBot.cards shouldBe List(Card(5, CardsData.PokerSuit.Clubs))

  "Bot " should "not have 8 of Spades in his cards" in:
    val cactusBot: CactusBot = CactusBot(List(Card(8, CardsData.PokerSuit.Spades), Card(2, CardsData.PokerSuit.Diamonds)))
    cactusBot.discard(0)
    cactusBot.cards shouldBe List(Card(2, CardsData.PokerSuit.Diamonds))

  "Bot " should "not have 8 of Spades in his known cards" in:
    val cactusBot: CactusBot = CactusBot(List(Card(8, CardsData.PokerSuit.Spades), Card(2, CardsData.PokerSuit.Diamonds)))
    cactusBot.seeCard(0)
    cactusBot.discard(0)
    cactusBot.knownCards shouldBe List.empty[Card]