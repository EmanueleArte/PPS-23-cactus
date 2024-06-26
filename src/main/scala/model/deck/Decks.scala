package model.deck

import card.CardBuilder.PokerDSL.*
import card.CardBuilder.PokerCardNames.*
import card.CardsData.PokerSuit.*

import card.Cards.Card
import card.Cards.PokerCard
import card.CardsData.{PokerSuit, Suit}

import scala.util.Random

object Decks:

  trait Deck:
    type CardType <: Card
    def cards: List[CardType]
    def size: Int
    def shuffle(): Deck
    def draw(): Option[CardType]

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  abstract class DeckImpl(shuffled: Boolean) extends Deck:
    var head: Int = -1
    override def size: Int = cards.size
    override def draw(): Option[CardType] = head match
      case n if n < size - 1 => head = head + 1; Some(cards(head))
      case _                 => Option.empty

  case class GenericDeck(values: Range, suits: List[Suit], shuffled: Boolean)
      extends DeckImpl(shuffled):
    override type CardType = Card

    override def shuffle(): Deck = GenericDeck(values, suits, true)

    override def cards: List[CardType] =
      for
        suit <- if shuffled then Random.shuffle(suits) else suits
        value <- if shuffled then Random.shuffle(values) else values
      yield Card(value, suit)

  case class PokerDeck(shuffled: Boolean) extends DeckImpl(shuffled):
    override type CardType = PokerCard

    private val SUITS: List[PokerSuit] = List(Spades, Diamonds, Clubs, Hearts)
    private val VALUES: Range = Ace to King

    override def shuffle(): Deck = PokerDeck(true)

    override def cards: List[CardType] =
      for
        suit <- if shuffled then Random.shuffle(SUITS) else SUITS;
        value <- if shuffled then Random.shuffle(VALUES) else VALUES
      yield value of suit

  object Deck:
    def apply(values: Range, suits: List[Suit], shuffled: Boolean): Deck =
      GenericDeck(values, suits, shuffled)
