package model.game

import card.Cards.Card
import model.deck.Decks.{Deck, PokerDeck}
import model.deck.Piles.{DiscardPile, PokerPile}

object Games:
  /* Placeholder for the real Player implementation */
  trait Player:
    def cards: List[Card] = List()
  private case class PlayerImpl() extends Player
  object Player:
    def apply(): Player = PlayerImpl()

  trait Game:
    def setupGame(playersNumber: Int): List[Player]

  case class CactusGame() extends Game:
    val deck: Deck = PokerDeck()
    val discardPile: DiscardPile = PokerPile()

    export deck.{size => deckSize}
    export discardPile.{draw => drawFromDiscardPile}

    @SuppressWarnings(Array("org.wartremover.warts.All"))
    override def setupGame(playersNumber: Int): List[Player] =
      (1 to playersNumber).toList
        .map(_ => (1 to 4).toList.map(_ => deck.draw().get))
        .map(list => new Player() {
          override def cards: List[Card] = list
        })
