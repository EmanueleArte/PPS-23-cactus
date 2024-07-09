package player

import model.card.Cards.{Card, PokerCard}
import model.deck.Piles.DiscardPile
import model.card.Cards.Card
import model.deck.Drawable

/** A player of the game. */
object Players:

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /**
   * Represents a generic player
   *
   * @tparam C type of the card item. C needs to be at least a [[Card]].
   */
  trait Player[C <: Card]:
    /** The name of the player. */
    val name: String

    /** The cards in the player's hand. */
    var cards: List[C]

    /**
     * Draws a card from a deck.
     *
     * @param drawable the drawable to draw from.
     */
    def draw(drawable: Drawable[C]): Unit

    /**
     * Discards a card from the player's hand.
     *
     * @param cardIndex the index of the card in the list to discard.
     * @return the discarded card.
     */
    def discard(cardIndex: Int): C

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class CactusPlayer[C <: Card](name: String, var cards: List[C]) extends Player[PokerCard]:
    override def draw(drawable: Drawable[C]): Unit =
      cards = cards :+ drawable.draw().get

    override def discard(cardIndex: Int): C =
      val cardToRemove: C = cards(cardIndex)
      cards = cards.zipWithIndex.filter((_, i) => i != cardIndex).map((c, _) => c)
      cardToRemove

  /** Companion object of [[Player]]. */
  object Player;
