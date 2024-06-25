package model.deck

import card.CardBuilder.PokerCardNames.*
import card.Cards.{Card, PokerCard}
import card.CardsData.PokerSuit.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, have}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import model.deck.Decks.*

class DecksTest extends AnyFlatSpec:

  "A deck" should "contain 52 cards" in:
    val deck: Deck = PokerDeck()
    deck.size shouldBe 52



