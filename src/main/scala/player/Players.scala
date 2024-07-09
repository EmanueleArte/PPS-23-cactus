package player

import model.card.Cards.{Card, PokerCard}
import model.deck.Piles.DiscardPile
import model.card.Cards.Card
import model.deck.Drawable

/** A player of the game. */
object Players:

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /** Represents a generic player */
  trait Player:
    /** Type representing the type of the cards in the hand of a player. */
    type CardType <: Card

    /** The name of the player. */
    val name: String

    /** The cards in the player's hand. */
    var cards: List[CardType]

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

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class CactusPlayer(name: String, var cards: List[PokerCard]) extends Player:
    override type CardType = PokerCard

    override def draw(drawable: Drawable[CardType]): Unit =
      cards = cards :+ drawable.draw().get

    override def discard(cardIndex: Int): CardType =
      val cardToRemove: CardType = cards(cardIndex)
      cards = cards.zipWithIndex.filter((_, i) => i != cardIndex).map((c, _) => c)
      cardToRemove

  /** Companion object of [[Player]]. */
  object Player;
