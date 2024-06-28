package model.deck

import card.CardBuilder.PokerDSL.of
import card.Cards.{Card, PokerCard}
import card.CardsData.PokerCardName.{Ace, King}
import card.CardsData.{PokerSuit, Suit}

import scala.util.Random

/** Cards deck with different implementations.
  */
object Decks:

  /** Deck of [[Card]] with basic methods.
    */
  trait Deck:
    /** Type of the cards in the deck.
      */
    type CardType <: Card

    /** The representation of the cards in the deck.
      * @return
      *   list of cards.
      */
    def cards: List[CardType]

    /** Size of the deck.
      * @return
      *   number of cards in the deck.
      */
    def size: Int

    /** Shuffle the cards in the deck.
      * @return
      *   new deck with the cards shuffled.
      */
    def shuffle(): Deck

    /** Pick the first card of the deck.
      * @return
      *   the card on top.
      */
    def draw(): Option[CardType]
    def reset(): Deck

  /** Basic implementation of a deck.
    * @param shuffled
    *   if `true` the deck is initially shuffled, if `false` it is not.
    */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  abstract class DeckImpl(shuffled: Boolean) extends Deck:
    private lazy val _cards: List[CardType] = shuffled match
      case true => Random.shuffle(_rawCards)
      case _    => _rawCards
    protected val _rawCards: List[CardType] = List()
    private val INITIAL_HEAD_VALUE: Int = -1
    private var head: Int = INITIAL_HEAD_VALUE

    override def draw(): Option[CardType] = head match
      case n if n < size - 1 => head = head + 1; Some(cards(head))
      case _                 => Option.empty

    override def size: Int = cards.size
    override def cards: List[CardType] = _cards
    override def reset(): Deck =
      head = INITIAL_HEAD_VALUE
      sameDeck

    protected def sameDeck: Deck

  /** The most general kind of deck creatable.
    *
    * The following example will show the creation of a deck formed by 6 cards,
    * which are: 1 of Spades, 2 of Spades, 3 of Spades, 1 of Diamonds, 2 of
    * Diamonds and 3 of Diamonds (in this order).
    * {{{
    * val deck: Deck = GenericDeck(1 to 3, List(Spades, Diamonds), false)
    * }}}
    *
    * @param values
    *   range of values of the cards.
    * @param suits
    *   list of suits of the cards.
    * @param shuffled
    *   if `true` the deck is initially shuffled, if `false` it is not.
    */
  case class GenericDeck(values: Range, suits: List[Suit], shuffled: Boolean)
      extends DeckImpl(shuffled):
    override type CardType = Card

    override protected val _rawCards: List[Card] = for
      suit <- suits
      value <- values
    yield Card(value, suit)

    override def shuffle(): Deck = GenericDeck(values, suits, true)
    override protected def sameDeck: Deck = this

  /** Specific deck with french-suited cards, without the jokers.
    *
    * @param shuffled
    *   if `true` the deck is initially shuffled, if `false` it is not.
    */
  case class PokerDeck(shuffled: Boolean) extends DeckImpl(shuffled):
    override type CardType = PokerCard

    override val _rawCards: List[CardType] = for
      suit <- PokerSuit.values.toList;
      value <- Ace to King
    yield value of suit

    override def shuffle(): Deck = PokerDeck(true)

    override protected def sameDeck: Deck = new PokerDeck(false):
      override def cards: List[CardType] = PokerDeck.this.cards

  /** Companion object of [[GenericDeck]].
    */
  object GenericDeck:
    /** Create an unshuffled generic deck.
      *
      * @param values
      *   range of values of the cards.
      * @param suits
      *   list of suits of the cards.
      * @return
      *   an unshuffled generic deck.
      */
    def apply(values: Range, suits: List[Suit]): GenericDeck =
      GenericDeck(values, suits, false)

  /** Companion object of [[PokerDeck]].
    */
  object PokerDeck:
    /** Creates an unshuffled poker deck.
      * @return
      *   an unshaffled poker deck.
      */
    def apply(): PokerDeck = PokerDeck(false)

  /** Companion object of [[Deck]]
    */
  object Deck:
    /** Creates a generic deck.
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
    def apply(values: Range, suits: List[Suit], shuffled: Boolean): Deck =
      GenericDeck(values, suits, shuffled)
