package model.deck

import model.card.CardsData.PokerCardName
import model.card.CardsData.PokerCardName.Ace
import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.{Card, PokerCard}
import model.card.CardsData.PokerSuit.Spades
import model.deck.Piles.{DiscardPile, PokerPile}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, defined, empty, have, not, an}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.matchers.must.Matchers

class DiscardPileTest extends AnyFlatSpec:
  "A discard pile" should "initially be empty" in:
    val discardPile: DiscardPile = PokerPile()
    discardPile.size shouldBe 0

  "A discarded card" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile()
    val updatedPile: DiscardPile = discardPile.put(Ace of Spades)
    updatedPile.cards should be (List(Ace of Spades))

  "Multiple discarded cards" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile()
    val updatedPile: DiscardPile = discardPile
      .put(Ace of Spades)
      .put(2 of Spades)
      .put(3 of Spades)
    updatedPile.cards should be(List(3 of Spades, 2 of Spades, Ace of Spades))

  "Draw" should "retrieve the last card discarded" in:
    val discardPile: DiscardPile = PokerPile()
    val updatedPile: DiscardPile = discardPile
      .put(Ace of Spades)
      .put(2 of Spades)
      .put(3 of Spades)
    val cardOption: Option[Card] = updatedPile.draw()
    cardOption shouldBe defined
    cardOption.fold(Nil)(card => card) should be (3 of Spades)

  "Draw from an empty pile" should "return empty Option" in:
    val discardPile: DiscardPile = PokerPile()
    val cardOption: Option[Card] = discardPile.draw()
    cardOption shouldBe empty

  "Card not from a certain deck" should "not be put on a pile" in:
    val discardPile: DiscardPile = PokerPile()
    an [IllegalArgumentException] should be thrownBy discardPile.put(Card(1, Spades))

  "Pile" should "be emptiable" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace of Spades)
      .put(2 of Spades)
      .put(3 of Spades)
      .empty()
    discardPile.size shouldBe 0

  "Empty an already empty pile" should "not give an error" in:
    val discardPile: DiscardPile = PokerPile().empty()
    discardPile.size shouldBe 0

  "After emptying a pile" should "be possibile to add cards" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace of Spades)
      .empty()
      .put(2 of Spades)

    discardPile.cards should be (List(2 of Spades))