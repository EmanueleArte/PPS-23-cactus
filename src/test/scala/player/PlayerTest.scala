package player

import model.card.Cards.Card
import model.card.CardsData
import model.deck.Decks.{Deck, PokerDeck}
import model.game.Games.CactusGame
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import player.Players.CactusPlayer

@SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
class PlayerTest extends AnyFlatSpec {

  val name: String = "Player"

  "Player " should "be a HumanPlayer" in:
    val player: CactusPlayer = CactusPlayer(name, List.empty[Card])
    player shouldBe a[CactusPlayer]

  "Player " should "have a non-null value" in:
    val player: CactusPlayer = CactusPlayer(name, List.empty[Card])
    player.cards shouldBe a[List[Card]]

  "Player " should "have 1 cards after a draw" in:
    val player: CactusPlayer = CactusPlayer(name, List.empty[Card])
    val deck: Deck = PokerDeck()
    player.draw(deck)
    player.cards.length shouldBe 1

  "Player " should "have 3 of Diamonds after drawing 3 of Diamonds" in:
    val player: CactusPlayer = CactusPlayer(name, List.empty[Card])
    val deck: Deck = Deck(3 to 3, List(CardsData.PokerSuit.Diamonds), false)
    player.draw(deck)
    player.cards.head shouldBe Card(3, CardsData.PokerSuit.Diamonds)

  "Player " should "have 0 cards after discard" in:
    val player: CactusPlayer = CactusPlayer(name, List(Card(5, CardsData.PokerSuit.Clubs)))
    player.discard(0)
    player.cards.length shouldBe 0

  "Discarded card " should "be 9 of Spades" in:
    val player: CactusPlayer = CactusPlayer(name, List(Card(9, CardsData.PokerSuit.Spades), Card(7, CardsData.PokerSuit.Clubs)))
    val discardedCard = player.discard(0)
    discardedCard shouldBe Card(9, CardsData.PokerSuit.Spades)
}
