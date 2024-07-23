package model.logic

import model.bot.Bots.BotParamsType
import model.card.Cards.PokerCard
import model.deck.Drawable
import model.game.{CactusGame, Game, Scores}
import model.player.Players.{CactusPlayer, Player}
import model.utils.Iterators.PeekableIterator

/** Logic of a game. */
object Logics:
  /** Type alias for a list of [[Player]]. */
  type Players = List[Player]

  /** Logic of a generic turn based game. */
  trait Logic:
    /** Type of the score of the game. */
    type Score
    type PlayerType <: Player

    protected val _players: Players = List[PlayerType]()

    /**
     * Iterator of the players in the game.
     *
     * @return an iterator of the players in the game.
     */
    private val playerIterator: PeekableIterator[Player] = PeekableIterator(Iterator.continually(_players).flatten)

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
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    def currentPlayer: PlayerType = playerIterator.peek.get.asInstanceOf[PlayerType]

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
    lazy val game: Game

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
     * Setup the game with bots.
     *
     * @param botsParams parameters to setup the bots.
     * @return the list of players obtained after the setup.
     */
    def setupWithBots(botsParams: BotParamsType): Players = game.setupGameWithBots(botsParams)

  /**
   * Logic of the Cactus game.
   *
   * @param playersInput can be either number of players or the collection of players in the game.
   */
  class CactusLogic(playersInput: Either[Int, BotParamsType]) extends Logic with GameLogic with GameWithTurnPhases:
    override type Score      = Int
    override type PlayerType = CactusPlayer

    override lazy val game: CactusGame = CactusGame()
    override val _players: Players = playersInput match
      case Left(nPlayers) => setup(nPlayers)
      case Right(players) => setupWithBots(players)
    private var turnsRemaining: Int = playersInput match
      case Left(nPlayers) => nPlayers
      case _              => players.length
    private var lastRound: Boolean = false
    _currentPhase = CactusTurnPhase.Draw

    override def continue(): Unit =
      val move = 1

    override def isGameOver: Boolean = if lastRound then
      turnsRemaining -= 1
      turnsRemaining <= 0
    else false

    override def calculateScore: Scores = game.calculateScores(players)

    /**
     * Make the current player to draw a card from the deck or the discard pile.
     *
     * @param fromDeck if `true` the card is drawn from the deck, if `false` it is drawn from the discard pile.
     */
    def draw(fromDeck: Boolean): Unit = currentPhase match
      case CactusTurnPhase.Draw =>
        if fromDeck then currentPlayer.draw(game.deck)
        else currentPlayer.draw(game.discardPile)
        currentPhase_=(CactusTurnPhase.Discard)
      case _ => ()

    /**
     * Make the current player to discard a card.
     *
     * @param cardIndex index of the card in the player hand to discard.
     */
    def discard(cardIndex: Int): Unit = currentPhase match
      case CactusTurnPhase.Discard =>
        game.discardPile = game.discardPile.put(currentPlayer.discard(cardIndex))
        currentPhase_=(CactusTurnPhase.DiscardEquals)
      case _ => ()

    /**
     * Make the current player to discard a card but with a malus if the card does not match the discard criteria.
     *
     * @param cardIndex index of the card in the player hand to discard.
     */
    def discardWithMalus(cardIndex: Int): Unit = currentPhase match
      case CactusTurnPhase.DiscardEquals =>
        game.discardPile.draw() match
          case Some(card) if card.value != currentPlayer.cards(cardIndex).value =>
            currentPlayer.draw(game.deck)
            game.discardPile = game.discardPile.put(card)
          case Some(card) =>
            game.discardPile = game.discardPile.put(card)
            currentPhase_=(CactusTurnPhase.Discard)
            discard(cardIndex)
          case _ => currentPlayer.draw(game.deck)
        currentPhase_=(BaseTurnPhase.End)
      case _ => ()

    /** Make the current player to call Cactus. */
    def callCactus(): Unit = lastRound = true

  /** Companion object for [[CactusLogic]]. */
  object CactusLogic:
    /**
     * Factory method for [[CactusLogic]].
     *
     * @param nPlayers number of players in the game.
     * @return a new instance of [[CactusLogic]].
     */
    def apply(nPlayers: Int): CactusLogic = new CactusLogic(Left(nPlayers): Either[Int, BotParamsType])

    /**
     * Factory method for [[CactusLogic]].
     *
     * @param botsParams parameters of the bots in the game.
     * @return a new instance of [[CactusLogic]].
     */
    def apply(botsParams: BotParamsType): CactusLogic = new CactusLogic(Right(botsParams): Either[Int, BotParamsType])
