package model.deck

import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.{Card, PokerCard}
import model.card.CardsData.PokerCardName.{Ace, King}
import model.card.CardsData.{PokerSuit, Suit}
import model.deck.Piles.{DiscardPile, PokerPile}

import scala.util.Random

/** Cards deck with different implementations. */
object Decks:

  /**
   * Deck of cards of type C with basic methods.
   * @tparam C type of the drawn item. C needs to be at least a [[Card]].
   */
  trait Deck[C <: Card] extends Drawable[C]:

    /**
     * The representation of the cards in the deck.
     * @return
     *   list of cards.
     */
    def cards: List[C]

    /**
     * Size of the deck.
     * @return
     *   number of remaining cards in the deck.
     */
    def size: Int

    /**
     * Shuffle the cards in the deck.
     * @return
     *   new deck with the cards shuffled.
     */
    def shuffle(): Deck[C]

    /**
     * Restore the deck with the cards given in the discard pile.
     * @param pile the discard pile to take the cards from.
     * @return new deck with the cards unshuffled.
     */
    def reset(pile: DiscardPile[C]): Deck[C]

    /**
     * Restore the deck with the initial cards.
     * @return new deck with the cards unshuffled.
     */
    def reset(): Deck[C]

  /**
   * Abstract class providing implementations of common methods for a [[Deck]] class.
   * @param shuffled if `true` the deck is initially shuffled, if `false` it is not.
   * @tparam C type of the drawn item. C needs to be at least a [[Card]].
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  abstract class AbstractDeck[C <: Card](shuffled: Boolean) extends Deck[C]:
    private val INITIAL_HEAD_VALUE: Int = -1
    private var head: Int               = INITIAL_HEAD_VALUE
    private lazy val _cards: List[C] = shuffled match
      case true => Random.shuffle(inputCards)
      case _    => inputCards
    protected val inputCards: List[C] = List()

    override def draw(): Option[C] =
      head match
        case n if n < _cards.size - 1 => head = head + 1; Some(cards(head))
        case _                        => Option.empty

    override def size: Int      = cards.size - head - 1
    override def cards: List[C] = _cards
    override def reset(pile: DiscardPile[C]): Deck[C] =
      head = INITIAL_HEAD_VALUE
      createDeck(pile.cards.reverse)

    override def reset(): Deck[C] =
      val discardPile: DiscardPile[C] = pile
      cards.foreach(card => discardPile.put(card))
      reset(discardPile)

    /**
     * Creates a specific deck, depending on the class implementing it.
     *
     * {{{
     *   class DeckA(shuffled: Boolean) extends AbstractDeck[CardA](shuffled):
     *    override def createDeck(cards: List[CardA]): Deck[CardA] = DeckA(shuffled = false)
     *
     *    class DeckB(shuffled: Boolean) extends AbstractDeck[CardB](shuffled):
     *    override def createDeck(cards: List[CardB]): Deck[CardB] = DeckB(shuffled = false)
     * }}}
     *
     * @param cards list with which implement the new deck.
     * @return an instance of [[Deck]] depending on the class that implements [[createDeck]] method.
     */
    protected def createDeck(cards: List[C]): Deck[C]

    /**
     * Creates a specific discard pile, depending on the class implementing it.
     * @return a [[DiscardPile]] according to the type of [[Card]] which the class implements.
     */
    protected def pile: DiscardPile[C]

  /**
   * The most general kind of deck creatable, using [[Card]] type cards.
   *
   * The following example will show the creation of a deck formed by 6 cards,
   * which are: 1 of Spades, 2 of Spades, 3 of Spades, 1 of Diamonds, 2 of
   * Diamonds and 3 of Diamonds (in this order).
   * {{{
   *  val deck: Deck = GenericDeck(1 to 3, List(Spades, Diamonds), false)
   * }}}
   *
   * @param inputCards list of [[Card]] with which implement the deck.
   * @param shuffled
   *   if `true` the deck is initially shuffled, if `false` it is not.
   */
  case class GenericDeck(override val inputCards: List[Card], shuffled: Boolean) extends AbstractDeck[Card](shuffled):

    override def shuffle(): Deck[Card]                               = GenericDeck(inputCards, true)
    override protected def createDeck(cards: List[Card]): Deck[Card] = Deck(cards)
    override protected def pile: DiscardPile[Card]                   = DiscardPile()

  /**
   * Specific deck with french-suited cards, without the jokers, using [[PokerCard]] type cards.
   *
   * @param shuffled
   *   if `true` the deck is initially shuffled, if `false` it is not.
   */
  case class PokerDeck(shuffled: Boolean) extends AbstractDeck[PokerCard](shuffled):

    override val inputCards: List[PokerCard] = for
      suit  <- PokerSuit.values.toList;
      value <- Ace to King
    yield value of suit

    override def shuffle(): Deck[PokerCard] = PokerDeck(true)

    override protected def createDeck(cards: List[PokerCard]): Deck[PokerCard] =
      new PokerDeck(false):
        override def cards: List[PokerCard] = PokerDeck.this.cards

    override protected def pile: DiscardPile[PokerCard] = PokerPile()

  /** Companion object of [[GenericDeck]]. */
  object GenericDeck:
    /**
     * Create an unshuffled generic deck.
     *
     * @param values
     *   range of values of the cards.
     * @param suits
     *   list of suits of the cards.
     * @return
     *   an unshuffled generic deck.
     */
    def apply(values: Range, suits: List[Suit]): GenericDeck =
      GenericDeck(for suit <- suits; value <- values yield Card(value, suit), false)

  /** Companion object of [[PokerDeck]]. */
  object PokerDeck:
    /**
     * Creates an unshuffled poker deck.
     * @return
     *   an unshuffled poker deck.
     */
    def apply(): PokerDeck = PokerDeck(false)

  /** Companion object of [[Deck]] */
  object Deck:

    /**
     * Creates a generic deck.
     *
     * @param values
     *   range of values of the cards.
     * @param suits
     *   list of suits of the cards.
     * @param shuffled
     *   if `true` the deck is initially shuffled, if `false` it is not.
     * @return
     *   a generic deck.
     */
    def apply(values: Range, suits: List[Suit], shuffled: Boolean): Deck[Card] = GenericDeck(values, suits)

    /**
     * Creates a generic deck.
     *
     * @param cards
     *   list of the cards to put in the deck.
     * @param shuffled
     *   if `true` the deck is initially shuffled, if `false` it is not.
     * @return
     *   a generic deck.
     */
    def apply(cards: List[Card], shuffled: Boolean): Deck[Card] = new GenericDeck(cards, shuffled)

    /**
     * Creates a generic deck.
     *
     * @param cards
     *   list of the cards to put in the deck.
     * @return
     *   a generic deck.
     */
    def apply(cards: List[Card]): Deck[Card] = Deck(cards, false)
