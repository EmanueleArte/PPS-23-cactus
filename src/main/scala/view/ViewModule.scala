package view

import model.card.CardBuilder.PokerDSL.of
import model.card.Cards.PokerCard
import model.card.CardsData.PokerSuit.*
import model.card.CardsData.PokerCardName.Ace
import model.deck.Decks.PokerDeck
import model.deck.Piles.{DiscardPile, PokerPile}
import player.Players.{CactusPlayer, Player}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.text.Text

object ControllerModule:
  trait Controller:
    def players: List[CactusPlayer]
    def deck: PokerDeck
    def pile: PokerPile
    def playerDiscards(player: CactusPlayer, index: Int): Unit

  trait Provider:
    val controller: Controller

  trait Component:
    class ScalaFXController extends Controller:
      import scala.util.Random
      import model.card.CardsData.PokerSuit
      var _deck: PokerDeck = PokerDeck(true)
      var _pile: PokerPile = PokerPile()
      val _players: List[CactusPlayer] = CactusPlayer("Player", cards) +: (1 to 4).toList.map(i => CactusPlayer(s"Bot$i", cards))
      private def cards: List[PokerCard] =
        (1 to Random.nextInt(9) + 1).map(_ of PokerSuit.values(Random.nextInt(4))).toList
      override def players: List[CactusPlayer] = _players
      override def deck: PokerDeck = _deck
      override def pile: PokerPile = _pile

      override def playerDiscards(player: CactusPlayer, index: Int): Unit =
//        println("Prima")
//        println(_pile.cards)
//        println(players(0).cards)
        val discardedCard: PokerCard = player.discard(index)
        _pile = pile.put(discardedCard)
//        println("Dopo")
//        println(_pile.cards)
//        println(players(0).cards)

  trait Interface extends Provider with Component

object ViewModule:
  trait View:
    def show: Unit
  trait Provider:
    val view: View

  type Requirements = ControllerModule.Provider
  trait Component:
    context: Requirements =>
    class ScalaFXView() extends View:
      override def show: Unit = ScalaFXWindow.main(Array.empty)

      object ScalaFXWindow extends JFXApp3:
        def width: Int = Panes.windowWidth

        def height: Int = Panes.windowHeight

        override def start(): Unit =
          stage = new JFXApp3.PrimaryStage:
            title.value = "Cactus"
            scene = new Scene(ScalaFXWindow.width, ScalaFXWindow.height):
              content = List(MainPane(context).pane, AsidePane(context).pane)

  trait Interface extends Provider with Component:
    self: Requirements =>

object MVC extends ViewModule.Interface with ControllerModule.Interface:
  override val view       = ScalaFXView()
  override val controller = ScalaFXController()

  @main def main(): Unit =
    view.show
