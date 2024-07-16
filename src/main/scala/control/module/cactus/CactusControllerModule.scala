package control.module.cactus

import control.module.ControllerModule
import model.module.cactus.CactusModelModule
import view.module.ViewModule

/** Represents the controller module for the Cactus game. */
object CactusControllerModule extends ControllerModule:
  override type Requirements = CactusModelModule.Provider //with ViewModule.Provider

  /** Represents the controller component for the Cactus game. */
  trait Component:
    context: Requirements =>

    /** Represents the controller for the Cactus game. */
    class CactusController extends Controller:

      /** Continue to the next step. */
      def continue(): Unit = context.model.continue()

      /**
       * Make player to draw a card from the deck or the discard pile.
       *
       * @param fromDeck if `true` the card is drawn from the deck, if `false` it is drawn from the discard pile.
       */
      def draw(fromDeck: Boolean): Unit = context.model.draw(fromDeck)

      /**
       * Make player to discard a card.
       *
       * @param cardIndex index of the card in the player hand to discard.
       */
      def discard(cardIndex: Int): Unit = context.model.discard(cardIndex)

      /**
       * Make player to discard a card but with a malus if the card does not match the discard criteria.
       *
       * @param cardIndex index of the card in the player hand to discard.
       */
      def discardWithMalus(cardIndex: Int): Unit = context.model.discardWithMalus(cardIndex)

  /** Interface of the controller module of Cactus game. */
  trait Interface extends Provider with Component:
    self: Requirements =>