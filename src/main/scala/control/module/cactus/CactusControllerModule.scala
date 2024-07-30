package control.module.cactus

import control.module.ControllerModule
import model.module.cactus.CactusModelModule
import model.card.Cards.{Coverable, PokerCard}
import model.logic.Logics.Players
import model.logic.{CactusTurnPhase, TurnPhase}
import model.player.Players.CactusPlayer
import view.module.cactus.CactusViewModule

/** Represents the controller module for the Cactus game. */
object CactusControllerModule extends ControllerModule:
  override type ControllerType = CactusController
  override type Requirements   = CactusModelModule.Provider with CactusViewModule.Provider

  trait CactusController extends Controller:
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
     * @param cardIndex index of the card in the player hand to handle.
     */
    def handlePlayerInput(cardIndex: Int): Unit

    /**
     * Makes player to discard a card.
     *
     * @param cardIndex index of the card in the player hand to discard.
     */
    def discard(cardIndex: Int): Unit

    /**
     * Makes player to discard a card but with a malus if the card does not match the discard criteria.
     *
     * @param cardIndex index of the card in the player hand to discard.
     */
    def discardWithMalus(cardIndex: Int): Unit

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

  /** Represents the controller component for the Cactus game. */
  trait Component:
    context: Requirements =>

    /** Represents the controller for the Cactus game. */
    class CactusControllerImpl extends CactusController:

      override def continue(): Unit =
        context.model.continue()
        context.view.updateViewTurnPhase()

      override def draw(fromDeck: Boolean): Unit =
        context.model.draw(fromDeck)
        context.view.updateViewTurnPhase()

      override def handlePlayerInput(cardIndex: Int): Unit =
        context.model.currentPhase match
          case CactusTurnPhase.Discard => context.model.discard(cardIndex)
          case CactusTurnPhase.DiscardEquals =>
            context.model.discardWithMalus(
              cardIndex,
              context.model.players(0) match
                case player: CactusPlayer => player
            )
          case _ => ()
        context.view.updateViewTurnPhase()

      override def discard(cardIndex: Int): Unit =
        context.model.discard(cardIndex)
        context.view.updateViewTurnPhase()

      override def callCactus(): Unit =
        context.model.callCactus()
        context.view.updateViewTurnPhase()

      override def players: Players = context.model.players
      
      override def pilesHead: Option[PokerCard & Coverable] = context.model.game.discardPile.cards.headOption

      override def discardWithMalus(cardIndex: Int): Unit = context.model.discardWithMalus(cardIndex)

      override def currentPhase: TurnPhase = context.model.currentPhase

  /** Interface of the controller module of Cactus game. */
  trait Interface extends Provider with Component:
    self: Requirements =>
