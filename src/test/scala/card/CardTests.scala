package card

import card.CardsData.*
import card.Cards.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

class CardTests extends AnyFlatSpec:
  def card: Card = GenericCard(10, PokerSuit.Spades)

  "A Card" should "have an integer value" in:
    card.value shouldBe a [Int]

  "A Card" should "have a suit" in:
    card.suit shouldBe a [Suit]

  def twoOfClubs: Card = GenericCard(2, PokerSuit.Clubs)

  "The card two of clubs" should "have a value of 2 and Clubs as suit" in:
    twoOfClubs.value should be (2)
    twoOfClubs.suit should be (PokerSuit.Clubs)

  

