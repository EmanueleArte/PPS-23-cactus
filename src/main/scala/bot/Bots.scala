package bot

import card.Cards.Card
import model.deck.Drawable
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

    def chooseDraw(deck: Boolean): Drawable[_ <: Card]

    /** Discards a card from the player's hand.
     * @param cardIndex the index of the card in the list to discard
     * @return the discarded card
     */
    def discard(cardIndex: Int): Card

    /*-checkEffect()*/
    def discardWithMalus(cardIndex: Int): Card

    def callCactus(): Unit

    def chooseOwnCard(cardIndex: Int): Card

    //def chooseTwoCards((Player, card: int), (Player, card: int))

    def choosePlayer(player: CactusPlayer): CactusPlayer


  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class CactusBotImpl(cards: List[Card]) extends CactusPlayer(cards) with CactusBot:
    var knownCards: List[Card] = List.empty

    override def seeCard(cardIndex: Int): Unit =
      knownCards = knownCards :+ cards(cardIndex)

    private def removeFromKnownCards(card: Card): Unit =
      knownCards = knownCards.filterNot(c => c == card)

    override def discard(cardIndex: Int): Card =
      val discardedCard = super.discard(cardIndex)
      removeFromKnownCards(discardedCard)
      discardedCard

    override def chooseDraw(deck: Boolean): Drawable[_ <: Card] = ???

    override def discardWithMalus(cardIndex: Int): Card = ???

    override def callCactus(): Unit = ???

    override def chooseOwnCard(cardIndex: Int): Card = ???

    override def choosePlayer(player: CactusPlayer): CactusPlayer = ???
