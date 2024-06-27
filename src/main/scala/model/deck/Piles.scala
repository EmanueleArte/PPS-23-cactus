package model.deck

import card.Cards.{Card, PokerCard}

object Piles:
  trait DiscardPile:
    type CardType <: Card

    def size: Int

    def put(card: Card): DiscardPile

    def cards: List[CardType]

    def draw(): Option[CardType]

    def empty(): DiscardPile


  abstract class PileImpl() extends DiscardPile:
    override def size: Int = cards.size

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class PokerPile(cards: List[PokerCard]) extends PileImpl:
    override type CardType = PokerCard

    override def draw(): Option[CardType] = cards.headOption

    override def put(card: Card): DiscardPile =
      require(card.isInstanceOf[PokerCard], "Expected a PokerCard")
      card match
        case pokerCard: CardType => PokerPile(pokerCard +: cards)
        case _ => this

    override def empty(): DiscardPile = PokerPile(List())

  object PokerPile:
    def apply(): PokerPile = PokerPile(List())