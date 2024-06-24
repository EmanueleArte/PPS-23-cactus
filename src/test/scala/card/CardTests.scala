package card

import card.CardsData.*
import card.Cards.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

import scala.language.postfixOps

class CardTests extends AnyFlatSpec:
  def card: Card = Card(10, Spades)

  "A Card" should "have an integer value" in:
    card.value shouldBe a [Int]

  "A Card" should "have a suit" in:
    card.suit shouldBe a [Suit]



