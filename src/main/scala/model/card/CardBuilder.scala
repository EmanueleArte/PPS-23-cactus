package model.card

import CardsData.PokerSuit
import Cards.*

/** Builders for creating cards. */
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

      /**
       * Creates a coverable poker card with the given value and suit. Using the syntax `value OF suit`
       * (e.g. 5 OF Spades).
       *
       * @param suit the suit of the card
       * @return a coverable poker card with the given value and suit
       */
      def OF(suit: PokerSuit): PokerCard & Coverable = new PokerCard(value, suit) with Coverable
