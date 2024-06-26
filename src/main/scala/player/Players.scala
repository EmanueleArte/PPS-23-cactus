package player

object Players:
  trait Player;

  case class HumanPlayer() extends Player;

  object Player;
