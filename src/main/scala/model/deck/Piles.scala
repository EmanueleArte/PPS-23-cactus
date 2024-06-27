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

  object DiscardPile:
    def apply(): DiscardPile = GenericPile()

  abstract class PileImpl() extends DiscardPile:
    override def size: Int = cards.size

  case class GenericPile(cards: List[Card]) extends PileImpl:
    override type CardType = Card
    override def put(card: Card): DiscardPile = card match
      case card: Card => GenericPile(card +: cards)
      case _ => this

    override def draw(): Option[Card] = cards.headOption

    override def empty(): DiscardPile = GenericPile()
  object GenericPile:
    def apply(): GenericPile = GenericPile(List())


  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class PokerPile(cards: List[PokerCard]) extends PileImpl:
    override type CardType = PokerCard

    override def draw(): Option[CardType] = cards.headOption

    override def put(card: Card): DiscardPile =
      require(card.isInstanceOf[PokerCard], "Expected a PokerCard")
      card match
        case pokerCard: PokerCard => PokerPile(pokerCard +: cards)
        case _ => this

    override def empty(): DiscardPile = PokerPile()

  object PokerPile:
    def apply(): PokerPile = PokerPile(List())