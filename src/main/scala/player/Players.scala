package player

import card.Cards.Card

/**A player of the game**/
object Players:

  /**Represents a generic player**/
  trait Player:
    /**The cards in the player's hand**/
    def cards: List[Card]

  case class HumanPlayer(l: List[Card]) extends Player:
    override val cards: List[Card] = l;

  /** Companion object of [[Player]]. */
  object Player;
