package model.bot

import Bots.DiscardMethods.Random
import Bots.DrawMethods.{Deck, PileSmartly}
import model.card.Cards.PokerCard
import model.player.Players.CactusPlayer

/** The bots of the game */
object Bots:

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /** Represents a bot. */
  trait CactusBot:
    /*/** The cards that the bot knows. */
    var knownCards: List[PokerCard]
    val _drawMethod: DrawMethods
    val _discardMethod: DiscardMethods
    val memoryLossPercentage: MemoryLossPercentage*/

    /** Let the [[CactusBot]] see a [[Card]].
     *
     * @param cardIndex the index of the [[Card]] in the list of the cards.
     */
    def seeCard(cardIndex: Int): Unit;

    /** Choose the [[Card]] to discard.
     * @return the index of the [[Card]] in the list to discard
     */
    def chooseDiscard(): Int

    /** Chooses the draw deck.
     * @return true if the bot should draw from the deck, false if it should draw from the discard pile
     */
    def chooseDraw(): Boolean

    /** Discards a card from the player's hand.
     * @param cardIndex the index of the card in the list to discard
     * @return the discarded card
     */
    def discard(cardIndex: Int): PokerCard

    /*-checkEffect()*/
    def discardWithMalus(cardIndex: Int): PokerCard

    /** Chooses if it has to call cactus.
     * @return true if it calls cactus
     */
    def callCactus(): Boolean

    def chooseOwnCard(cardIndex: Int): PokerCard

    //def chooseTwoCards((Player, card: int), (Player, card: int))

    def choosePlayer(player: CactusPlayer): CactusPlayer

  /*class Memory(lp: Double):
    private val lossPercentage: Double = lp
    require(lossPercentage <= 1)
    require(lossPercentage >= 0)

    def getLossPercentage: Double =
      lossPercentage*/

  enum DrawMethods:
    case Deck, Pile, RandomDeck, PileSmartly

  enum DiscardMethods:
    case Unknown, Known, Random

  enum Memory(val lossPercentage: Double):
    require(lossPercentage <= 1)
    require(lossPercentage >= 0)

    case Bad extends Memory(0.8)
    case Normal extends Memory(0.5)
    case Good extends Memory(0.25)
    case VeryGood extends Memory(0.1)
    case Optimal extends Memory(0)

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class CactusBotImpl(name: String, c: List[PokerCard], private val _drawMethod: DrawMethods, private val _discardMethod: DiscardMethods, private val _memory: Memory)
  extends CactusPlayer(name, c) with CactusBot:
    private var _knownCards: List[PokerCard] = List.empty
    //private val _drawMethod: DrawMethods = drMth
    //private val _discardMethod: DiscardMethods = discMth
    //private val _memory: Memory = m

    /** Gets the known cards.
     * @return the cards known by the bot.
     */
    def knownCards: List[PokerCard] = _knownCards

    override def seeCard(cardIndex: Int): Unit =
      if (cards.isEmpty) {
        throw new UnsupportedOperationException()
      }
      val random = scala.util.Random.nextDouble()
      if (random >= _memory.lossPercentage) {
        _knownCards = _knownCards :+ cards(cardIndex)
      }

    private def removeFromKnownCards(card: PokerCard): Unit =
      _knownCards = _knownCards.filterNot(c => c == card)

    override def discard(cardIndex: Int): PokerCard =
      val discardedCard = super.discard(cardIndex)
      removeFromKnownCards(discardedCard)
      discardedCard

    private def higherKnownCard: Int =
      var higherValue: Int = -1
      var higherValueIndex: Int = 0
      for (i <- _knownCards.indices) {
        if (_knownCards(i).value > higherValue) {
          higherValue = _knownCards(i).value
          higherValueIndex = i
        }
      }
      higherValueIndex

    private def unknownCard: Int =
      val diff: List[PokerCard] = cards.diff(_knownCards)
      var higherValue: Int = -1
      for (i <- diff.indices) {
        if (diff(i).value > higherValue) {
          higherValue = diff(i).value
        }
      }
      val indexes: List[Int] = cards.zipWithIndex.filter((c, _) => c.## == higherValue).map((_, i) => i)
      if (indexes.isEmpty) {
        0
      } else {
        indexes.head
      }

    override def chooseDiscard(): Int = _discardMethod match
      case DiscardMethods.Unknown => higherKnownCard
      case DiscardMethods.Known => unknownCard
      case DiscardMethods.Random => scala.util.Random.nextInt(cards.length)

    override def chooseDraw(): Boolean = _drawMethod match
      case DrawMethods.Deck => true
      case DrawMethods.Pile => false
      case DrawMethods.RandomDeck => scala.util.Random.nextBoolean()
      case DrawMethods.PileSmartly => ???   //TODO dovrebbe ottenere il valore della carta in cima alla pila degli scarti

    override def discardWithMalus(cardIndex: Int): PokerCard = ???

    private def totKnownValue: Int =
      _knownCards.map(c => c.value).sum

    override def callCactus(): Boolean =
      cards.length <= 2 || ((cards.length - _knownCards.length) <= 2 && totKnownValue < 10)

    override def chooseOwnCard(cardIndex: Int): PokerCard = ???

    override def choosePlayer(player: CactusPlayer): CactusPlayer = ???

  /** Companion object of [[CactusBotImpl]]. */
  /*object CactusBotImpl:
    def apply(cards: List[Card]): CactusBotImpl = CactusBotImpl(cards)*/
