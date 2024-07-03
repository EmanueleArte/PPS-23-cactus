package model.deck

import model.card.CardsData.PokerCardName.*
import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.{Card, PokerCard}
import model.card.CardsData.PokerSuit.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, defined, empty, have, not}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import model.deck.Decks.{Deck, PokerDeck}
import org.scalatest.matchers.must.Matchers

import scala.collection.immutable.List

@SuppressWarnings(Array("org.wartremover.warts.All"))
class DecksTest extends AnyFlatSpec:
  val cardsList: List[PokerCard] = List(
    Ace of Spades, 2 of Spades, 3 of Spades, 4 of Spades, 5 of Spades, 6 of Spades, 7 of Spades, 8 of Spades, 9 of Spades, 10 of Spades, Jack of Spades, Queen of Spades, King of Spades,
    Ace of Diamonds, 2 of Diamonds, 3 of Diamonds, 4 of Diamonds, 5 of Diamonds, 6 of Diamonds, 7 of Diamonds, 8 of Diamonds, 9 of Diamonds, 10 of Diamonds, Jack of Diamonds, Queen of Diamonds, King of Diamonds,
    Ace of Clubs, 2 of Clubs, 3 of Clubs, 4 of Clubs, 5 of Clubs, 6 of Clubs, 7 of Clubs, 8 of Clubs, 9 of Clubs, 10 of Clubs, Jack of Clubs, Queen of Clubs, King of Clubs,
    Ace of Hearts, 2 of Hearts, 3 of Hearts, 4 of Hearts, 5 of Hearts, 6 of Hearts, 7 of Hearts, 8 of Hearts, 9 of Hearts, 10 of Hearts, Jack of Hearts, Queen of Hearts, King of Hearts
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
    val cardOption: Option[Card] = deck.draw()
    cardOption shouldBe defined
    cardOption.fold(None)(card => card) should be (Ace of Spades)

  "After drawing, the head of the deck" should "be different" in:
    val deck: Deck = PokerDeck()
    deck.draw()
    val cardOption: Option[Card] = deck.draw()
    cardOption shouldBe defined
    cardOption.fold(None)(card => card) should be (2 of Spades)

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
    val cardOption: Option[Card] = deck.reset().draw()
    cardOption shouldBe defined
    cardOption.fold(Nil)(card => card) shouldBe (Ace of Spades)

  "A resetted shuffled deck" should "maintain the order" in:
    val deck: Deck = PokerDeck(true)
    val firstCardOption: Option[Card] = deck.draw()
    val firstCardAfterResetOption: Option[Card] = deck.reset().draw()
    firstCardOption shouldBe defined
    firstCardAfterResetOption shouldBe defined
    firstCardOption.fold(Nil)(card => card) should be
      (firstCardAfterResetOption.fold(Nil)(card => card))

  "Drawing cards" should "reduce deck's size" in:
    val deck: Deck = PokerDeck()
    val drawnCards: Int = 5
    for (i <- 1 to drawnCards) deck.draw()
    deck.size shouldBe 52 - drawnCards

  "Deck" should "be resettable using a discard pile" in:
    import model.deck.Piles.DiscardPile
    val cardsNumber: Int = 3
    val deck: Deck = Deck(1 to cardsNumber, List(Spades), shuffled = false)
    val pile: DiscardPile = DiscardPile()
      .put(deck.draw().get)
      .put(deck.draw().get)
      .put(deck.draw().get)
    deck.reset(pile).cards should be (List(Card(1, Spades), Card(2, Spades), Card(3, Spades)))

  "Resetting a deck using a partial discard pile" should "create a deck with only the cards of the discard pile" in:
    import model.deck.Piles.DiscardPile
    val cardsNumber: Int = 4
    val deck: Deck = Deck(1 to cardsNumber, List(Spades), shuffled = false)
    // Drawn cards are not put on the pile...
    deck.draw()
    deck.draw()
    // ... only the last 2 cards are put on the pile
    val pile: DiscardPile = DiscardPile()
      .put(deck.draw().get)
      .put(deck.draw().get)
    deck.reset(pile).cards should be (List(Card(3, Spades), Card(4, Spades)))

