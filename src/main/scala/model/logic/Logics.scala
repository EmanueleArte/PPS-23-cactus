package model.logic

import model.game.{CactusGame, Game, Scores}
import model.utils.Iterators.PeekableIterator
import player.Players.Player

import scala.annotation.tailrec

/**
 * Opaque type representing the players.
 * Internally it is a list of [[Player]]].
 */
opaque type Players = List[Player]

/** Logic of a game. */
object Logics:
  /** Type alias for a list of [[Player]]. */
  type Players = List[Player]

  /** Logic of a generic turn based game. */
  trait Logic:
    /** Type of the score of the game. */
    type Score

    protected val _players: Players = List()

    /**
     * Iterator of the players in the game.
     *
     * @return an iterator of the players in the game.
     */
    protected val playerIterator: PeekableIterator[Player] = PeekableIterator(Iterator.continually(_players).flatten)

    /**
     * Getter for the list of players in the game.
     *
     * @return the list of players in the game.
     */
    def players: Players = _players

    /**
     * Getter for the current player.
     *
     * @return the current player.
     */
    @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
    def currentPlayer: Player = playerIterator.peek.get

    /**
     * Switch to the next player.
     *
     * @return the next player.
     */
    def nextPlayer: Player = playerIterator.next()

    /** Continue to the next step. */
    def continue(): Unit

    /**
     * Check if the game is over.
     *
     * @return `true` if the game is over, `false` otherwise.
     */
    def isGameOver: Boolean

    /**
     * Calculate the score of the game.
     *
     * @return a map with the [[Player]] and the [[Score]].
     */
    def calculateScore: Scores

  /** Provider of a [[Game]]. */
  trait GameProvider:
    /** Instance of the game to play. */
    val game: Game

  /** Trait that represents a game logic based on a certain game. */
  trait GameLogic extends GameProvider:
    /**
     * Setup the game.
     *
     * @param nPlayers number of players in the game.
     * @return the list of players obtained after the setup.
     */
    def setup(nPlayers: Int): Players = game.setupGame(nPlayers)

  /**
   * Logic of the Cactus game.
   *
   * @param nPlayers number of players in the game.
   */
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  class CactusLogic(nPlayers: Int) extends Logic with GameLogic:
    type Score = Int

    override val game: CactusGame   = CactusGame()
    override val _players: Players  = setup(nPlayers)
    private var turnsRemaining: Int = nPlayers
    private var lastRound: Boolean  = false

    override def continue(): Unit =
      val move = 1
//      move match
//        case DrawFromDeck => playerIterator.peek.get.draw(game.deck)
//        case _            => playerIterator.peek.get.draw(game.discardPile)
//      val discarded = currentPlayer.discard(
//        waitInput(2) match
//          case Discard(i) => i
//      )
//      game.discardPile = game.discardPile.put(discarded)
//      if waitInput(3) == Cactus then lastRound = true
//
    override def isGameOver: Boolean = lastRound match
      case true =>
        turnsRemaining -= 1
        turnsRemaining < 1
      case _ => false

    override def calculateScore: Scores = Scores(players.map(p => p -> 0).toMap)

  /** Companion object for [[CactusLogic]]. */
  object CactusLogic:
    /**
     * Factory method for [[CactusLogic]].
     *
     * @param nPlayers number of players in the game.
     * @return a new instance of [[CactusLogic]].
     */
    def apply(nPlayers: Int): CactusLogic = new CactusLogic(nPlayers)
