package model.logic

import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.Card
import model.card.CardsData.PokerSuit.*
import model.deck.Decks.Deck
import model.logic.Logics.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import player.Players.Player

/** Tests for basic game logic. */
class LogicTest extends AnyFlatSpec:
  type Players = List[Player]

  val N: Int = 10

  /** Simple game logic implementation for testing. */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class TestLogic(players: List[Player]) extends AbstractLogic(players: List[Player]):
    type Score = Int

    protected var _counter = 0

    override def playTurn(): Unit = _counter += 1
    override def isGameOver: Boolean = _counter == N
    override def calculateScore: Map[Player, Score] = players.map(player => player -> _counter).toMap

  /** Simple player implementation for testing. */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  case class PlayerImpl(name: String) extends Player:
    var cards: List[Card] = List()
    override def draw(deck: Deck): Unit = None
    override def discard(cardIndex: Int): Card = 2 of Spades

  val players: Players = List(PlayerImpl("Alice"), PlayerImpl("Bob"), PlayerImpl("Charlie"))

  "The players" should "play turns cyclically" in :
    val logic = TestLogic(players)
    for _ <- players do logic.playTurn()
    logic.playTurn()
    for (_, s) <- logic.calculateScore do s should be(players.size + 1)

  "A game logic" should "provide a score of the game" in:
    val logic = TestLogic(players)
    logic.calculateScore should be (Map(PlayerImpl("Alice") -> 0, PlayerImpl("Bob") -> 0, PlayerImpl("Charlie") -> 0))

  it should "play turns until the game is over" in:
    val logic = TestLogic(players)
    logic.gameLoop()
    for (_, s) <- logic.calculateScore do s should be (N)
