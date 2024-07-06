package model.logic

import model.game.{CactusGame, Game, Scores}
import model.utils.Iterators.PeekableIterator
import player.Players.Player

import scala.annotation.tailrec

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
    val playerIterator: PeekableIterator[Player] = PeekableIterator(Iterator.continually(_players).flatten)

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
     * @return a map with the [[Player]] and the [[Score]].
     */
    def calculateScore: Scores

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
        playerIterator.next()
        gameLoop()

  /** Provider of a [[Game]]. */
  trait GameProvider:
    /** Instance of the game to play. */
    val game: Game

  /** Trait that represents a game logic based on a certain game. */
  trait GameLogic extends GameProvider:
    protected trait Move

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
  class CactusLogic(nPlayers: Int) extends AbstractLogic(nPlayers) with GameLogic:
    type Score = Int

    override val game: CactusGame    = CactusGame()
    override val _players: Players   = setup(nPlayers)
    private val turnMovesNumber: Int = 2

    private enum InitialMove extends Move:
      case DrawFromDeck, DrawFromDiscards

    private enum DiscardMove extends Move:
      case Discard(cardIndex: Int)

    import InitialMove.*
    import DiscardMove.*

    override def playTurn(): Unit =
      val move = waitInput(1)
      move match
        case DrawFromDeck => playerIterator.peek.get.draw(game.deck)
        case _            => playerIterator.peek.get.draw(game.discardPile)
      val discarded = currentPlayer.discard(
        waitInput(2) match
          case Discard(i) => i
      )
      println(discarded)
      game.discardPile = game.discardPile.put(discarded)
      println(game.discardPile)

    override def isGameOver: Boolean = true

    override def calculateScore: Scores = Scores(players.map(p => p -> 0).toMap)

    private def waitInput(moveNumber: Int): Move = moveNumber match
      case 1 => DrawFromDeck
      case 2 => Discard(1)

  /** Companion object for [[CactusLogic]]. */
  object CactusLogic:
    /**
     * Factory method for [[CactusLogic]].
     *
     * @param nPlayers number of players in the game.
     * @return a new instance of [[CactusLogic]].
     */
    def apply(nPlayers: Int): CactusLogic = new CactusLogic(nPlayers)
