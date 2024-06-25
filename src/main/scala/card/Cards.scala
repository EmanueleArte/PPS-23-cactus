package card

import card.CardsData.*

/**
 * Cards with a value and a suit implementation.
 */
object Cards:
  /**
   * Represents a card with a value and a suit.
   */
  trait Card:
    type Value
    /**
     * The value of the card.
     */
    def value: Value
    /**
     * The suit of the card.
     */
    def suit: Suit

  /**
   * Represents generic a card with a value and a suit. It is an extension of the [[Card]] trait.
   *
   * @tparam A the type of the value of the card
   * @param value the value of the card
   * @param suit the suit of the card
   */
  case class GenericCard[A](value: A, suit: Suit) extends Card:
    override type Value = A

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

  /**
   * Represents a card that can be covered and uncovered.
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  trait Coverable:
    private var covered = true
    /**
     * Checks if the card is covered.
     *
     * @return true if the card is covered, false otherwise
     */
    def isCovered: Boolean = covered
    /**
     * Covers the card.
     */
    def cover(): Unit = covered = true
    /**
     * Uncovers the card.
     */
    def uncover(): Unit = covered = false
