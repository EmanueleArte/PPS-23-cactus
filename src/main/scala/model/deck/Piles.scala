package model.deck

import model.card.Cards.{Card, Coverable, PokerCard}
import model.deck.Drawable

import scala.collection.mutable.ListBuffer

/** Stack of discarded cards with different implementations. */
object Piles:
  /**
   * Pile of [[Card]] with basic methods.
   * @tparam C type of the drawn item. C needs to be at least a [[Card]].
   */
  trait DiscardPile[C <: Card & Coverable] extends Drawable[C]:

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
  abstract class AbstractPile[C <: Card & Coverable] extends DiscardPile[C]:
    override def size: Int = cards.size

  /**
   * The most general kind of pile creatable.
   * @param inputCards
   *   list of cards of the pile.
   */
  case class GenericPile(inputCards: List[Card & Coverable]) extends AbstractPile[Card & Coverable]:
    var _cards: List[Card & Coverable] = inputCards
    override def put(card: Card & Coverable): DiscardPile[Card & Coverable] = card match
      case card: Card => GenericPile(card +: cards)
      case _          => this

    override def draw(): Option[Card & Coverable] =
      val returnedCard: Option[Card & Coverable] = _cards.headOption
      _cards = _cards.drop(1)
      returnedCard

    override def empty(): DiscardPile[Card & Coverable] = GenericPile()

    override def cards: List[Card & Coverable] = _cards

  /**
   * Specific pile for french-suited cards.
   * @param _cards
   *   list of cards.
   */
  case class PokerPile(private var _cards: List[PokerCard & Coverable]) extends AbstractPile[PokerCard & Coverable]:

    override def draw(): Option[PokerCard & Coverable] =
      val returnCard: Option[PokerCard & Coverable] = _cards.headOption
      _cards = _cards.drop(1)
      returnCard

    override def put(card: PokerCard & Coverable): PokerPile = PokerPile(card +: cards)

    override def empty(): PokerPile = PokerPile()

    override def cards: List[PokerCard & Coverable] = _cards

  /** Companion object of [[DiscardPile]]. */
  object DiscardPile:
    /**
     * Create a generic pile.
     * @return
     *   a generic pile.
     */
    def apply(): DiscardPile[Card & Coverable] = GenericPile()

  /** Companion object of [[GenericPile]]. */
  object GenericPile:
    /**
     * Create an empty generic pile.
     * @return
     *   an empty generic pile.
     */
    def apply(): GenericPile = GenericPile(List[Card & Coverable]())

  /** Companion object of [[PokerPile]]. */
  object PokerPile:
    /**
     * Create an empty poker pile.
     * @return
     *   an empty poker pile.
     */
    def apply(): PokerPile = PokerPile(List[PokerCard & Coverable]())
