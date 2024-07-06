package model.logic

import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.Card
import model.card.CardsData.PokerSuit.*
import model.deck.DeckUtils.Drawable
import model.game.Scores
import model.game.Scores.toMap
import model.logic.Logics.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import player.Players.Player

/** Tests for basic game logic. */
class LogicTest extends AnyFlatSpec:

  val N: Int = 10
  val nPlayers: Int = 3

  /** Simple player implementation for testing. */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class PlayerImpl(name: String) extends Player:
    var cards: List[Card] = List()

    override def draw(deck: Drawable): Unit = None

    override def discard(cardIndex: Int): Card = 2 of Spades

  /** Simple game logic implementation for testing. */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class TestLogic(nPlayers: Int) extends AbstractLogic(nPlayers):
    type Score = Int

    override val _players: Players = (1 to nPlayers).toList.map(i => PlayerImpl(s"Player $i"))
    private var _counter = 0

    override def playTurn(): Unit = _counter += 1
    override def isGameOver: Boolean = _counter == N
    override def calculateScore: Scores = Scores(players.map(player => player -> _counter).toMap)

  "The players" should "play turns cyclically" in :
    val logic = TestLogic(nPlayers)
    for _ <- 1 to nPlayers do logic.playTurn()
    logic.playTurn()
    for (_, s) <- toMap(logic.calculateScore) do s should be (nPlayers + 1)

  "A game logic" should "provide a score of the game" in:
    val logic = TestLogic(nPlayers)
    logic.calculateScore should be (Map(
      PlayerImpl("Player 1") -> 0,
      PlayerImpl("Player 2") -> 0,
      PlayerImpl("Player 3") -> 0)
    )

  it should "play turns until the game is over" in:
    val logic = TestLogic(nPlayers)
    logic.gameLoop()
    for (_, s) <- toMap(logic.calculateScore) do s should be (N)
