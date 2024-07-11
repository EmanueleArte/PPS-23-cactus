package model.bot

import Bots.DiscardMethods.Random
import Bots.DrawMethods.{Deck, PileSmartly}
import model.card.Cards.PokerCard
import model.card.CardsData.{PokerCardName, PokerSuit}
import model.card.CardsData.PokerSuit.Clubs
import model.player.Players.CactusPlayer

/** The bots of the game */
object Bots:

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /** Represents a bot. */
  trait CactusBot:
    /** Gets the known cards.
     *
     * @return the cards known by the bot.
     */
    def knownCards: List[PokerCard]

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

    def choosePlayer(players: List[CactusPlayer]): CactusPlayer

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
    private val cardsListLengthForCactus: Int = 2
    private val differenceForCactus: Int = 1
    private val maxPointsForCactus: Int = 10

    override def knownCards: List[PokerCard] = _knownCards

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

    private def checkIfHigherValue(c1: PokerCard, c2: PokerCard): Boolean =
      if(c1.value == PokerCardName.King && (c1.suit == PokerSuit.Hearts || c1.suit == PokerSuit.Diamonds)){
        return false
      }
      if(c1.value > c2.value){
        return true
      }
      false

    private def higherKnownCard: Int =
      var higherValueCard: PokerCard = PokerCard(PokerCardName.Ace, Clubs)
      for (i <- _knownCards.indices) {
        if (checkIfHigherValue(_knownCards(i), higherValueCard) || i == 0) {
          higherValueCard = _knownCards(i)
        }
      }
      cards.zipWithIndex.filter((c, _) => c.equals(higherValueCard)).map((_, i) => i).head

    private def unknownCard: Int =
      if (_knownCards.isEmpty) {
        scala.util.Random.nextInt(cards.length)
      }
      val diff: List[PokerCard] = cards.diff(_knownCards)
      if (diff.isEmpty) {
        higherKnownCard
      }
      val indexes: List[Int] = cards.zipWithIndex.filter((c, _) => diff.contains(c)).map((_, i) => i)
      indexes(scala.util.Random.nextInt(indexes.length))

    override def chooseDiscard(): Int = _discardMethod match
      case DiscardMethods.Unknown => unknownCard
      case DiscardMethods.Known => higherKnownCard
      case DiscardMethods.Random => scala.util.Random.nextInt(cards.length)

    override def chooseDraw(): Boolean = _drawMethod match
      case DrawMethods.Deck => true
      case DrawMethods.Pile => false
      case DrawMethods.RandomDeck => scala.util.Random.nextBoolean()
      case DrawMethods.PileSmartly => ???   //TODO dovrebbe ottenere il valore della carta in cima alla pila degli scarti

    override def discardWithMalus(cardIndex: Int): PokerCard = ???

    private def totKnownValue: Int =
      _knownCards.map(c => c.value).sum //TODO non è considerato che il re rosso vale 0

    override def callCactus(): Boolean =
      cards.length <= cardsListLengthForCactus || ((cards.length - _knownCards.length) <= differenceForCactus && totKnownValue < maxPointsForCactus)

    override def chooseOwnCard(cardIndex: Int): PokerCard = ???

    override def choosePlayer(players: List[CactusPlayer]): CactusPlayer = ???

  /** Companion object of [[CactusBotImpl]]. */
  /*object CactusBotImpl:
    def apply(cards: List[Card]): CactusBotImpl = CactusBotImpl(cards)*/
