package player

import card.Cards.Card
import model.deck.Drawable

/** A player of the game. */
object Players:

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /** Represents a generic player. */
  trait Player:
    /** The cards in the player's hand. */
    var cards: List[Card]

    /**
     * Draws a card from a deck.
     * @param deck the deck to draw from
     */
    def draw(drawable: Drawable[_ <: Card]): Unit

    /**
     * Discards a card from the player's hand.
     * @param cardIndex the index of the card in the list to discard
     * @return the discarded card
     */
    def discard(cardIndex: Int): Card
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class CactusPlayer(var cards: List[Card]) extends Player:
    override def draw(drawable: Drawable[_ <: Card]): Unit =
      cards = cards :+ drawable.draw().get

    override def discard(cardIndex: Int): Card =
      val cardToRemove: Card = cards(cardIndex)
      cards = cards.zipWithIndex.filter((_, i) => i != cardIndex).map((c, _) => c)
      cardToRemove

  /** Companion object of [[Player]]. */
  object Player;
