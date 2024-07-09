package bot

import card.Cards.Card
import player.Players.CactusPlayer

/** The bots of the game */
object Bots:

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /** Represents a bot. */
  trait Bot:
    /** The cards that the bot knows. */
    var knownCards: List[Card]

    /** Let the [[Bot]] see a [[Card]].
     * @param cardIndex the index of the [[Card]] in the list of the cards.
     */
    def seeCard(cardIndex: Int): Unit;

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class CactusBot(cards: List[Card]) extends CactusPlayer(cards) with Bot:
    var knownCards: List[Card] = List.empty

    override def seeCard(cardIndex: Int): Unit =
      knownCards = knownCards :+ cards(cardIndex)