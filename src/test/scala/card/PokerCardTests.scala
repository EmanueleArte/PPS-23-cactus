package card

import card.CardsData.*
import PokerCardName.*
import PokerSuit.*
import card.Cards.*
import card.CardBuilder.PokerDSL.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

/** Tests for poker cards. */
class PokerCardTests extends AnyFlatSpec:

  "Poker card" should "have an value between 1 to 13" in:
    intercept[IllegalArgumentException]:
      PokerCard(-3, Spades)
    intercept[IllegalArgumentException]:
      PokerCard(14, Spades)

  "Poker card created with DSL" should "have same value and suit as one created using constructor" in:
    10 of Spades should be (PokerCard(10, Spades))

  "Poker card created with poker names" should "have same value and suit as one created using constructor" in:
    Ace of Clubs should be (PokerCard(1, Clubs))
    Jack of Diamonds should be (PokerCard(11, Diamonds))
    Queen of Hearts should be (PokerCard(12, Hearts))
    King of Spades should be (PokerCard(13, Spades))

  "Coverable Poker card created with DSL" should "be covered when created" in:
    (3 OF Clubs).isCovered should be (true)

  it should "be uncovered after it has been uncovered" in:
    val card = 3 OF Clubs
    card.isCovered should be (true)
    card.uncover()
    card.isCovered should be (false)
