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

  "Players" should "have always the same amount of cards at the end of a normal turn" in:
    val logic = CactusLogic(playersNumber)
    for _ <- 1 to playersNumber do
      logic.playTurn()
      logic.playerIterator.next()
    logic.players.foreach(player => player.cards.size should be (4))
    logic.game.discardPile.size should be (playersNumber)

  "A Cactus match" should "end after another full round after the call of cactus" in:
    val logic = CactusLogic(playersNumber)
    logic.gameLoop()
    logic.isGameOver should be (true)
    logic.game.discardPile.size should be (playersNumber)
    logic.game.deckSize should be (52 - playersNumber * 5)


