package model.logic

import model.utils.Iterators.PeekableIterator
import player.Players.Player

import scala.annotation.tailrec

/** Logic of a game. */
object Logics:

  /** Logic of a generic turn based game. */
  trait Logic:
    type Score

    protected val _players: List[Player] = List()
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
   * @param players list of players in the game.
   */
  abstract class AbstractLogic(players: List[Player]) extends Logic:
    type Score = Int

    override protected val _players: List[Player] = players

    @tailrec
    override final def gameLoop(): Unit =
      if !isGameOver then
        playTurn()
        _currentPlayer.next()
        gameLoop()
