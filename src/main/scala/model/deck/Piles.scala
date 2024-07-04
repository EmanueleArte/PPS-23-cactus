package model.deck

import model.card.Cards.{Card, PokerCard}
import model.deck.DeckUtils.Drawable

/** Stack of discarded cards with different implementations. */
object Piles:
  /** Pile of [[Card]] with basic methods. */
  trait DiscardPile extends Drawable:
    /** Type of the cards in the pile. */
    type CardType <: Card

    override def draw(): Option[CardType]

    /**
     * Size of the pile.
     * @return
     *   number of cards in the pile.
     */
    def size: Int

    /**
     * Place a card on top of the pile.
     * @param card
     *   the card to discard.
     * @return
     *   the pile with the card on top.
     */
    def put(card: Card): DiscardPile

    /**
     * The representations of the cards in the pile.
     * @return
     *   list of cards.
     */
    def cards: List[CardType]

    /**
     * Remove all the cards from the pile.
     * @return
     *   an empty pile.
     */
    def empty(): DiscardPile

  /** Basic implementation of a pile. */
  abstract class PileImpl() extends DiscardPile:
    override def size: Int = cards.size

  /**
   * The most general kind of pile creatable.
   * @param cards
   *   list of cards of the pile.
   */
  case class GenericPile(cards: List[Card]) extends PileImpl:
    override type CardType = Card
    override def put(card: Card): DiscardPile = card match
      case card: Card => GenericPile(card +: cards)
      case _          => this

    override def draw(): Option[Card] = cards.headOption

    override def empty(): DiscardPile = GenericPile()

  /**
   * Specific pile for french-suited cards.
   * @param cards
   *   list of cards.
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class PokerPile(cards: List[PokerCard]) extends PileImpl:
    override type CardType = PokerCard

    override def draw(): Option[CardType] = cards.headOption

    override def put(card: Card): DiscardPile =
      require(card.isInstanceOf[PokerCard], "Expected a PokerCard")
      card match
        case pokerCard: PokerCard => PokerPile(pokerCard +: cards)
        case _                    => this

    override def empty(): DiscardPile = PokerPile()

  /** Companion object of [[DiscardPile]]. */
  object DiscardPile:
    /**
     * Create a generic pile.
     * @return
     *   a generic pile.
     */
    def apply(): DiscardPile = GenericPile()

  /** Companion object of [[GenericPile]]. */
  object GenericPile:
    /**
     * Create an empty generic pile.
     * @return
     *   an empty generic pile.
     */
    def apply(): GenericPile = GenericPile(List())

  /** Companion object of [[PokerPile]]. */
  object PokerPile:
    /**
     * Create an empty poker pile.
     * @return
     *   an empty poker pile.
     */
    def apply(): PokerPile = PokerPile(List())
