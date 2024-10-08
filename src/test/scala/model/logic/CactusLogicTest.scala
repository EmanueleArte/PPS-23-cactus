package model.logic

import model.bot.Bots.{BotParamsType, CactusBot}
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.card.Cards.{Coverable, PokerCard}
import model.deck.Decks.{Deck, PokerDeck}
import model.game.CactusGame
import model.game.Scores.toMap
import model.logic
import model.logic.Logics.CactusLogic
import model.player.Players.{CactusPlayer, Player}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec

@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
/** Test for [[CactusLogic]]. */
class CactusLogicTest extends AnyFlatSpec:

  val playersNumber: Int = 4
  val deckSize: Int      = 52

  /** Custom implementation of the CactusGame to make tests with an unshuffled deck without bots. */
  class TestCactusLogic(nPlayers: Int) extends CactusLogic(Left(nPlayers): Either[Int, BotParamsType]):
    override lazy val game: CactusGame = new CactusGame():
      override val deck: Deck[PokerCard & Coverable] = PokerDeck()
      override def setupGame(playersNumber: Int): List[Player] = (1 to nPlayers).toList.map(i =>
        CactusPlayer(s"Player-$i", (1 to game.initialPlayerCardsNumber).toList.map(_ => game.deck.draw().get))
      )

  /** Custom implementation of the CactusGame to make tests with an unshuffled deck. */
  class TestCactusLogicBots(nPlayers: Int) extends CactusLogic(Left(nPlayers): Either[Int, BotParamsType]):
    override lazy val game: CactusGame = new CactusGame():
      override val deck: Deck[PokerCard & Coverable] = PokerDeck()

  /** Custom implementation of the CactusGame to make tests with an unshuffled deck and bot config. */
  class TestCactusLogicBotsConfigured(botParamsType: BotParamsType)
      extends CactusLogic(Right(botParamsType): Either[Int, BotParamsType]):
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
    logic.movesHandler(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be(1)

  it should "draw from the discard pile" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Discard)
    logic.movesHandler(0)
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
    logic.movesHandler(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.game.discardPile.size should be(1)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "have a malus if he discards a card not in the classic discard phase and the discard pile is empty" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.DiscardEquals)
    logic.movesHandler(0)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber + 1)
    logic.game.discardPile.size should be(0)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "have a malus if he discards an incorrect card not in the classic discard phase" in:
    val logic = TestCactusLogicBots(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Discard)
    logic.movesHandler(1)
    logic.nextPlayer
    logic.discardWithMalus(1)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber + 1)
    logic.game.discardPile.size should be(1)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber - 1)

  it should "discard a card if it matches the criteria of the non-classic discard" in:
    val logic = TestCactusLogicBots(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Discard)
    logic.movesHandler(1)
    for _ <- 1 to 3 do logic.nextPlayer
    logic.discardWithMalus(2)
    logic.currentPlayer.cards.size should be(logic.game.initialPlayerCardsNumber - 1)
    logic.game.discardPile.size should be(2)
    logic.game.deckSize should be(deckSize - playersNumber * logic.game.initialPlayerCardsNumber)

  it should "be untargetable after calling cactus" in:
    val logic = TestCactusLogic(playersNumber)
    while logic.currentPhase != CactusTurnPhase.GameOver do
      logic.currentPhase_=(CactusTurnPhase.Draw)
      logic.draw(true)
      logic.movesHandler(1)
      logic.currentPhase match
        case CactusTurnPhase.AceEffect =>
          logic.movesHandler(0)
          logic.movesHandler(1)
        case _ => ()
      logic.continue()
      logic.callCactus()
      logic.continue()
      logic.continue()
    logic.getPlayer(0).cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.getPlayer(1).cards.size should be(logic.game.initialPlayerCardsNumber + 1)
    logic.getPlayer(2).cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.getPlayer(3).cards.size should be(logic.game.initialPlayerCardsNumber)

  it should "not draw a card if targeted by an ace effect after calling cactus" in:
    val logic = TestCactusLogic(2)
    // Player 1
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.movesHandler(1)
    logic.continue()
    logic.continue()
    logic.continue()
    // Player 2
    logic.draw(true)
    logic.movesHandler(1)
    logic.continue()
    logic.callCactus()
    logic.continue()
    // Player 1
    logic.draw(true)
    logic.movesHandler(0)
    logic.currentPhase match
      case CactusTurnPhase.AceEffect =>
        logic.movesHandler(0)
        logic.movesHandler(1)
      case _ => ()
    logic.getPlayer(0).cards.size should be(logic.game.initialPlayerCardsNumber)
    logic.getPlayer(1).cards.size should be(logic.game.initialPlayerCardsNumber)

  "Players" should "make a complete match using basic moves" in:
    val logic = TestCactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    while logic.currentPhase != CactusTurnPhase.GameOver do
      logic.draw(true)
      logic.movesHandler(1)
      logic.currentPhase_=(CactusTurnPhase.CallCactus)
      logic.callCactus()
      logic.continue()
    logic.game.deckSize should be <= (deckSize - playersNumber * logic.game.initialPlayerCardsNumber - playersNumber)
    logic.game.discardPile.size should be(playersNumber)

  "At the end of a Cactus match" should "be possible to calculate the scores" in:
    val logic = CactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    while logic.currentPhase != CactusTurnPhase.GameOver do
      logic.draw(true)
      val cp = logic.currentPlayer
      logic.movesHandler(1)
      logic.continue()
      logic.callCactus()
      logic.continue()
    for (_, score) <- toMap(logic.calculateScore) do score should be > 0

  "A bot" should "discard a card in non-classic way if the head of the pile has the same value" in:
    val drawings: Seq[DrawMethods]       = Seq.fill(playersNumber - 1)(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq.fill(playersNumber - 1)(DiscardMethods.Random)
    val memories: Seq[Memory]            = Seq.fill(playersNumber - 1)(Memory.Optimal)
    val logic                            = TestCactusLogicBotsConfigured((drawings, discardings, memories))
    logic.players.foreach {
      case bot: CactusBot =>
        (0 until logic.game.initialPlayerCardsNumber).foreach(i => bot.seeCard(i))
      case _ => ()
    }
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.movesHandler(1)
    logic.continue()
    logic.continue()
    logic.continue()
    logic.getPlayer(3).cards.size should be(logic.game.initialPlayerCardsNumber - 1)

  it should "call cactus if it has a good hand" in:
    val maxPlayersNumber                 = 6
    val drawings: Seq[DrawMethods]       = Seq.fill(maxPlayersNumber - 1)(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq.fill(maxPlayersNumber - 1)(DiscardMethods.Random)
    val memories: Seq[Memory]            = Seq.fill(maxPlayersNumber - 1)(Memory.Optimal)
    val logic                            = TestCactusLogicBotsConfigured((drawings, discardings, memories))
    logic.players.foreach {
      case bot: CactusBot =>
        (0 until logic.game.initialPlayerCardsNumber).foreach(i => bot.seeCard(i))
      case _ => ()
    }
    var nCardsOfCactusCaller = -1
    while logic.currentPhase != CactusTurnPhase.GameOver do
      logic.currentPlayer match
        case bot: CactusBot =>
          logic.continue()
          logic.players(logic.players.indexOf(bot)) match
            case p: CactusPlayer =>
              if p.calledCactus then nCardsOfCactusCaller = p.cards.size
            case _ => ()
        case _ =>
          logic.currentPhase_=(CactusTurnPhase.Draw)
          logic.draw(true)
          logic.movesHandler(1)
          logic.currentPhase_=(CactusTurnPhase.DiscardEquals)
          logic.continue()
          logic.continue()
          logic.continue()
    logic.currentPhase should be(CactusTurnPhase.GameOver)
    logic.players.find(_.asInstanceOf[CactusPlayer].calledCactus).get.cards.size should be(nCardsOfCactusCaller)

  it should "see a card after discarding a Jack" in:
    val drawings: Seq[DrawMethods]       = Seq.fill(playersNumber - 1)(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq.fill(playersNumber - 1)(DiscardMethods.Random)
    val memories: Seq[Memory]            = Seq.fill(playersNumber - 1)(Memory.Optimal)
    val logic                            = TestCactusLogicBotsConfigured((drawings, discardings, memories))
    logic.nextPlayer
    val knownCardsLength = logic.nextPlayer.asInstanceOf[CactusBot].knownCards.length
    logic.currentPhase = CactusTurnPhase.Discard
    logic.movesHandler(2)
    logic.currentPhase should be(CactusTurnPhase.JackEffect)
    logic.continue()
    logic.currentPlayer.asInstanceOf[CactusBot].knownCards.length shouldBe (knownCardsLength + 1)
    logic.currentPhase should be(CactusTurnPhase.DiscardEquals)

  "A game consisting on basic moves" should "be played with bots" in:
    val drawings: Seq[DrawMethods]       = Seq.fill(playersNumber - 1)(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq.fill(playersNumber - 1)(DiscardMethods.Random)
    val memories: Seq[Memory]            = Seq.fill(playersNumber - 1)(Memory.Optimal)
    val logic                            = TestCactusLogicBotsConfigured((drawings, discardings, memories))
    while logic.currentPhase != CactusTurnPhase.GameOver do
      logic.currentPlayer match
        case bot: CactusBot =>
          logic.continue()
        case _ =>
          logic.currentPhase_=(CactusTurnPhase.Draw)
          logic.draw(true)
          logic.movesHandler(1)
          logic.continue()
          logic.callCactus()
          logic.continue()
    for (_, score) <- toMap(logic.calculateScore) do score should be > 0
    logic.game.deckSize should be <= 32

  it should "be played with the player and one bot" in:
    val drawings: Seq[DrawMethods]       = Seq(DrawMethods.Deck)
    val discardings: Seq[DiscardMethods] = Seq(DiscardMethods.Random)
    val memories: Seq[Memory]            = Seq(Memory.Optimal)
    val logic                            = TestCactusLogicBotsConfigured((drawings, discardings, memories))
    while logic.currentPhase != CactusTurnPhase.GameOver do
      logic.currentPlayer match
        case bot: CactusBot =>
          logic.continue()
        case _ =>
          logic.currentPhase_=(CactusTurnPhase.Draw)
          logic.draw(true)
          logic.movesHandler(1)
          logic.continue()
          logic.callCactus()
          logic.continue()
    for (_, score) <- toMap(logic.calculateScore) do score should be > 0
    logic.game.deckSize should be(42)

  "The effect of an ace" should "be activated when it is discarded" in:
    val logic = TestCactusLogicBots(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.movesHandler(0)
    logic.currentPhase should be(CactusTurnPhase.AceEffect)
    logic.movesHandler(1)
    logic.getPlayer(1).cards.size should be(logic.game.initialPlayerCardsNumber + 1)

  it should "not be activated if is not discarded" in:
    val logic = TestCactusLogicBots(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.movesHandler(1)
    logic.currentPhase should be(CactusTurnPhase.DiscardEquals)

  "When the game starts the player" should "see 2 cards" in:
    val logic: CactusLogic = CactusLogic(4)
    logic.currentPlayer.cards.count(!_.isCovered) should be(0)
    logic.seeCard(0)
    logic.seeCard(1)
    logic.currentPlayer.cards.count(!_.isCovered) should be(2)

  "When the player sees 2 cards, then he" should "draw" in:
    val logic = CactusLogic(4)
    logic.seeCard(0)
    logic.seeCard(1)
    logic.currentPhase should be(CactusTurnPhase.Draw)

  it should "not see more than 2 cards" in:
    val logic = CactusLogic(4)
    logic.seeCard(0)
    logic.seeCard(1)
    logic.seeCard(2)
    logic.currentPlayer.cards.count(!_.isCovered) should be(2)

  "If the player sees less than 2 cards he" should "remain in the same turn phase" in:
    val logic = CactusLogic(4)
    logic.seeCard(0)
    logic.currentPhase should be(BaseTurnPhase.Start)

  "Seeing the same card 2 times" should "not count as 2 different cards" in:
    val logic = CactusLogic(4)
    logic.seeCard(0)
    logic.seeCard(0)
    logic.currentPlayer.cards.count(!_.isCovered) should be(1)

  "Passing an index out of bounds" should "make the requirements fail" in:
    val logic                  = CactusLogic(4)
    val playerCardsNumber: Int = logic.currentPlayer.cards.size
    an[IllegalArgumentException] should be thrownBy logic.seeCard(-1)
    an[IllegalArgumentException] should be thrownBy logic.seeCard(playerCardsNumber)

  "As soon as Draw phase is reached, the cards in player's hand" should "be covered" in:
    val logic = CactusLogic(4)
    logic.seeCard(0)
    logic.seeCard(1)
    logic.draw(fromDeck = true)
    logic.currentPlayer.cards.count(!_.isCovered) should be(1)

  "When the game starts the bot" should "know at maximum 2 cards" in:
    val logic = CactusLogic(2)
    import model.bot.Bots.CactusBotImpl
    logic.players(1).asInstanceOf[CactusBotImpl].knownCards.size should be <= 2

  "The card drawn from an ace special effect" should "be covered" in:
    val logic = TestCactusLogic(playersNumber)
    logic.currentPhase_=(CactusTurnPhase.Draw)
    logic.draw(true)
    logic.movesHandler(0)
    logic.currentPhase should be(CactusTurnPhase.AceEffect)
    logic.movesHandler(1)
    logic.getPlayer(1).cards.foreach(c => c.isCovered shouldBe true)

  "Cactus" should "be called by only one player for match" in:
    val logic = CactusLogic(playersNumber)
    while logic.currentPhase != CactusTurnPhase.GameOver do
      logic.currentPhase_=(CactusTurnPhase.Draw)
      logic.draw(true)
      logic.movesHandler(1)
      logic.continue()
      logic.callCactus()
      logic.continue()
    logic.players.count(_.asInstanceOf[CactusPlayer].calledCactus) should be(1)
