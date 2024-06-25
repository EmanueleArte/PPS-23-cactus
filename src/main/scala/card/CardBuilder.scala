package card

import card.CardsData.PokerSuit
import card.Cards.*

/** Builder for creating cards. */
object CardBuilder:

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
