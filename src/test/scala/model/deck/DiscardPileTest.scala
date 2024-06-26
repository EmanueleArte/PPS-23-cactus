package model.deck

import card.CardBuilder.PokerCardNames
import card.CardBuilder.PokerCardNames.Ace
import card.CardBuilder.PokerDSL.of
import card.Cards.PokerCard
import card.CardsData.PokerSuit.Spades
import model.deck.Decks.{DiscardPile, PokerPile}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, defined, empty, have, not}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.matchers.must.Matchers

class DiscardPileTest extends AnyFlatSpec:
  "A discard pile" should "initially be empty" in:
    val discardPile: DiscardPile = PokerPile(List())
    discardPile.size shouldBe 0

  "A discarded card" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile(List())
    val updatedPile: DiscardPile = discardPile.put(Ace of Spades)
    updatedPile.cards should be (List(Ace of Spades))

  "Multiple discarded cards" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile(List())
    val updatedPile: DiscardPile = discardPile
      .put(Ace of Spades)
      .put(2 of Spades)
      .put(3 of Spades)
    updatedPile.cards should be(List(3 of Spades, 2 of Spades, Ace of Spades))
