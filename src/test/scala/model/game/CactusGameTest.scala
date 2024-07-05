package model.game

import card.CardsData.PokerSuit.{Clubs, Hearts, Spades}
import card.CardBuilder.PokerDSL.of
import card.Cards.Card
import card.CardsData.PokerCardName.Ace
import model.deck.Decks
import model.game.Games.{CactusGame, Game}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{an, be, contain, empty, have, not}
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

  "The discard pile " should " be empty" in:
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
    val scores: Map[Player, Int] = CactusGame().calculateScores(players)
    for (i <- 1 to 13)
      scores(players(i - 1)) should be (i)

  "The sum of the cards of player" should "be consistent with their values" in:
    val player: CactusPlayer = CactusPlayer(List(Ace of Spades, 2 of Spades, 3 of Spades))
    val scores: Map[Player, Int] = CactusGame().calculateScores(List(player))
    scores(player) should be (Ace + 2 + 3)

  "If no players are passed it" should "return an empty map" in:
    val scores: Map[Player, Int] = CactusGame().calculateScores(List())
    scores should be (empty)

  "Calculate scores of player with non poker cards" should "return empty score" in:
    CactusGame().calculateScores(List(nonCactusPlayer)) should be (empty)

  "Calculate scores of players with some of them having non poker cards" should "return scores for only the players with poker cards" in:
    val players: Players = List(
      CactusPlayer(List(Ace of Spades, 2 of Hearts)),
      nonCactusPlayer,
      CactusPlayer(List(10 of Clubs, 10 of Spades))
    )
    val scores: Map[Player, Int] = CactusGame().calculateScores(players)
    scores.size should be (2)
    scores(players(0)) should be (Ace + 2)
    scores(players(2)) should be (10 + 10)
    scores.keys should not contain nonCactusPlayer
