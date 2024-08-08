package model.logic

import model.card.CardBuilder.PokerDSL.OF
import model.card.Cards.{Card, Coverable}
import model.card.CardsData.PokerSuit.*
import model.deck.Drawable
import model.game.Scores
import model.game.Scores.toMap
import model.logic.Logics.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import model.player.Players.Player

/** Tests for basic game logic. */
class LogicTest extends AnyFlatSpec:

  val N: Int        = 10
  val nPlayers: Int = 3

  /** Simple player implementation for testing. */
  case class PlayerImpl(name: String) extends Player:
    override type CardType = Card & Coverable

    var cards: List[Card & Coverable] = List[Card & Coverable]()

    override def draw(deck: Drawable[CardType]): Unit = None

    override def drawCovered(deck: Drawable[CardType]): Unit = None

    override def discard(cardIndex: Int): Card & Coverable = 2 OF Spades

    override def isEqualTo(anotherPlayer: Player): Boolean =
      this.name.compareTo(anotherPlayer.name) == 0 && this.cards.diff(anotherPlayer.cards).isEmpty

  /** Simple game logic implementation for testing. */
  class TestLogic(nPlayers: Int) extends Logic:
    type Score = Int
    type PlayerType = PlayerImpl

    override val _players: Players = (1 to nPlayers).toList.map(i => PlayerImpl(s"Player $i"))
    private var _counter           = 0

    override def getPlayer(index: Int): PlayerType = PlayerImpl(s"Player $index")
    override def humanPlayer: Player = PlayerImpl(s"Player 0")
    override def continue(): Unit       = _counter += 1
    override def isGameOver: Boolean    = _counter == N
    override def calculateScore: Scores = Scores(players.map(player => player -> _counter).toMap)
    override def seeCard(cardIndex: Int): Unit = ()
  
  "The players" should "play turns cyclically" in:
    val logic = TestLogic(nPlayers)
    logic.currentPlayer should be(PlayerImpl("Player 1"))
    for _ <- 1 to nPlayers do
      logic.continue()
      logic.nextPlayer
    logic.currentPlayer should be(PlayerImpl("Player 1"))
    for (_, s) <- toMap(logic.calculateScore) do s should be(nPlayers)

  "A game logic" should "provide a score of the game" in:
    val logic = TestLogic(nPlayers)
    logic.calculateScore should be(
      Map(PlayerImpl("Player 1") -> 0, PlayerImpl("Player 2") -> 0, PlayerImpl("Player 3") -> 0)
    )

  it should "play turns until the game is over" in:
    val logic = TestLogic(nPlayers)
    while !logic.isGameOver do logic.continue()
    for (_, s) <- toMap(logic.calculateScore) do s should be(N)
