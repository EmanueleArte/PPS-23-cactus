package card

import card.CardsData.PokerSuit
import card.Cards.*

/** Builder for creating cards. */
object CardBuilder:

  /** Represents the names of poker cards associated to their values. */
  object PokerCardNames extends Enumeration:
    val Ace = 1
    val Jack = 11
    val Queen = 12
    val King = 13

  /** A DSL definition for [[PokerCard]]. */
  object PokerDSL:
    extension (value: Int)
      /**
       * Creates a poker card with the given value and suit. Using the syntax `value of suit` (e.g. 5 of Spades).
       *
       * @param suit the suit of the card
       * @return a poker card with the given value and suit
       */
      def of(suit: PokerSuit): PokerCard = PokerCard(value, suit)
