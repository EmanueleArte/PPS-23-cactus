package card

import card.CardsData.*

/**
 * Cards with a value and a suit implementation.
 */
object Cards:
  type CardValue = Int
  
  /**
   * Represents a card with a value and a suit.
   */
  trait Card:
    /**
     * The value of the card.
     */
    def value: CardValue
    /**
     * The suit of the card.
     */
    def suit: Suit

  /**
   * Represents generic a card with a value and a suit.
   *
   * @param value the value of the card
   * @param suit the suit of the card
   */
  case class GenericCard(value: CardValue, suit: Suit) extends Card

  /**
   * Represents a poker card with a value and a suit.
   *
   * @param value the value of the card that ranges from 1 to 13
   * @param suit the suit of the card
   */
  case class PokerCard(value: CardValue, suit: Suit) extends Card:
    require(value >= 1, "Card value cannot be less than 1")
    require(value <= 13, "Card value cannot be greater than 13")
