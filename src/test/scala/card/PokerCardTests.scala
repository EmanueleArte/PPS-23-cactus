package card

import card.CardsData.*
import card.Cards.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

class PokerCardTests extends AnyFlatSpec:

  "A Poker card" should "have an value between 1 to 13" in:
    intercept[IllegalArgumentException]:
      PokerCard(-3, PokerSuit.Spades)
    intercept[IllegalArgumentException]:
      PokerCard(14, PokerSuit.Spades)
