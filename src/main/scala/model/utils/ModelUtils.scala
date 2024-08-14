package model.utils

import model.card.Cards.PokerCard
import model.card.CardsData.{PokerCardName, PokerSuit}

/** Represents the common functions of the model part. */
object ModelUtils:
  /**
   * Checks if the given [[PokerCard]] is a red king.
   *
   * @param c the card to check
   * @return `true` if the given card is a red king, `false` otherwise
   */
  def isRedKing(c: PokerCard): Boolean = c.value match
    case PokerCardName.King => c.suit == PokerSuit.Hearts || c.suit == PokerSuit.Diamonds
    case _                  => false
