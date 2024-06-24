package card

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import scala.language.postfixOps

class CardTests extends AnyFlatSpec:
  def card = Card(10, "Spades")

  "A Card" should "have an integer value" in:
    card10spades.value should be a Int
    

