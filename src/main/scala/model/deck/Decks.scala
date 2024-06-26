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
    type CardType
    def cards: List[CardType]
    def size: Int = cards.size
    def shuffle(): Deck

  case class GenericDeck(values: Range, suits: List[Suit], shuffled: Boolean)
      extends Deck:
    override type CardType = Card

    override def shuffle(): Deck = GenericDeck(values, suits, true)

    override def cards: List[CardType] =
      for
        suit <- if shuffled then Random.shuffle(suits) else suits
        value <- if shuffled then Random.shuffle(values) else values
      yield Card(value, suit)

  case class PokerDeck(shuffled: Boolean) extends Deck:
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
