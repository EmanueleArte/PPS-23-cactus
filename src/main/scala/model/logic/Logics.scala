package model.logic

import model.game.Games.{CactusGame, Game}
import model.utils.Iterators.PeekableIterator
import player.Players.Player

import scala.annotation.tailrec

/** Logic of a game. */
object Logics:
  type Players = List[Player]

  /** Logic of a generic turn based game. */
  trait Logic:
    type Score

    protected val _players: Players                        = List()
    protected val _currentPlayer: PeekableIterator[Player] = PeekableIterator(Iterator.continually(_players).flatten)

    /** Represents all the actions action done during the turn. */
    def playTurn(): Unit

    /**
     * Check if the game is over.
     *
     * @return `true` if the game is over, `false` otherwise.
     */
    def isGameOver: Boolean

    /**
     * Calculate the score of the game.
     *
     * @return a map with the [[Player]] and the score.
     */
    def calculateScore: Map[Player, Score]

    /** Main loop of the game. */
    def gameLoop(): Unit

  /**
   * Abstract implementation of a [[Logic]] that provides implementations of basic methods.
   *
   * @param nPlayers number of players in the game.
   */
  abstract class AbstractLogic(nPlayers: Int) extends Logic:
    type Score = Int

    @tailrec
    override final def gameLoop(): Unit =
      if !isGameOver then
        playTurn()
        _currentPlayer.next()
        gameLoop()

  /** Provider of a [[Game]]. */
  trait GameProvider:
    /** Instance of the game to play. */
    val game: Game

  /** Trait that represents a game logic based on a certain game. */
  trait GameLogic extends GameProvider:
    /** Setup the game.
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
  class CactusLogic(nPlayers: Int) extends AbstractLogic(nPlayers) with GameLogic:
    type Score = Int

    override val game: Game = CactusGame()
    override val _players: Players = setup(nPlayers)

    override def playTurn(): Unit = None

    override def isGameOver: Boolean = true

    override def calculateScore: Map[Player, Score] = _players.map(p => p -> 0).toMap
