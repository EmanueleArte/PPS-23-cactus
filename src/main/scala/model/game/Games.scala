package model.game

import card.Cards.Card
import model.deck.Decks.{Deck, PokerDeck}
import model.deck.Piles.{DiscardPile, PokerPile}

/** Module with CactusGame implementation. */
object Games:
  /* Placeholder for the real Player implementation */
  trait Player:
    def cards: List[Card] = List()

  /** Generic card game. */
  trait Game:
    /**
     * Setup method to call before start the game.
     * @param playersNumber number of players in the match.
     * @return a list with the initialized players.
     */
    def setupGame(playersNumber: Int): List[Player]

  /** Cactus game implementation. */
  case class CactusGame() extends Game:
    /** Deck with the cards to draw. */
    val deck: Deck = PokerDeck()

    /** Pile with the discarded cards. */
    val discardPile: DiscardPile = PokerPile()

    export deck.{size => deckSize}
    export discardPile.{draw => drawFromDiscardPile}

    @SuppressWarnings(Array("org.wartremover.warts.All"))
    override def setupGame(playersNumber: Int): List[Player] =
      (1 to playersNumber).toList
        .map(_ => (1 to 4).toList.map(_ => deck.draw().get))
        .map(list =>
          new Player() {
            override def cards: List[Card] = list
          }
        )

  private case class PlayerImpl() extends Player

  object Player:
    def apply(): Player = PlayerImpl()
