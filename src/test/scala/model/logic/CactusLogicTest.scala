package model.logic

import model.card.Cards.PokerCard
import model.deck.Decks.{Deck, PokerDeck}
import model.game.CactusGame
import model.game.Scores.toMap
import model.logic.Logics.{CactusLogic, GameLogic, Players}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

/** Test for [[CactusLogic]]. */
class CactusLogicTest extends AnyFlatSpec:

  val playersNumber: Int = 4
  val deckSize: Int = 52

  /** Custom implementation of the CactusGame to make tests with an unshuffled deck. */
  class TestCactusLogic(nPlayers: Int) extends CactusLogic(nPlayers) with GameLogic:
    override lazy val game: CactusGame = new CactusGame():
      override val deck: Deck[PokerCard] = PokerDeck()

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
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be (1)
    logic.draw(false)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber)
    logic.game.discardPile.size should be (0)

  it should "make a basic complete turn" in:
    val logic = CactusLogic(playersNumber)
    logic.draw(true)
    logic.discard(0)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber)
    logic.game.discardPile.size should be (1)
    logic.game.deckSize should be (deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "have a malus if he discards a card not in the classic discard phase and the discard pile is empty" in:
    val logic = CactusLogic(playersNumber)
    logic.discardWithMalus(0)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber + 1)
    logic.game.discardPile.size should be (0)
    logic.game.deckSize should be (deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "have a malus if he discards an incorrect card not in the classic discard phase" in:
    val logic = TestCactusLogic(playersNumber)
    logic.discard(1)
    logic.nextPlayer
    logic.discardWithMalus(1)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber + 1)
    logic.game.discardPile.size should be (1)
    logic.game.deckSize should be (deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "discard a card if it matches the criteria of the non-classic discard" in:
    val logic = TestCactusLogic(playersNumber)
    logic.discard(1)
    for _ <- 1 to 3 do logic.nextPlayer
    logic.discardWithMalus(2)
    logic.currentPlayer.cards.size should be (logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be (2)
    logic.game.deckSize should be (deckSize - playersNumber * logic.game.initialPlayerCardsNumber)

  "Players" should "make a complete match using basic moves" in:
    val logic = CactusLogic(playersNumber)
    while !logic.isGameOver do
      logic.draw(true)
      logic.discard(0)
      logic.callCactus()
      logic.nextPlayer
    logic.game.deckSize should be (deckSize - playersNumber * logic.game.initialPlayerCardsNumber - playersNumber)
    logic.game.discardPile.size should be (playersNumber)

  "At the end of a Cactus match" should "be possible to calculate the scores" in:
    val logic = CactusLogic(playersNumber)
    while !logic.isGameOver do
      logic.draw(true)
      logic.discard(0)
      logic.callCactus()
      logic.nextPlayer
    for (_, score) <- toMap(logic.calculateScore) do score should be > 0
