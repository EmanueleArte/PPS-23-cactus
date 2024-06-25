package card

/** Values and suits for a card. */
object CardsData:
  /** Represents the suit of a card. */
  trait Suit

  /** Represents the suit of a poker card. */
  enum PokerSuit extends Suit:
    case Hearts, Diamonds, Clubs, Spades

  /** Represents the names of poker cards associated to their values. */
  object PokerCardNames extends Enumeration:
    val Ace   = 1
    val Jack  = 11
    val Queen = 12
    val King  = 13
