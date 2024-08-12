package model.player

import model.card.Cards.{Card, Coverable, PokerCard}
import model.deck.Drawable

/** A player of the game. */
object Players:

  /** Represents a generic player. */
  trait Player:
    /** Type representing the type of the cards in a game. */
    type CardType <: Card & Coverable

    /** The name of the player. */
    val name: String

    /**
     * The cards in the player's hand.
     * @return the cards in the player's hand.
     */
    def cards: List[CardType]

    /**
     * Draws a card from a deck.
     * @param drawable the drawable to draw from.
     */
    def draw(drawable: Drawable[CardType]): Unit

    /**
     * Draws a card that will remain covered from a deck.
     * @param drawable the drawable to draw from.
     */
    def drawCovered(drawable: Drawable[CardType]): Unit

    /**
     * Discards a card from the player's hand.
     * @param cardIndex the index of the card in the list to discard.
     * @return the discarded card.
     */
    def discard(cardIndex: Int): CardType

    /**
     * Compares two players, returning `true` if they are equal.
     * @param anotherPlayer to compare with the player.
     * @return `true` if the players are equal, `false` otherwise.
     */
    def isEqualTo(anotherPlayer: Player): Boolean

  /** Represents a Cactus player.
   * @param name the name of the player.
   * @param _cards the cards of the player.
   */
  case class CactusPlayer(name: String, private var _cards: List[PokerCard & Coverable]) extends Player:
    override type CardType = PokerCard & Coverable

    private var _cactusCaller: Boolean = false

    override def cards: List[PokerCard & Coverable] = _cards

    private def genericDraw(drawable: Drawable[CardType], shouldStayCovered: Boolean): Unit =
      val drawnCard: Option[CardType] = drawable.draw()
      if drawnCard.isDefined then
        val card = drawnCard.get
        if shouldStayCovered then ()
        else card.uncover()
        _cards = _cards ::: card :: Nil

    override def draw(drawable: Drawable[CardType]): Unit =
      genericDraw(drawable, false)

    override def drawCovered(drawable: Drawable[CardType]): Unit =
      genericDraw(drawable, true)

    override def discard(cardIndex: Int): CardType =
      require(cardIndex >= 0)
      require(cardIndex < cards.size)
      _cards.foreach(_.cover())
      val cardToRemove: CardType = cards(cardIndex)
      _cards = _cards.zipWithIndex.filter((_, i) => i != cardIndex).map((c, _) => c)
      cardToRemove

    override def isEqualTo(anotherPlayer: Player): Boolean = this.name.compareTo(anotherPlayer.name) == 0 &&
      this.cards.diff(anotherPlayer.cards).isEmpty &&
      anotherPlayer.cards.diff(this.cards).isEmpty

    /**
     * Returns if the player has called cactus.
     * @return `true` if the player has called cactus, `false` otherwise.
     */
    def calledCactus: Boolean = _cactusCaller

    /** Calls cactus, setting the player as the only caller. */
    def callCactus(): Unit = _cactusCaller = true
