package model.deck

import card.CardsData.Suit
import model.deck.Decks.{Deck, GenericDeck, PokerDeck}

/**
 * Factory for the [[Decks]] objects.
 */
object DecksFactory:
  /**
   * Creates generic deck, not shuffled.
   *
   * @param values range of values of the cards.
   * @param suits  list of suits of the cards.
   * @return a [[GenericDeck]]
   */
  def deck(values: Range, suits: List[Suit]): Deck = deck(values, suits, false)

  /**
   * Creates a generic deck.
   *
   * @param values range of values of the cards.
   * @param suits  list of suits of the cards.
   * @param shuffled
   *               if `true` the deck is initially shuffled, if `false` it is not.
   * @return a [[GenericDeck]]
   */
  def deck(values: Range, suits: List[Suit], shuffled: Boolean): Deck = GenericDeck(values: Range, suits: List[Suit], shuffled)

  /**
   * Creates a poker deck, not shuffled.
   * @return a [[PokerDeck]]
   */
  def pokerDeck: Deck = pokerDeck(false)

  /**
   * Creates a poker deck.
   * @param shuffled
   *               if `true` the deck is initially shuffled, if `false` it is not.
   * @return a [[PokerDeck]]
   */
  def pokerDeck(shuffled: Boolean): Deck = PokerDeck(shuffled)