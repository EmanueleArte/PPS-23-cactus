package card

import card.CardsData.*

/**
 * Cards with a value and a suit implementation.
 */
object Cards:
  /**
   * Represents a card with a value and a suit.
   *
   * @param value the value of the card
   * @param suit the suit of the card
   */
  case class Card(value: Int, suit: Suit)
