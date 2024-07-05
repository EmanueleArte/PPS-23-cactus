package model.game

import card.CardsData.PokerSuit.{Clubs, Hearts, Spades}
import card.CardBuilder.PokerDSL.of
import card.Cards.Card
import card.CardsData.PokerCardName.Ace
import model.deck.Decks
import model.game.Scores
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, empty, have, not}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import player.Players.{CactusPlayer, Player}

@SuppressWarnings(Array("org.wartremover.warts.All"))
class CactusGameTest extends AnyFlatSpec:
  type Players = List[Player]
  val playersNumber: Int = 3
  val game: Game = CactusGame()
  val nonCactusPlayer: Player = new Player:
    var cards: List[Card] = List(Card(1, Spades), Card(2, Spades))

    override def draw(deck: Decks.Deck): Unit = deck.draw() match
      case Some(card) => cards = cards :+ card
      case _ => ()

    override def discard(cardIndex: Int): Card = cards(cardIndex)

  "Game setup " should "return the players" in:
    val game: Game = CactusGame()
    val players: Players = game.setupGame(playersNumber)
    players should have size playersNumber

  "Initially players " should "have 4 cards in their hand" in:
    val game: CactusGame = CactusGame()
    @SuppressWarnings(Array("org.wartremover.warts.All"))
    val players: Players = game.setupGame(playersNumber)
    players.foreach(player => player.cards should have size 4)

  "Initialized players" should "always have different cards" in:
    val players1: Players = CactusGame().setupGame(playersNumber)
    val players2: Players = CactusGame().setupGame(playersNumber)
    players1 should not be players2

  "Drawn cards from players" should "not be in the deck anymore" in:
    val game: CactusGame = CactusGame()
    val players: Players = game.setupGame(playersNumber)
    val drawnCards: List[Card] = players.flatMap(player => player.cards)
    game.deck.cards should not contain drawnCards

  "The discard pile" should "be empty" in:
    val game: CactusGame = CactusGame()
    val cardOption: Option[Card] = game.drawFromDiscardPile()
    cardOption shouldBe empty

  "After player initialization deck " should " have less cards" in:
    val game: CactusGame = CactusGame()
    game.setupGame(playersNumber)
    game.deckSize should be (52 - playersNumber * 4)

  "Each card" should "score points equal to their value" in:
    val players: Players = (1 to 13)
      .map(index => index of Spades)
      .map(card => List(card))
      .map(list => CactusPlayer(list))
      .toList
    val scores: Scores = CactusGame().calculateScores(players)
    for (i <- 1 to 13)
      scores.get(players(i - 1)) should be (Some(i))

  "The sum of the cards of player" should "be consistent with their values" in:
    val player: CactusPlayer = CactusPlayer(List(Ace of Spades, 2 of Spades, 3 of Spades))
    val scores: Scores = CactusGame().calculateScores(List(player))
    scores.get(player) should be (Some(Ace + 2 + 3))

  "If no players are passed it" should "return an empty map" in:
    val scores: Scores = CactusGame().calculateScores(List())
    scores.isEmpty should be (true)

  "Calculate scores of player with non poker cards" should "return empty score" in:
    CactusGame().calculateScores(List(nonCactusPlayer)).isEmpty should be (true)

  "Calculate scores of players with some of them having non poker cards" should "return scores for only the players with poker cards" in:
    val players: Players = List(
      CactusPlayer(List(Ace of Spades, 2 of Hearts)),
      nonCactusPlayer,
      CactusPlayer(List(10 of Clubs, 10 of Spades))
    )
    val scores: Scores = CactusGame().calculateScores(players)
    scores.size should be (2)
    scores.get(players(0)) should be (Some(Ace + 2))
    scores.get(players(2)) should be (Some(10 + 10))
    scores.players should not contain nonCactusPlayer
