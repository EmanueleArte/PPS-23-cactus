package model.deck

import model.card.CardsData.PokerCardName.*
import model.card.CardBuilder.PokerDSL.OF
import model.card.Cards.{Card, Coverable, PokerCard}
import model.card.CardsData.PokerSuit.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, defined, empty, not}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import model.deck.Decks.{Deck, PokerDeck}
import org.scalatest.matchers.must.Matchers

import scala.collection.immutable.List

class DecksTest extends AnyFlatSpec:
  type Deck = model.deck.Decks.Deck[PokerCard & Coverable]
  val cardsList: List[PokerCard & Coverable] = List(
    Ace OF Spades, 2 OF Spades, 3 OF Spades, 4 OF Spades, 5 OF Spades, 6 OF Spades, 7 OF Spades, 8 OF Spades, 9 OF Spades, 10 OF Spades, Jack OF Spades, Queen OF Spades, King OF Spades,
    Ace OF Diamonds, 2 OF Diamonds, 3 OF Diamonds, 4 OF Diamonds, 5 OF Diamonds, 6 OF Diamonds, 7 OF Diamonds, 8 OF Diamonds, 9 OF Diamonds, 10 OF Diamonds, Jack OF Diamonds, Queen OF Diamonds, King OF Diamonds,
    Ace OF Clubs, 2 OF Clubs, 3 OF Clubs, 4 OF Clubs, 5 OF Clubs, 6 OF Clubs, 7 OF Clubs, 8 OF Clubs, 9 OF Clubs, 10 OF Clubs, Jack OF Clubs, Queen OF Clubs, King OF Clubs,
    Ace OF Hearts, 2 OF Hearts, 3 OF Hearts, 4 OF Hearts, 5 OF Hearts, 6 OF Hearts, 7 OF Hearts, 8 OF Hearts, 9 OF Hearts, 10 OF Hearts, Jack OF Hearts, Queen OF Hearts, King OF Hearts
  )

  "A deck" should "contain 52 cards" in:
    val deck: Deck = PokerDeck()
    deck.size shouldBe 52

  "New deck" should "be unshuffled" in :
    val deck: Deck = PokerDeck()
    deck.cards should be (cardsList)

  "Shuffled deck" should "has cards ordered differently than the initial order" in:
    val shuffledDeck: Deck = PokerDeck(shuffled = true)

    shuffledDeck.cards should not equal cardsList

  it should "be shuffled more times" in:
    val deck: Deck = PokerDeck(shuffled = true)
    val deckShuffled: Deck = deck.shuffle()
    deck.cards should not equal deckShuffled.cards

  it should "be possible to draw the first card" in:
    val deck: Deck = PokerDeck()
    val cardOption: Option[Card & Coverable] = deck.draw()
    cardOption shouldBe defined
    cardOption.fold(None)(card => card) should be (Ace OF Spades)

  "After drawing, the head OF the deck" should "be different" in:
    val deck: Deck = PokerDeck()
    deck.draw()
    val cardOption: Option[Card & Coverable] = deck.draw()
    cardOption shouldBe defined
    cardOption.fold(None)(card => card) should be (2 OF Spades)

  "After drawing the last card, the deck" should "be empty" in:
    val deck: Deck = PokerDeck()
    for (i <- 1 to 52) deck.draw()
    deck.draw() shouldBe empty

  "Drawing more cards after the deck ends" should "retrieve empty options" in:
    val deck: Deck = PokerDeck()
    for (i <- 1 to 52) deck.draw()
    deck.draw() shouldBe empty
    deck.draw() shouldBe empty

  "A deck" should "be resettable" in:
    val deck: Deck = PokerDeck()
    deck.draw()
    deck.draw()
    val cardOption: Option[Card & Coverable] = deck.reset().draw()
    cardOption shouldBe defined
    cardOption.fold(Nil)(card => card) shouldBe (Ace OF Spades)

  "A resetted shuffled deck" should "maintain the order" in:
    val deck: Deck = PokerDeck(true)
    val firstCardOption: Option[Card & Coverable] = deck.draw()
    val firstCardAfterResetOption: Option[Card & Coverable] = deck.reset().draw()
    firstCardOption shouldBe defined
    firstCardAfterResetOption shouldBe defined
    firstCardOption.fold(Nil)(card => card) should be
      firstCardAfterResetOption.fold(Nil)(card => card)

  "Drawing cards" should "reduce deck's size" in:
    val deck: Deck = PokerDeck()
    val drawnCards: Int = 5
    for (i <- 1 to drawnCards) deck.draw()
    deck.size shouldBe 52 - drawnCards