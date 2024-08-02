package model.game

import model.card.CardsData.PokerSuit.{Clubs, Hearts, Spades}
import model.card.CardBuilder.PokerDSL.OF
import model.card.Cards.{Card, Coverable, GenericCard}
import model.card.CardsData.PokerCardName.{Ace, King}
import model.deck.{Decks, Drawable}
import model.deck.Decks.Deck
import model.deck.Piles.PokerPile
import model.game.Scores
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, empty, have, not}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import model.player.Players.Player
import model.player.Players.CactusPlayer

class CactusGameTest extends AnyFlatSpec:
  type Players = List[Player]
  val playersNumber: Int = 3
  val game: Game         = CactusGame()
  val nonCactusPlayer: Player = new Player:
    override type CardType = Card & Coverable

    override val name: String = "Player"

    var cards: List[CardType] = List(new GenericCard(1, Spades) with Coverable, new GenericCard(2, Spades) with Coverable)

    override def draw(drawable: Drawable[CardType]): Unit = drawable.draw() match
      case Some(card) => cards = cards ::: card :: Nil
      case _          => ()

    override def drawCovered(drawable: Drawable[Card & Coverable]): Unit =
      draw(drawable)

    override def discard(cardIndex: Int): CardType = cards(cardIndex)

    override def isEqualsTo(anotherPlayer: Player): Boolean = this.name.compareTo(anotherPlayer.name) == 0 &&
      this.cards.diff(anotherPlayer.cards).isEmpty &&
      anotherPlayer.cards.diff(this.cards).isEmpty

  "Game setup " should "return the players" in:
    val game: Game       = CactusGame()
    val players: Players = game.setupGame(playersNumber)
    players should have size playersNumber

  "Initially players " should "have 4 cards in their hand" in:
    val game: CactusGame = CactusGame()
    val players: Players = game.setupGame(playersNumber)
    players.foreach(player => player.cards should have size game.initialPlayerCardsNumber)

  "Initialized players" should "always have different cards" in:
    val players1: Players = CactusGame().setupGame(playersNumber)
    val players2: Players = CactusGame().setupGame(playersNumber)
    players1 should not be players2

  "Drawn cards from players" should "not be in the deck anymore" in:
    val game: CactusGame       = CactusGame()
    val players: Players       = game.setupGame(playersNumber)
    val drawnCards: List[Card & Coverable] = players.flatMap(player => player.cards)
    game.deck.cards should not contain drawnCards

  "The discard pile" should "be empty" in:
    val game: CactusGame         = CactusGame()
    val cardOption: Option[Card & Coverable] = game.discardPile.draw() // game.drawFromDiscardPile()
    cardOption shouldBe empty

  "After player initialization deck " should " have less cards" in:
    val game: CactusGame = CactusGame()
    game.setupGame(playersNumber)
    game.deckSize should be(52 - playersNumber * game.initialPlayerCardsNumber)

  "Each card" should "score points equal to their value" in:
    val players: Players = (1 to 13)
      .map(index => index OF Spades)
      .map(card => List(card))
      .map(list => CactusPlayer("", list))
      .toList
    val scores: Scores = CactusGame().calculateScores(players)
    for (i <- 1 to 13)
      scores.get(players(i - 1)) should be(Some(i))

  "The sum OF the cards OF player" should "be consistent with their values" in:
    val player: CactusPlayer = CactusPlayer("", List(Ace OF Spades, 2 OF Spades, 3 OF Spades))
    val scores: Scores       = CactusGame().calculateScores(List(player))
    scores.get(player) should be(Some(Ace + 2 + 3))

  "If no players are passed it" should "return an empty map" in:
    val scores: Scores = CactusGame().calculateScores(List[Player]())
    scores.isEmpty should be(true)

  "Calculate scores OF player with non poker cards" should "return empty score" in:
    CactusGame().calculateScores(List(nonCactusPlayer)).isEmpty should be(true)

  "Calculate scores OF players with some OF them having non poker cards" should "return scores for only the players with poker cards" in:
    val players: Players = List(
      CactusPlayer("", List(Ace OF Spades, 2 OF Hearts)),
      nonCactusPlayer,
      CactusPlayer("", List(10 OF Clubs, 10 OF Spades))
    )
    val scores: Scores = CactusGame().calculateScores(players)
    scores.size should be(2)
    scores.get(players(0)) should be(Some(Ace + 2))
    scores.get(players(2)) should be(Some(10 + 10))
    scores.players should not contain nonCactusPlayer

  "Red King" should "count 0 on score calculation" in:
    val players: Players = List(
      CactusPlayer("", List(King OF Spades, King OF Hearts, 9 OF Clubs))
    )
    val scores: Scores = CactusGame().calculateScores(players)
    scores.get(players.headOption.get).get shouldBe 22

  "Discarded card effect" should "be recognised if the discard pile is not empty" in:
    val game: CactusGame = CactusGame()
    game.discardPile = PokerPile(List(Ace OF Spades))
    game.checkCardEffect() should be(CactusCardEffect.AceEffect)
    game.discardPile.size should be(1)

  it should "be none if the discard pile is empty" in:
    val game: CactusGame = CactusGame()
    game.checkCardEffect() should be(CactusCardEffect.NoEffect)
    game.discardPile.size should be(0)

  it should "be none if the card on top of discard pile has no effects" in:
    val game: CactusGame = CactusGame()
    game.discardPile = PokerPile(List(2 OF Spades))
    game.checkCardEffect() should be(CactusCardEffect.NoEffect)
    game.discardPile.size should be(1)
