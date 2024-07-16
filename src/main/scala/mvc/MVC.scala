package mvc

/** Represents the main module for a game. */
trait MVC:
  private var _nPlayers: Int = 2
  protected val _minPlayers  = 2
  protected val _maxPlayers  = 6

  /**
   * Getter for the number of players.
   *
   * @return the number of players
   */
  protected def nPlayers: Int = _nPlayers

  /**
   * Sets the number of players for the game. If the number of players is not acceptable, it will be set
   * to the minimum or maximum number of players based on the proximity to them.
   *
   * @param nPlayers the number of players
   */
  def setup(nPlayers: Int): Unit = _nPlayers = nPlayers
