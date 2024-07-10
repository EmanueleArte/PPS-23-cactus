package model.deck

import model.card.CardsData.PokerCardName
import model.card.CardsData.PokerCardName.Ace
import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.{Card, PokerCard}
import model.card.CardsData.PokerSuit.Spades
import model.deck.Piles.{DiscardPile, PokerPile}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, defined, empty}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.matchers.must.Matchers

class DiscardPileTest extends AnyFlatSpec:
  type DiscardPile = model.deck.Piles.DiscardPile[PokerCard]

  "A discard pile" should "initially be empty" in:
    PokerPile().size shouldBe 0

  "A discarded card" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile().put(Ace of Spades)
    discardPile.cards should be (List(Ace of Spades))

  "Multiple discarded cards" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace of Spades)
      .put(2 of Spades)
      .put(3 of Spades)
    discardPile.cards should be(List(3 of Spades, 2 of Spades, Ace of Spades))

  "Draw" should "retrieve the last card discarded" in:
    val cardOption: Option[Card] = PokerPile()
      .put(Ace of Spades)
      .put(2 of Spades)
      .put(3 of Spades)
      .draw()
    cardOption shouldBe defined
    cardOption.fold(Nil)(card => card) should be (3 of Spades)
    updatedPile.size shouldBe 2

  "Draw from an empty pile" should "return empty Option" in:
    PokerPile().draw() shouldBe empty

  "Draw" should "remove the card on top of the pile" in:
    val discardPile: DiscardPile = PokerPile().put(Ace of Spades).put(2 of Spades)
    discardPile.draw()
    discardPile.cards should be (List(Ace of Spades))

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

  "After emptying a pile" should "be possible to add cards" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace of Spades)
      .empty()
      .put(2 of Spades)

    discardPile.cards should be (List(2 of Spades))