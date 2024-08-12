package mvc

/** Represents the playable of games. */
enum PlayableGame:
  case Cactus

  /** Returns the name of the game.
   * @return the name of the game.
   */
  def name: String = this match
    case Cactus => "Cactus"

  /**
   * Gets the respective game MVC.
   *
   * @return the game MVC.
   */
  def gameMVC: GameMVC = this match
    case Cactus => CactusMVC()
