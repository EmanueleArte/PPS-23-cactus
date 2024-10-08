package model.deck

import model.card.CardsData.PokerCardName.*
import model.card.CardBuilder.PokerDSL.{OF, of}
import model.card.Cards.{Card, Coverable, PokerCard}
import model.card.CardsData.PokerSuit.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import model.deck.Decks.{Deck, PokerDeck}
import model.deck.Piles.PokerPile
import org.scalatest.matchers.must.Matchers

/** Test for deck and discard pile. */
class DeckAndDiscardPileTest extends AnyFlatSpec:

  "Deck" should "be resettable using a discard pile" in:
    import model.deck.Piles.DiscardPile
    val cardsNumber: Int             = 3
    val deck: Deck[Card & Coverable] = Deck(1 to cardsNumber, List(Spades), shuffled = false)
    val pile: DiscardPile[Card & Coverable] = DiscardPile()
      .put(deck.draw().get)
      .put(deck.draw().get)
      .put(deck.draw().get)
    deck.resetWithPile(pile).cards should be(List(Card(1, Spades), Card(2, Spades), Card(3, Spades)))

  "Resetting a deck using a partial discard pile" should "create a deck with only the cards of the discard pile" in:
    import model.deck.Piles.DiscardPile
    val cardsNumber: Int             = 4
    val deck: Deck[Card & Coverable] = Deck(1 to cardsNumber, List(Spades), shuffled = false)
    // Drawn cards are not put on the pile...
    deck.draw()
    deck.draw()
    // ... only the last 2 cards are put on the pile
    val pile: DiscardPile[Card & Coverable] = DiscardPile()
      .put(deck.draw().get)
      .put(deck.draw().get)
    deck.resetWithPile(pile).cards should be(List(Card(3, Spades), Card(4, Spades)))

  "Player" should "draw from both deck and discard pile" in:
    val drawableDeck: Drawable[PokerCard & Coverable] = PokerDeck()
    val drawablePile: Drawable[PokerCard & Coverable] = PokerPile().put(King OF Hearts)
    drawableDeck.draw() should be(Some(Ace of Spades))
    drawablePile.draw() should be(Some(King of Hearts))
