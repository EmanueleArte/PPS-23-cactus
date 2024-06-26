package player

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import player.Players.HumanPlayer

class PlayerTest extends AnyFlatSpec {
  val player: HumanPlayer = HumanPlayer()

  "Player" should "have a non-null value" in :
    player shouldBe a[HumanPlayer]
}
