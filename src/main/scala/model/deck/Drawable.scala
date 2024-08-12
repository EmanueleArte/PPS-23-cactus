package model.deck

import model.card.Cards.Card

/**
 * Represents an object from which is possible to draw something.
 *
 * @tparam C type of the drawn item. C needs to be at least a [[Card]].
 */
trait Drawable[C <: Card]:
  /**
   * Picks the first item of the collection.
   * @return an `Option` with the drawn item.
   */
  def draw(): Option[C]
