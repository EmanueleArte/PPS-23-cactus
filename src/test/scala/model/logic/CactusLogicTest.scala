package model.logic

import model.bot.Bots.{BotParamsType, CactusBot}
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.card.CardBuilder.PokerDSL.OF
import model.card.Cards.{Coverable, PokerCard}
import model.card.CardsData
import model.card.CardsData.PokerCardName
import model.card.CardsData.PokerSuit.Spades
import model.deck.Decks.{Deck, PokerDeck}
import model.game.CactusGame
import model.game.Scores.toMap
import model.logic.Logics.{CactusLogic, GameLogic}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
/** Test for [[CactusLogic]]. */
class CactusLogicTest extends AnyFlatSpec:

  val playersNumber: Int = 4
  val deckSize: Int      = 52

  /** Custom implementation of the CactusGame to make tests with an unshuffled deck. */
  class TestCactusLogic(nPlayers: Int) extends CactusLogic(Left(nPlayers): Either[Int, BotParamsType]) with GameLogic:
    override lazy val game: CactusGame = new CactusGame():
      override val deck: Deck[PokerCard & Coverable] = PokerDeck()

  class TestCactusLogicBots(botParamsType: BotParamsType) extends CactusLogic(Right(botParamsType): Either[Int, BotParamsType]) with GameLogic:
    override lazy val game: CactusGame = new CactusGame():
      override val deck: Deck[PokerCard & Coverable] = PokerDeck()

  "A Cactus Logic" should "have the correct number of players after the setup" in:
    val logic = CactusLogic(playersNumber)
    logic.players.size should be(playersNumber)

  "A player" should "draw from the deck" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber + 1)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "discard a card" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Discard)
    logic.discard(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be(1)

  it should "draw from the discard pile" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Discard)
    logic.discard(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be(1)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(false)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.game.discardPile.size should be(0)

  it should "make a basic complete turn" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.discard(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.game.discardPile.size should be(1)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "have a malus if he discards a card not in the classic discard phase and the discard pile is empty" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.DiscardEquals)
    logic.discardWithMalus(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber + 1)
    logic.game.discardPile.size should be(0)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "have a malus if he discards an incorrect card not in the classic discard phase" in:
    val logic = TestCactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Discard)
    logic.discard(1)
    logic.nextPlayer
    logic.discardWithMalus(1)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber + 1)
    logic.game.discardPile.size should be(1)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "discard a card if it matches the criteria of the non-classic discard" in:
    val logic = TestCactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Discard)
    logic.discard(1)
    for _ <- 1 to 3 do logic.nextPlayer
    logic.discardWithMalus(2)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be(2)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber)

  "Players" should "make a complete match using basic moves" in:
    val logic = CactusLogic(playersNumber)
    while !logic.isGameOver do
      logic.draw(true)
      logic.discard(1)
      logic.currentPhase_=(CactusTurnPhase.CallCactus)
      logic.callCactus()
      logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.game.deckSize should be <= (deckSize - playersNumber * logic.game.initialPlayerCardsNumber - playersNumber)
    logic.game.discardPile.size should be(playersNumber)

  "At the end of a Cactus match" should "be possible to calculate the scores" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    while !logic.isGameOver do
      logic.draw(true)
      logic.discard(1)
      logic.continue()
      logic.callCactus()
      logic.nextPlayer
      logic.currentPhase_=(CactusTurnPhase.Draw)
    for (_, score) <- toMap(logic.calculateScore) do score should be > 0

  "A bot" should "discard a card in non-classic way if the head of the pile has the same value" in:
    val drawings: Seq[DrawMethods] = Seq.fill(playersNumber - 1)(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq.fill(playersNumber - 1)(DiscardMethods.Random)
    val memories: Seq[Memory] = Seq.fill(playersNumber - 1)(Memory.Optimal)
    val logic = TestCactusLogicBots((drawings, discardings, memories))
    logic.players.foreach {
      case bot: CactusBot =>
        (0 until logic.game.initialPlayerCardsNumber).foreach(i => bot.seeCard(i))
      case _ => ()
    }
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.discard(1)
    logic.continue()
    logic.continue()
    logic.continue()
    logic.players(3).cards.size should be (logic.game.initialPlayerCardsNumber - 1)

  it should "call cactus if it has a good hand" in:
    val maxPlayersNumber = 6
    val drawings: Seq[DrawMethods] = Seq.fill(maxPlayersNumber - 1)(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq.fill(maxPlayersNumber - 1)(DiscardMethods.Random)
    val memories: Seq[Memory] = Seq.fill(maxPlayersNumber - 1)(Memory.Optimal)
    val logic = TestCactusLogicBots((drawings, discardings, memories))
    logic.players.foreach {
      case bot: CactusBot =>
        (0 until logic.game.initialPlayerCardsNumber).foreach(i => bot.seeCard(i))
      case _ => ()
    }
    while !logic.isGameOver do
      logic.currentPlayer match
        case bot: CactusBot =>
          logic.continue()
        case _ =>
          logic.currentPhase_=(CactusTurnPhase.Draw)
          logic.draw(true)
          logic.discard(1)
          logic.currentPhase_=(CactusTurnPhase.DiscardEquals)
          logic.continue()
          logic.continue()
          logic.continue()
    logic.isGameOver should be (true)

//  it should "see a card after discarding a Jack" in :
//    val drawings: Seq[DrawMethods] = Seq.fill(playersNumber - 1)(DrawMethods.Deck)
//    val discardings: Seq[DiscardMethods] = Seq.fill(playersNumber - 1)(DiscardMethods.Random)
//    val memories: Seq[Memory] = Seq.fill(playersNumber - 1)(Memory.Optimal)
//    val logic = TestCactusLogicBots((drawings, discardings, memories))
//    logic.nextPlayer
//    val knownCardsLength = logic.nextPlayer.asInstanceOf[CactusBot].knownCards.length
//    logic.currentPhase = CactusTurnPhase.Discard
//    logic.discard(2)
//    logic.currentPlayer.asInstanceOf[CactusBot].knownCards.length shouldBe (knownCardsLength + 1)

  "A game consisting on basic moves" should "be played with bots" in:
    val drawings: Seq[DrawMethods] = Seq.fill(playersNumber - 1)(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq.fill(playersNumber - 1)(DiscardMethods.Random)
    val memories: Seq[Memory] = Seq.fill(playersNumber - 1)(Memory.Optimal)
    val logic = TestCactusLogicBots((drawings, discardings, memories))
    while !logic.isGameOver do
      logic.currentPlayer match
        case bot: CactusBot =>
          logic.continue()
        case _ =>
          logic.currentPhase_=(CactusTurnPhase.Draw)
          logic.draw(true)
          logic.discard(1)
          logic.continue()
          logic.callCactus()
          logic.continue()
    for (_, score) <- toMap(logic.calculateScore) do score should be > 0
    logic.game.deckSize should be <= 32

  it should "be played with the player and one bot" in :
    val drawings: Seq[DrawMethods] = Seq(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq(DiscardMethods.Random)
    val memories: Seq[Memory] = Seq(Memory.Optimal)
    val logic = TestCactusLogicBots((drawings, discardings, memories))
    while !logic.isGameOver do
      logic.currentPlayer match
        case bot: CactusBot =>
          logic.continue()
        case _ =>
          logic.currentPhase_=(CactusTurnPhase.Draw)
          logic.draw(true)
          logic.discard(1)
          logic.continue()
          logic.callCactus()
          logic.continue()
    for (_, score) <- toMap(logic.calculateScore) do score should be > 0
    logic.game.deckSize should be(42)

  "The effect of an ace" should "be activated when it is discarded" in:
    val logic = TestCactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.discard(0)
    logic.currentPhase should be (CactusTurnPhase.AceEffect)
    logic.applyAceEffect(logic.players(1).asInstanceOf[logic.PlayerType])
    logic.players(1).cards.size should be (logic.game.initialPlayerCardsNumber + 1)

  it should "not be activated if is not discarded" in:
    val logic = TestCactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.discard(1)
    logic.currentPhase should be(CactusTurnPhase.DiscardEquals)
