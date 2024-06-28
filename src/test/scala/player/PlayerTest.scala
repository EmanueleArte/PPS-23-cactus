package player

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import player.Players.HumanPlayer

class PlayerTest extends AnyFlatSpec {
  val player: HumanPlayer = HumanPlayer(List.empty[Int])

  "Player" should "be a HumanPlayer" in:
    player shouldBe a[HumanPlayer]

  "Player" should "have a non-null value" in:
      player.cards shouldBe a[List[Int]]

  /*"Player" should "have a non-null value" in :
    player.cards shouldBe a[List[Card]]*/
}
