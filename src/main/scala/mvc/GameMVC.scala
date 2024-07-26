package mvc

import model.bot.Bots.BotParamsType
import model.logic.Logics.Players

/** Represents the main module for a game. */
trait GameMVC:
  private var _botsParamsSet: Boolean    = false
  private var _botsParams: BotParamsType = (1, 2)
  private var _nPlayers: Int             = 2
  private val _minPlayers                = 2
  private val _maxPlayers                = 6

  /**
   * Getter for the number of players.
   *
   * @return the number of players.
   */
  protected def nPlayers: Int = _nPlayers

  /**
   * Getter for the players.
   *
   * @return the players.
   */
  protected def botsParams: BotParamsType = _botsParams

  /**
   * Checks if the bots parameters are set.
   *
   * @return `true` if the bots parameters are set, `false` otherwise.
   */
  protected def areBotsParamsSet: Boolean = _botsParamsSet

  /**
   * Sets the number of players for the game. If the number of players is not acceptable, it will be set
   * to the minimum or maximum number of players based on the proximity to them.
   *
   * @param nPlayers the number of players.
   */
  def setup(nPlayers: Int): Unit = _nPlayers = nPlayers match
    case _ if nPlayers < _minPlayers => _minPlayers
    case _ if nPlayers > _maxPlayers => _maxPlayers
    case _                           => nPlayers

  /**
   * Sets the bots parameters for the game.
   *
   * @param botsParams the parameters of the bots.
   */
  def setupWithBots(botsParams: BotParamsType): Unit =
    _botsParamsSet = true
    _botsParams = botsParams

  /** Runs the game. */
  def run(): Unit
