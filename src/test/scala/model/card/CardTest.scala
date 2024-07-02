package model.card

import model.card.Cards.*
import model.card.CardsData.PokerSuit.{Clubs, Spades}
import model.card.CardsData.{PokerSuit, Suit}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

/** Tests for cards. */
class CardTest extends AnyFlatSpec:

  val card: Card = Card(10, Spades)

  "Generic Card" should "have a non-null value" in:
    card.value shouldBe a [Int]

  it should "have a suit" in:
    card.suit shouldBe a [Suit]

  def twoOfClubs: Card = GenericCard(2, Clubs)

  "The card two of clubs" should "have a value of 2 and Clubs as suit" in:
    twoOfClubs.value should be (2)
    twoOfClubs.suit should be (PokerSuit.Clubs)

  val coverableCard = new GenericCard(5, PokerSuit.Diamonds) with Coverable

  "Coverable card" should "be covered by default" in:
    coverableCard.isCovered should be (true)

  it should "be able to be uncovered" in:
    coverableCard.isCovered should be (true)
    coverableCard.uncover()
    coverableCard.isCovered should be (false)

  it should "be able to be covered" in:
    coverableCard.isCovered should be (false)
    coverableCard.cover()
    coverableCard.isCovered should be (true)
