package model.logic

import model.logic.Logics.CactusLogic
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

/** Test for [[CactusLogic]]. */
class CactusLogicTest extends AnyFlatSpec:

  val playersNumber: Int = 4

  "A Cactus Logic" should "have the correct number of players after the setup" in:
    val logic = CactusLogic(playersNumber)
    logic.players.size should be (playersNumber)

  "Players" should "have 5 cards after draw from the deck" in:
    val logic = CactusLogic(playersNumber)
    for _ <- 1 to playersNumber do
      logic.playTurn()
      logic.playerIterator.next()
    logic.players.foreach(player => player.cards.size should be (5))
