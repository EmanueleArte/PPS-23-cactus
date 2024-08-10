package control.module.cactus

import control.module.ControllerModule
import model.module.cactus.CactusModelModule
import model.card.Cards.{Coverable, PokerCard}
import model.logic.Logics.Players
import model.logic.{CactusTurnPhase, TurnPhase}
import model.player.Players.{CactusPlayer, Player}
import mvc.TutorialMVC
import mvc.PlayableGame.Cactus
import mvc.FinalScreenMVC
import scalafx.application.Platform
import view.module.cactus.CactusViewModule

import scala.collection.immutable.ListMap

/** Represents the controller module for the Cactus game. */
object CactusControllerModule extends ControllerModule:
  override type ControllerType = CactusController
  override type Requirements   = CactusModelModule.Provider with CactusViewModule.Provider

  trait CactusController extends Controller:
    /** Shows the tutorial. */
    def showTutorial(): Unit

    /** Continues to the next step. */
    def continue(): Unit

    /**
     * Makes player to draw a card from the deck or the discard pile.
     *
     * @param fromDeck if `true` the card is drawn from the deck, if `false` it is drawn from the discard pile.
     */
    def draw(fromDeck: Boolean): Unit

    /**
     * Handles the player input according to the turn phase.
     *
     * @param index index of the card in the player hand or the player to handle.
     */
    def handlePlayerInput(index: Int): Unit

    /** Makes player to call Cactus, in order to end the game. */
    def callCactus(): Unit

    /**
     * Returns the players of the game.
     * @return players of the game.
     */
    def players: Players

    /**
     * Retrieve an [[Option]] with the card on top of the discard pile (if present).
     * @return [[Option]] with the card on top of the discard pile.
     */
    def pilesHead: Option[PokerCard & Coverable]

    /**
     * Returns the current phase of the game.
     * @return current phase of the game.
     */
    def currentPhase: TurnPhase

    /**
     * Returns the player which is playing in the current turn.
     * @return the current player
     */
    def currentPlayer: CactusPlayer

    /**
     * Getter for the human player.
     * @return the human player.
     */
    def humanPlayer: Player

  /** Represents the controller component for the Cactus game. */
  trait Component:
    context: Requirements =>

    /** Represents the controller for the Cactus game. */
    class CactusControllerImpl extends CactusController:

      override def showTutorial(): Unit = TutorialMVC.run(Cactus)

      @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
      override def continue(): Unit = context.model.currentPhase match
        case CactusTurnPhase.GameOver =>
          val finalScreenMVC = FinalScreenMVC
          finalScreenMVC.setup(
            ListMap(
              context.model.calculateScore.asInstanceOf[Map[CactusPlayer, Integer]].toSeq.sortWith(_._2 < _._2): _*
            )
          )
          finalScreenMVC.run()
        case _ =>
          context.model.continue()
          context.view.updateViewTurnPhase()
          context.view.updateDiscardPile()

      override def draw(fromDeck: Boolean): Unit =
        context.model.draw(fromDeck)
        context.view.updateViewTurnPhase()

      override def handlePlayerInput(index: Int): Unit =
        context.model.movesHandler(index)
        context.view.updateViewTurnPhase()

      override def callCactus(): Unit =
        context.model.callCactus()
        context.view.updateViewTurnPhase()

      override def players: Players = context.model.players

      override def humanPlayer: Player = context.model.humanPlayer

      override def pilesHead: Option[PokerCard & Coverable] = context.model.game.discardPile.cards.headOption

      override def currentPhase: TurnPhase = context.model.currentPhase

      override def currentPlayer: CactusPlayer = context.model.currentPlayer

  /** Interface of the controller module of Cactus game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
