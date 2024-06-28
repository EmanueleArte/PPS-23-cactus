package player

object Players:
  trait Player;

  case class HumanPlayer(l: List[Int]) extends Player:
    val cards: List[Int] = l;   //TODO change to List[Card]

  object Player;
