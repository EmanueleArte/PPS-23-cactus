package model.deck

import model.card.Cards.Card

/** Utilities for decks and similar. */
object DeckUtils:
  /** Represents a drawable object. */
  trait Drawable:
    /**
     * Pick the first card of the drawable.
     *
     * @return the card on top.
     */
    def draw(): Option[Card]
