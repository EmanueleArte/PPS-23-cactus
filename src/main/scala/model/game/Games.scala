package model.game

import card.Cards.{Card, PokerCard}
import model.deck.Decks.{Deck, PokerDeck}
import model.deck.Piles.{DiscardPile, PokerPile}
import player.Players.{CactusPlayer, Player}

opaque type Scores = Map[Player, Int]

object Scores:
  def apply(map: Map[Player, Int]): Scores = map
  def toMap(score: Scores): Map[Player, Int] = score
  extension (score: Scores)
    def size: Int = score.size
    def get(player: Player): Option[Int] = score.get(player)
    def players: Iterable[Player] = score.keys
    def isEmpty: Boolean = score.isEmpty

/** Generic card game. */
trait Game:
  /**
   * Setup method to call before start the game.
   * @param playersNumber number of players in the match.
   * @return a list with the initialized players.
   */
  def setupGame(playersNumber: Int): List[Player]
  def calculateScores(players: List[Player]): Scores

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
  override def calculateScores(players: List[Player]): Scores = Scores(
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
    )

