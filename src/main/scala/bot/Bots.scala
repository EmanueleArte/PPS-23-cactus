package bot

import bot.Bots.DiscardMethods.Random
import bot.Bots.DrawMethods.Deck
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
    val drawMethod: DrawMethods
    val discardMethod: DiscardMethods
    val memoryLossPercentage: MemoryLossPercentage

    /** Let the [[CactusBot]] see a [[Card]].
     *
     * @param cardIndex the index of the [[Card]] in the list of the cards.
     */
    def seeCard(cardIndex: Int): Unit;

    /** Choose the [[Card]] to discard.
     * @return the index of the [[Card]] in the list to discard
     */
    def chooseDiscard(): Int

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

  class MemoryLossPercentage(lp: Double):
    val lossPercentage: Double = lp
    require(lossPercentage <= 1)
    require(lossPercentage >= 0)

  enum DrawMethods:
    case Deck, Pile, PileSmartly

  enum DiscardMethods:
    case Unknown, Known, Random

  enum Memory(percentage: MemoryLossPercentage):
    case Bad extends Memory(MemoryLossPercentage(0.8))
    case Normal extends Memory(MemoryLossPercentage(0.5))
    case Good extends Memory(MemoryLossPercentage(0.2))

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class CactusBotImpl(cards: List[Card], drMth: DrawMethods, discMth: DiscardMethods, mlp: MemoryLossPercentage) extends CactusPlayer(cards) with CactusBot:
    var knownCards: List[Card] = List.empty
    override val drawMethod: DrawMethods = drMth
    override val discardMethod: DiscardMethods = discMth
    override val memoryLossPercentage: MemoryLossPercentage = mlp

    override def seeCard(cardIndex: Int): Unit =
      val random = scala.util.Random.nextDouble()
      if (random > memoryLossPercentage.lossPercentage) {
        knownCards = knownCards :+ cards(cardIndex)
      }

    private def removeFromKnownCards(card: Card): Unit =
      knownCards = knownCards.filterNot(c => c == card)

    override def discard(cardIndex: Int): Card =
      val discardedCard = super.discard(cardIndex)
      removeFromKnownCards(discardedCard)
      discardedCard

    private def higherKnownCard: Int =
      var higherValue: Int = 0
      var higherValueIndex: Int = 0
      for (i <- 0 until knownCards.length) {
        if (knownCards(i).## > higherValue) {   //TODO ## non va bene
          higherValue = knownCards(i).##
          higherValueIndex = i
        }
      }
      higherValueIndex

    private def unknownCard: Int =
      ???

    override def chooseDiscard(): Int = discardMethod match
      case DiscardMethods.Unknown => higherKnownCard
      case DiscardMethods.Known => unknownCard
      case DiscardMethods.Random => scala.util.Random.nextInt(cards.length)

    override def chooseDraw(deck: Boolean): Drawable[_ <: Card] = ???

    override def discardWithMalus(cardIndex: Int): Card = ???

    override def callCactus(): Unit = ???

    override def chooseOwnCard(cardIndex: Int): Card = ???

    override def choosePlayer(player: CactusPlayer): CactusPlayer = ???

  /** Companion object of [[CactusBotImpl]]. */
  /*object CactusBotImpl:
    def apply(cards: List[Card]): CactusBotImpl = CactusBotImpl(cards)*/
