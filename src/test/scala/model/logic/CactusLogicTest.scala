package model.logic

import model.logic.Logics.CactusLogic
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

/** Test for [[CactusLogic]]. */
class CactusLogicTest extends AnyFlatSpec:

  val playersNumber: Int = 4
  val deckSize: Int = 52

  "A Cactus Logic" should "have the correct number of players after the setup" in:
    val logic = CactusLogic(playersNumber)
    logic.players.size should be (playersNumber)

  "A player" should "draw from the deck" in:
    val logic = CactusLogic(playersNumber)
    logic.draw(true)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber + 1)
    logic.game.deckSize should be (deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "discard a card" in:
    val logic = CactusLogic(playersNumber)
    logic.discard(0)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be (1)

  it should "draw from the discard pile" in :
    val logic = CactusLogic(playersNumber)
    logic.discard(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be(1)
    logic.draw(false)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.game.discardPile.size should be(0)

  it should "make a basic complete turn" in:
    val logic = CactusLogic(playersNumber)
    logic.draw(true)
    logic.discard(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.game.discardPile.size should be(1)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  "Players" should "make a complete match using basic moves" in:
    val logic = CactusLogic(playersNumber)
    while !logic.isGameOver do
      logic.draw(true)
      logic.discard(0)
      logic.callCactus()
      logic.nextPlayer
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - playersNumber)
    logic.game.discardPile.size should be(playersNumber)

