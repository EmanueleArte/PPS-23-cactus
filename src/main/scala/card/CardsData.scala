package card

/**
 * Values and suits for a card.
 */
object CardsData:
  /**
   * Represents the suit of a card.
   */
  trait Suit
  
  /**
   * Represents the suit of a poker card.
   */
  enum PokerSuit extends Suit:
    case Hearts, Diamonds, Clubs, Spades
