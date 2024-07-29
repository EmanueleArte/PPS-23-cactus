package model.player

import model.card.Cards.{Card, Coverable, PokerCard}
import model.deck.Piles.DiscardPile
import model.deck.Drawable

/** A player of the game. */
object Players:

  /** Represents a generic player. */
  trait Player:
    /** Type representing the type of the cards in a game. */
    type CardType <: Card & Coverable
    
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

    /**
     * Compares two players, returning `true` if they are equal.
     * @param anotherPlayer to compare with the player.
     * @return `true` if the players are equal, `false` otherwise.
     */
    def isEqualsTo(anotherPlayer: Player): Boolean

  case class CactusPlayer(name: String, private var _cards: List[PokerCard]) extends Player:
    override type CardType = PokerCard

    override def cards: List[PokerCard] = _cards

    override def draw(drawable: Drawable[CardType]): Unit =
      val drawnCard = drawable.draw().get
      drawnCard.cover()
      _cards = _cards ::: drawnCard :: Nil

    override def discard(cardIndex: Int): CardType =
      val cardToRemove: CardType = cards(cardIndex)
      _cards = _cards.zipWithIndex.filter((_, i) => i != cardIndex).map((c, _) => c)
      cardToRemove

    override def isEqualsTo(anotherPlayer: Player): Boolean = this.name.compareTo(anotherPlayer.name) == 0 &&
        this.cards.diff(anotherPlayer.cards).isEmpty &&
        anotherPlayer.cards.diff(this.cards).isEmpty

  /** Companion object of [[Player]]. */
  object Player
