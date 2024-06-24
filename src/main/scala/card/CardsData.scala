package card

/**
 * Values and suits for a card.
 */
object CardsData:
  /**
   * Represents the suit of a card.
   */
  sealed trait Suit
  /** Suit of hearts. */
  case object Hearts extends Suit
  /** Suit of diamonds. */
  case object Diamonds extends Suit
  /** Suit of clubs. */
  case object Clubs extends Suit
  /** Suit of spades. */
  case object Spades extends Suit