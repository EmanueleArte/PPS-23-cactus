package model.player

import model.card.Cards.{Card, PokerCard}
import model.deck.Piles.DiscardPile
import model.card.Cards.Card
import model.deck.Drawable

/** A player of the game. */
object Players:

  /** Represents a generic player. */
  trait Player:
    /** Type representing the type of the cards in a game. */
    type CardType <: Card
    
    /** The name of the player. */
    val name: String

    /** The cards in the player's hand.
     * @return the cards in the player's hand.
     */
    def cards: List[CardType]

    /**
     * Draws a card from a deck.
     *
     * @param drawable the drawable to draw from.
     */
    def draw(drawable: Drawable[CardType]): Unit

    /**
     * Discards a card from the player's hand.
     *
     * @param cardIndex the index of the card in the list to discard.
     * @return the discarded card.
     */
    def discard(cardIndex: Int): CardType

  case class CactusPlayer(name: String, private var _cards: List[PokerCard]) extends Player:
    override type CardType = PokerCard

    override def cards: List[PokerCard] = _cards

    override def draw(drawable: Drawable[CardType]): Unit =
      _cards = _cards ::: drawable.draw().get :: Nil

    override def discard(cardIndex: Int): CardType =
      val cardToRemove: CardType = cards(cardIndex)
      _cards = _cards.zipWithIndex.filter((_, i) => i != cardIndex).map((c, _) => c)
      cardToRemove

  /** Companion object of [[Player]]. */
  object Player
