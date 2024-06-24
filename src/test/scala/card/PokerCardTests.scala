package card

import card.CardsData.*
import card.Cards.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

class PokerCardTests extends AnyFlatSpec:

  "A PokerCard" should "have an value between 1 to 13" in:
    intercept[IllegalArgumentException]:
      PokerCard(-3, Suit.Spades)
    intercept[IllegalArgumentException]:
      PokerCard(14, Suit.Spades)



