package model.player

import model.card.CardBuilder.PokerDSL.OF
import model.card.Cards.{Card, Coverable, PokerCard}
import model.card.CardsData
import model.card.CardsData.PokerSuit.*
import model.card.CardsData.PokerCardName.Ace
import model.deck.Decks.{Deck, PokerDeck}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import model.player.Players.{CactusPlayer, Player}

class PlayerTest extends AnyFlatSpec:

  "Player " should "be a CactusPlayer" in:
    val player: CactusPlayer = CactusPlayer("", List.empty[PokerCard & Coverable])
    player shouldBe a[CactusPlayer]

  "Player " should "have a non-null value" in:
    val player: CactusPlayer = CactusPlayer("", List.empty[PokerCard & Coverable])
    player.cards shouldBe a[List[Card & Coverable]]

  "Player " should "have 1 cards after a draw" in :
    val player: CactusPlayer = CactusPlayer("", List.empty[PokerCard & Coverable])
    val deck: Deck[PokerCard & Coverable] = PokerDeck()
    player.draw(deck)
    player.cards.length shouldBe 1

  "Player " should "have Ace of Spades in his hand" in :
    val player: CactusPlayer = CactusPlayer("", List.empty[PokerCard & Coverable])
    val deck: Deck[PokerCard & Coverable] = PokerDeck()
    player.draw(deck)
    player.cards(0) should be (Ace OF Spades)

  "Player " should "have 0 cards after discard" in:
    val player: CactusPlayer = CactusPlayer("", List(5 OF Clubs))
    player.discard(0)
    player.cards.length shouldBe 0

  "Discarded card " should "be 9 of Spades" in:
    val player: CactusPlayer = CactusPlayer("", List(9 OF Spades, 7 OF Clubs))
    val discardedCard = player.discard(0)
    discardedCard shouldBe PokerCard(9, CardsData.PokerSuit.Spades)

  "Players with same name and same cards" should "be equal" in:
    val player1: Player = CactusPlayer("Mario", List(1 OF Spades))
    val player2: Player = CactusPlayer("Mario", List(1 OF Spades))
    player1.isEqualsTo(player2) should be (true)

  "Players with same name and different cards" should "not be equal" in :
    val player1: Player = CactusPlayer("Mario", List(1 OF Spades))
    val player2: Player = CactusPlayer("Mario", List(2 OF Spades))
    player1.isEqualsTo(player2) should be (false)

  "Players with same name and different number of cards" should "not be equal" in :
    val player1: Player = CactusPlayer("Mario", List(1 OF Spades))
    val player2: Player = CactusPlayer("Mario", List(1 OF Spades, 2 OF Spades))
    player1.isEqualsTo(player2) should be (false)

  "Players with different name and same cards" should "not be equal" in :
    val player1: Player = CactusPlayer("Mario", List(1 OF Spades))
    val player2: Player = CactusPlayer("Luigi", List(1 OF Spades))
    player1.isEqualsTo(player2) should be (false)

  "Player" should "be equal to himself" in:
    val player: Player = CactusPlayer("Mario", List(1 OF Spades, 2 OF Spades))
    player.isEqualsTo(player) should be (true)