package model.game

import card.Cards.Card
import model.game.Games.{CactusGame, Game, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, empty, have}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}

class CactusGameTest extends AnyFlatSpec:
  type Players = List[Player]
  val playersNumber: Int = 3
  val game: Game = CactusGame()

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