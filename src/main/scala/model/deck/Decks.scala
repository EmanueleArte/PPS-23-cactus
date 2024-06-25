package model.deck

import card.CardBuilder.PokerDSL.*
import card.CardBuilder.PokerCardNames.*
import card.CardsData.PokerSuit.*

import scala.collection.immutable.HashSet
import card.Cards.Card
import card.Cards.PokerCard
import card.CardsData.{PokerSuit, Suit}

object Decks:

  trait Deck:
    type CardType
    def drawPile: List[CardType]
    def size: Int

  case class GenericDeck(values: Range, suits: Array[Suit]) extends Deck:
    override type CardType = Card

    override def size: Int = drawPile.size

    override def drawPile: List[CardType] =
      (for
        suit <- suits
        value <- values
      yield Card(value, suit)).toList

  case class PokerDeck() extends Deck:
    override type CardType = PokerCard

    override def size: Int = drawPile.size

    override def drawPile: List[CardType] =
      (for
        suit <- Array(Spades, Diamonds, Clubs, Hearts);
        value <- Ace to King
      yield value of suit).toList

  object Deck:
    def apply(values: Range, suits: Array[Suit]): Deck =
      GenericDeck(values, suits)
