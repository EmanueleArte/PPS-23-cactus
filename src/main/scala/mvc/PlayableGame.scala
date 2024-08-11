package mvc

/** Represents the playable of games. */
enum PlayableGame:
  case Cactus
  
  def name: String = this match
    case Cactus => "Cactus"

  /**
   * Gets the respective game MVC.
   *
   * @return the game MVC.
   */
  def gameMVC: GameMVC = this match
    case Cactus => CactusMVC()
