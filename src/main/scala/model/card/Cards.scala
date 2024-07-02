package model.card

import model.card.CardsData.{PokerSuit, Suit}

/** Cards with a value and a suit implementations. */
object Cards:
  /** Represents a card with a value and a suit. */
  trait Card:
    type Value

    /** The value of the card. */
    def value: Value

    /** The suit of the card. */
    def suit: Suit

  /**
   * Represents generic a card with a value and a suit. It is an extension of the [[Card]] trait.
   *
   * @param value the value of the card
   * @param suit the suit of the card
   * @tparam A the type of the value of the card
   */
  case class GenericCard[A](value: A, suit: Suit) extends Card:
    override type Value = A

  /** Companion object of [[Card]]. */
  object Card:
    /**
     * Creates a generic card with a value and a suit.
     *
     * @param value the value of the card
     * @param suit  the suit of the card
     * @tparam A the type of the value of the card
     * @return a generic card with a value and a suit
     */
    def apply[A](value: A, suit: Suit): Card = GenericCard(value, suit)

  /**
   * Represents a poker card with a value and a suit. It is an extension of the [[Card]] trait.
   *
   * @param value the value of the card that ranges from 1 to 13
   * @param suit the suit of the card
   */
  case class PokerCard(value: Int, suit: PokerSuit) extends Card:
    override type Value = Int
    require(value >= 1, "Card value cannot be less than 1")
    require(value <= 13, "Card value cannot be greater than 13")

  /** Represents a card that can be covered and uncovered. */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  trait Coverable:
    private var _covered = true

    /**
     * Checks if the card is covered.
     *
     * @return true if the card is covered, false otherwise
     */
    def isCovered: Boolean = _covered

    /** Covers the card. */
    def cover(): Unit = _covered = true

    /** Uncovers the card. */
    def uncover(): Unit = _covered = false
