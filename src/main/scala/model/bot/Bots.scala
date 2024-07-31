package model.bot

import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.card.Cards.{Coverable, PokerCard}
import model.card.CardsData.{PokerCardName, PokerSuit}
import model.card.CardsData.PokerSuit.Clubs
import model.deck.Piles.{DiscardPile, PokerPile}
import model.player.Players.CactusPlayer

/** The bots of the game */
object Bots:
  /** Type alias for the parameters to setup the bots. */
  type BotParamsType = Tuple

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  /** Represents a bot. */
  trait CactusBot:
    /**
     * Gets the known cards.
     *
     * @return the cards known by the bot.
     */
    def knownCards: List[PokerCard]

    /**
     * Let the [[CactusBot]] see a [[Card]].
     *
     * @param cardIndex the index of the [[Card]] in the list of the cards.
     */
    def seeCard(cardIndex: Int): Unit;

    /**
     * Choose the [[Card]] to discard.
     * @return the index of the [[Card]] in the list to discard
     */
    def chooseDiscard(): Int

    /**
     * Chooses the draw deck.
     * @param discardPile the [[DiscardPile]] of the game
     * @return `true` if the bot should draw from the deck, `false` if it should draw from the discard pile
     */
    def chooseDraw(discardPile: PokerPile): Boolean

    /**
     * Discards a card from the player's hand.
     * @param cardIndex the index of the card in the list to discard
     * @return the discarded card
     */
    def discard(cardIndex: Int): PokerCard

    /*-checkEffect()*/

    /**
     * Applies the jack card special effect.
     */
    def applyJackCardEffect(): Unit

    def chooseDiscardWithMalus(discardPile: PokerPile): Option[Int]

    /**
     * Chooses if it has to call cactus.
     * @return true if it calls cactus
     */
    def callCactus(): Boolean

    def chooseOwnCard(cardIndex: Int): PokerCard

    // def chooseTwoCards((Player, card: int), (Player, card: int))

    def choosePlayer(players: List[CactusPlayer]): CactusPlayer

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class CactusBotImpl(
      name: String,
      c: List[PokerCard & Coverable],
      private val _drawMethod: DrawMethods,
      private val _discardMethod: DiscardMethods,
      private val _memory: Memory
  ) extends CactusPlayer(name, c)
      with CactusBot:
    private var _knownCards: List[PokerCard]  = List.empty
    private val cardsListLengthForCactus: Int = 2
    private val differenceForCactus: Int      = 1
    private val maxPointsForCactus: Int       = 10

    override def knownCards: List[PokerCard] = _knownCards

    override def seeCard(cardIndex: Int): Unit = cards match
      case c if c.isEmpty => throw new UnsupportedOperationException()
      case _ =>
        scala.util.Random.nextDouble() match
          case r if r >= _memory.lossPercentage => _knownCards = _knownCards ++ List(cards(cardIndex))
          case _                                =>

    private def removeFromKnownCards(card: PokerCard): Unit =
      _knownCards = _knownCards.filterNot(c => c.equals(card))

    override def discard(cardIndex: Int): PokerCard & Coverable =
      val discardedCard = super.discard(cardIndex)
      removeFromKnownCards(discardedCard)
      discardedCard

    private def isRedKing(c: PokerCard): Boolean = c.value match
      case PokerCardName.King => c.suit == PokerSuit.Hearts || c.suit == PokerSuit.Diamonds
      case _                  => false

    private def isHigherValue(c1: PokerCard, c2: PokerCard): Boolean = c1 match
      case c1 if isRedKing(c1)                        => false
      case c1 if isRedKing(c2) || c1.value > c2.value => true
      case _                                          => false

    private def higherKnownCardIndex: Int =
      var higherValueCard: PokerCard = PokerCard(PokerCardName.Ace, Clubs)
      _knownCards.zipWithIndex.foreach((c, i) =>
        if (isHigherValue(c, higherValueCard) || i == 0) then higherValueCard = _knownCards(i)
      )
      cards.zipWithIndex.filter((c, _) => c.equals(higherValueCard)).map((_, i) => i).head

    private def unknownCard: Int =
      val indexes: List[Int] = cards.zipWithIndex.filter((c, _) => cards.diff(_knownCards).contains(c)).map((_, i) => i)
      indexes match
        case indexes if indexes.isEmpty => scala.util.Random.nextInt(cards.length)
        case _ => indexes(scala.util.Random.nextInt(indexes.length))

    override def chooseDiscard(): Int = _discardMethod match
      case DiscardMethods.Unknown => unknownCard
      case DiscardMethods.Known   => higherKnownCardIndex
      case DiscardMethods.Random  => scala.util.Random.nextInt(cards.length)

    private def isDiscardPileBetter(discardPile: PokerPile): Boolean = discardPile match
      case discardPile if discardPile.size == 0 => false
      case _ => isHigherValue(cards(higherKnownCardIndex), discardPile.copy(discardPile.cards).draw().get)

    override def chooseDraw(discardPile: PokerPile): Boolean = _drawMethod match
      case DrawMethods.Deck        => true
      case DrawMethods.Pile        => false
      case DrawMethods.RandomDeck  => scala.util.Random.nextBoolean()
      case DrawMethods.PileSmartly => isDiscardPileBetter(discardPile)

    override def applyJackCardEffect(): Unit =
      seeCard(unknownCard)

    override def chooseDiscardWithMalus(discardPile: PokerPile): Option[Int] =
      knownCards.zipWithIndex.find((c, _) => c.value == discardPile.copy(discardPile.cards).draw().get.value) match
        case Some((card, i)) => Some(cards.zipWithIndex.filter((c, _) => c.equals(card)).map((_, i) => i).head)
        case _            => None

    private def totKnownValue: Int =
      _knownCards.map {
        case c if isRedKing(c) => 0
        case c                 => c.value
      }.sum

    override def callCactus(): Boolean =
      cards.length <= cardsListLengthForCactus || ((cards.length - _knownCards.length) <= differenceForCactus && totKnownValue < maxPointsForCactus)

    override def chooseOwnCard(cardIndex: Int): PokerCard = ???

    override def choosePlayer(players: List[CactusPlayer]): CactusPlayer = ???
