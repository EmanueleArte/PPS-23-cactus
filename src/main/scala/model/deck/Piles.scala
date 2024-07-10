package model.deck

import card.Cards.{Card, PokerCard}
import model.deck.Drawable

import scala.collection.mutable.ListBuffer

/** Stack of discarded cards with different implementations. */
object Piles:
  /**
   * Pile of [[Card]] with basic methods.
   * @tparam C type of the drawn item. C needs to be at least a [[Card]].
   */
  trait DiscardPile[C <: Card] extends Drawable[C]:

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
    def put(card: C): DiscardPile[C]

    /**
     * The representations of the cards in the pile.
     * @return
     *   list of cards.
     */
    def cards: List[C]

    /**
     * Remove all the cards from the pile.
     * @return
     *   an empty pile.
     */
    def empty(): DiscardPile[C]

  /** Abstract class providing implementations of common methods for a [[DiscardPile]] class. */
  abstract class AbstractPile[C <: Card]() extends DiscardPile[C]:
    override def size: Int = cards.size

  /**
   * The most general kind of pile creatable.
   * @param inputCards
   *   list of cards of the pile.
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class GenericPile(inputCards: List[Card]) extends AbstractPile[Card]:
    var _cards: List[Card] = inputCards
    override def put(card: Card): DiscardPile[Card] = card match
      case card: Card => GenericPile(card +: cards)
      case _          => this

    override def draw(): Option[Card] =
      val returnedCard: Option[Card] = _cards.headOption
      _cards = _cards.drop(1)
      returnedCard

    override def empty(): DiscardPile[Card] = GenericPile()

    override def cards: List[Card] = _cards

  /**
   * Specific pile for french-suited cards.
   * @param inputCards
   *   list of cards.
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class PokerPile(inputCards: List[PokerCard]) extends AbstractPile[PokerCard]:
    var _cards: List[PokerCard] = inputCards

    override def draw(): Option[PokerCard] =
      val returnCard: Option[PokerCard] = _cards.headOption
      _cards = _cards.drop(1)
      returnCard

    override def put(card: PokerCard): DiscardPile[PokerCard] = PokerPile(card +: cards)

    override def empty(): DiscardPile[PokerCard] = PokerPile()

    override def cards: List[PokerCard] = _cards

  /** Companion object of [[DiscardPile]]. */
  object DiscardPile:
    /**
     * Create a generic pile.
     * @return
     *   a generic pile.
     */
    def apply(): DiscardPile[Card] = GenericPile()

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
