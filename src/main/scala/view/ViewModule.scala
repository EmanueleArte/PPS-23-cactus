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
    def players: List[Player]
  trait Provider:
    val controller: Controller

  trait Component:
    class ScalaFXController extends Controller:
      private def cards: List[PokerCard] = (1 to 10).map(_ of Diamonds).toList
      override def players: List[Player] = (1 to 3).toList.map(i => CactusPlayer(s"Bot$i", cards))

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
        def width: Int = 1800

        def height: Int = 900

        override def start(): Unit =
          stage = new JFXApp3.PrimaryStage:
            title.value = "Cactus"
            scene = new Scene(ScalaFXWindow.width, ScalaFXWindow.height):
              content = List(MainPane(context).pane)

  trait Interface extends Provider with Component:
    self: Requirements =>

object MVC extends ViewModule.Interface with ControllerModule.Interface:
  override val view       = ScalaFXView()
  override val controller = ScalaFXController()

  @main def main(): Unit =
    view.show
    val pile: PokerPile = PokerPile()
    val clonedPile: PokerPile = pile.co