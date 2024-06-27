package model.deck

import card.CardBuilder.PokerDSL.*
import card.CardBuilder.PokerCardNames.*
import card.CardsData.PokerSuit.*
import card.Cards.Card
import card.Cards.PokerCard
import card.CardsData.{PokerSuit, Suit}

import scala.util.{Random, Try}

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

  /** Basic implementation of a deck.
    * @param shuffled
    *   if `true` the deck is initially shuffled, if `false` it is not.
    */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  abstract class DeckImpl(shuffled: Boolean) extends Deck:
    var head: Int = -1
    override def size: Int = cards.size
    override def draw(): Option[CardType] = head match
      case n if n < size - 1 => head = head + 1; Some(cards(head))
      case _                 => Option.empty

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

    override def shuffle(): Deck = GenericDeck(values, suits, true)

    override def cards: List[CardType] =
      for
        suit <- if shuffled then Random.shuffle(suits) else suits
        value <- if shuffled then Random.shuffle(values) else values
      yield Card(value, suit)

  /** Specific deck with french-suited cards, without the jokers.
    *
    * @param shuffled
    *   if `true` the deck is initially shuffled, if `false` it is not.
    */
  case class PokerDeck(shuffled: Boolean) extends DeckImpl(shuffled):
    override type CardType = PokerCard

    private val SUITS: List[PokerSuit] = List(Spades, Diamonds, Clubs, Hearts)
    private val VALUES: Range = Ace to King

    override def shuffle(): Deck = PokerDeck(true)

    override def cards: List[CardType] =
      for
        suit <- if shuffled then Random.shuffle(SUITS) else SUITS;
        value <- if shuffled then Random.shuffle(VALUES) else VALUES
      yield value of suit

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

  trait DiscardPile:
    type CardType <: Card
//    def cards: List[CardType]
    def size: Int
    def put(card: Card): DiscardPile
    def cards: List[CardType]
    def draw(): Option[CardType]

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class PokerPile(cards: List[PokerCard]) extends DiscardPile:
    override type CardType = PokerCard
    override def size: Int = cards.size
    override def put(card: Card): DiscardPile =
      require(card.isInstanceOf[PokerCard], "Expected a PokerCard")
      card match
        case pokerCard: PokerCard => PokerPile(pokerCard +: cards)
        case _                    => this

    override def draw(): Option[PokerCard] = cards.headOption
