package model.deck

import model.card.CardsData.PokerCardName
import model.card.CardsData.PokerCardName.Ace
import model.card.CardBuilder.PokerDSL.OF
import model.card.Cards.{Card, Coverable, PokerCard}
import model.card.CardsData.PokerSuit.Spades
import model.deck.Piles.{DiscardPile, PokerPile}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, defined, empty}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.matchers.must.Matchers

/** Test for discard pile. */
class DiscardPileTest extends AnyFlatSpec:
  /** Type of the discard pile. */
  type DiscardPile = model.deck.Piles.DiscardPile[PokerCard & Coverable]

  "A discard pile" should "initially be empty" in:
    PokerPile().size shouldBe 0

  "A discarded card" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile().put(Ace OF Spades)
    discardPile.cards should be(List(Ace OF Spades))

  "Multiple discarded cards" should "be added on the pile" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace OF Spades)
      .put(2 OF Spades)
      .put(3 OF Spades)
    discardPile.cards should be(List(3 OF Spades, 2 OF Spades, Ace OF Spades))

  "Draw" should "retrieve the last card discarded" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace OF Spades)
      .put(2 OF Spades)
      .put(3 OF Spades)
    val cardOption: Option[Card & Coverable] = discardPile.draw()
    cardOption shouldBe defined
    cardOption.fold(Nil)(card => card) should be(3 OF Spades)
    discardPile.size shouldBe 2

  "Draw from an empty pile" should "return empty Option" in:
    PokerPile().draw() shouldBe empty

  "Draw" should "remove the card on top OF the pile" in:
    val discardPile: DiscardPile = PokerPile().put(Ace OF Spades).put(2 OF Spades)
    discardPile.draw()
    discardPile.cards should be(List(Ace OF Spades))

  "Pile" should "be emptiable" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace OF Spades)
      .put(2 OF Spades)
      .put(3 OF Spades)
      .empty()
    discardPile.size shouldBe 0

  "Empty an already empty pile" should "not give an error" in:
    val discardPile: DiscardPile = PokerPile().empty()
    discardPile.size shouldBe 0

  "After emptying a pile" should "be possible to add cards" in:
    val discardPile: DiscardPile = PokerPile()
      .put(Ace OF Spades)
      .empty()
      .put(2 OF Spades)

    discardPile.cards should be(List(2 OF Spades))
