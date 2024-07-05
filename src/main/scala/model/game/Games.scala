package model.game

import card.Cards.{Card, PokerCard}
import model.deck.Decks.{Deck, PokerDeck}
import model.deck.Piles.{DiscardPile, PokerPile}
import player.Players.{CactusPlayer, Player}

/** Module with CactusGame implementation. */
object Games:

  /** Generic card game. */
  trait Game:
    /**
     * Setup method to call before start the game.
     * @param playersNumber number of players in the match.
     * @return a list with the initialized players.
     */
    def setupGame(playersNumber: Int): List[Player]
    def calculateScores(players: List[Player]): Map[Player, Int]

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
        .map(list => CactusPlayer(list))
    override def calculateScores(players: List[Player]): Map[Player, Int] =
      players
        .zipWithIndex
        .map((player, index) => (player, player.cards))
        .filter((player, cards) => cards.count {
          case card: PokerCard => true
          case _ => false
        } == cards.size)
        .map((player, cards) => (player, cards.collect { case card: PokerCard => card.value }.sum))
        .map((player, score) => player -> score)
        .toMap

