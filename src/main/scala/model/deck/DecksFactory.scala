package model.deck

import card.CardsData.Suit
import model.deck.Decks.{Deck, GenericDeck, PokerDeck}

object DecksFactory:
  def deck(values: Range, suits: List[Suit]): Deck = deck(values, suits, false)
  def deck(values: Range, suits: List[Suit], shuffled: Boolean): Deck = GenericDeck(values: Range, suits: List[Suit], shuffled)
  def pokerDeck: Deck = pokerDeck(false)
  def pokerDeck(shuffled: Boolean): Deck = PokerDeck(shuffled)