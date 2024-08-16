package model.bot

import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.card.Cards.{Coverable, PokerCard}
import model.card.CardsData.{PokerCardName, PokerSuit}
import model.card.CardsData.PokerSuit.Clubs
import model.deck.Piles.PokerPile
import model.player.Players.CactusPlayer
import model.utils.ModelUtils.isRedKing

/** The bots of the game */
object Bots:
  /** Type alias for the parameters to setup the bots. */
  type BotParamsType = Tuple

  /** Represents a bot of the Cactus game. */
  trait CactusBot:
    /**
     * Gets the known cards.
     *
     * @return the cards known by the bot.
     */
    def knownCards: List[PokerCard]

    /**
     * Lets the [[CactusBot]] see a [[Card]].
     *
     * @param cardIndex the index of the [[Card]] in the list of the cards.
     */
    def seeCard(cardIndex: Int): Unit

    /**
     * Chooses the [[Card]] to discard.
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

    /** Applies the jack card special effect. */
    def applyJackEffect(): Unit

    /**
     * Chooses a [[PokerCard]] to discard if its value is equal to the one in top of the [[PokerPile]].
     * @param discardPile the discard pile of the game.
     * @return an [[Option]] with the index of the card to discard, if it exists.
     */
    def chooseDiscardWithMalus(discardPile: PokerPile): Option[Int]

    /**
     * Chooses if it has to call cactus.
     * @return true if it calls cactus
     */
    def shouldCallCactus(): Boolean

    /**
     * Chooses the [[CactusPlayer]] with the fewest cards.
     * @param players the [[List]] of the players.
     * @return an [[Option]] containing the chosen [[CactusPlayer]].
     */
    def choosePlayer(players: List[CactusPlayer]): Option[CactusPlayer]

  /**
   * Implementation of a [[CactusBot]]
   * @param name the name of the bot.
   * @param c the cards of the bot.
   * @param _drawMethod the [[DrawMethods]] of the bot.
   * @param _discardMethod the [[DiscardMethods]] of the bot.
   * @param _memory the [[Memory]] of the bot.
   */
  class CactusBotImpl(
      name: String,
      c: List[PokerCard & Coverable],
      private val _drawMethod: DrawMethods,
      private val _discardMethod: DiscardMethods,
      private val _memory: Memory
  ) extends CactusPlayer(name, c)
      with CactusBot:
    private var _knownCards: List[PokerCard]   = List.empty
    private val _cardsListLengthForCactus: Int = 2
    private val _differenceForCactus: Int      = 1
    private val _maxPointsForCactus: Int       = 10

    override def knownCards: List[PokerCard] = _knownCards

    override def seeCard(cardIndex: Int): Unit = cards match
      case c if c.isEmpty => throw new UnsupportedOperationException()
      case _ =>
        scala.util.Random.nextDouble() match
          case r if r >= _memory.lossPercentage => _knownCards = _knownCards ++ List(cards(cardIndex))
          case _                                => ()

    private def removeFromKnownCards(card: PokerCard): Unit =
      _knownCards = _knownCards.filterNot(c => c.equals(card))

    override def discard(cardIndex: Int): PokerCard & Coverable =
      val discardedCard = super.discard(cardIndex)
      removeFromKnownCards(discardedCard)
      discardedCard

    private def isHigherValue(c1: PokerCard, c2: PokerCard): Boolean = c1 match
      case c1 if isRedKing(c1)                        => false
      case c1 if isRedKing(c2) || c1.value > c2.value => true
      case _                                          => false

    private def higherKnownCardIndex: Int =
      var higherValueCard: PokerCard = PokerCard(PokerCardName.Ace, Clubs)
      _knownCards.zipWithIndex.foreach((c, i) =>
        if isHigherValue(c, higherValueCard) || i == 0 then higherValueCard = _knownCards(i)
      )
      cards.zipWithIndex.filter((c, _) => c.equals(higherValueCard)).map((_, i) => i).headOption.getOrElse(unknownCard)

    private def unknownCard: Int =
      cards.zipWithIndex.filter((c, _) => cards.diff(_knownCards).contains(c)).map((_, i) => i) match
        case Nil     => higherKnownCardIndex
        case indexes => indexes(scala.util.Random.nextInt(indexes.length))

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

    override def applyJackEffect(): Unit =
      seeCard(unknownCard)

    override def chooseDiscardWithMalus(discardPile: PokerPile): Option[Int] =
      if knownCards.nonEmpty then
        knownCards.zipWithIndex.find((c, _) => c.value == discardPile.copy(discardPile.cards).draw().get.value) match
          case Some((card, i)) =>
            Some(cards.zipWithIndex.filter((c, _) => c.equals(card)).map((_, i) => i).headOption.get)
          case _ => None
      else None

    private def totKnownValue: Int =
      _knownCards.map {
        case c if isRedKing(c) => 0
        case c                 => c.value
      }.sum

    override def shouldCallCactus(): Boolean =
      cards.lengthIs <= _cardsListLengthForCactus || ((cards.length - _knownCards.length) <= _differenceForCactus && totKnownValue < _maxPointsForCactus)

    override def choosePlayer(players: List[CactusPlayer]): Option[CactusPlayer] =
      players
        .filter(p => p != this)
        .filter(p => !p.calledCactus)
        .minOption((p1, p2) => p1.cards.length - p2.cards.length)
