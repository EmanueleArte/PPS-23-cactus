package bot

import card.Cards.Card
import player.Players.CactusPlayer

/** The bots of the game */
object Bots:

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /** Represents a bot. */
  trait CactusBot:
    /** The cards that the bot knows. */
    var knownCards: List[Card]

    /** Let the [[CactusBot]] see a [[Card]].
 *
     * @param cardIndex the index of the [[Card]] in the list of the cards.
     */
    def seeCard(cardIndex: Int): Unit;

    /** Removes the [[Card]] from the known cards list.
     * @param card the card to remove
     */
    def removeFromKnownCards(card: Card): Unit

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class CactusBotImpl(cards: List[Card]) extends CactusPlayer(cards) with CactusBot:
    var knownCards: List[Card] = List.empty

    override def seeCard(cardIndex: Int): Unit =
      knownCards = knownCards :+ cards(cardIndex)

    override def removeFromKnownCards(card: Card): Unit =
      knownCards = knownCards.filterNot(c => c == card)