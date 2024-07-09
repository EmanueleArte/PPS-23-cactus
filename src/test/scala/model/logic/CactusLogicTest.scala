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

  "A player" should "draw from the deck" in:
    val logic = CactusLogic(playersNumber)
    logic.draw(true)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber + 1)
    logic.game.deck.size should be (52 - playersNumber * logic.game.initialPlayerCardsNumber - 1)

