package view

import model.logic.{BaseTurnPhase, CactusTurnPhase, TurnPhase}
import scalafx.scene.control.ComboBox
import scalafx.scene.paint.Color

/** Contains utility methods for the view. */
object Utils:
  val topPosition: Int = 0
  val leftPosition: Int = 0
  val topLeftCorner: ViewPosition = ViewPosition(topPosition, leftPosition)

  case class Phase(name: String, description: String)

  val turnPhaseDescription: Map[TurnPhase, Phase] = Map[TurnPhase, Phase](
    BaseTurnPhase.Start -> Phase("Watch cards", "Click on 2 cards to see them."),
    CactusTurnPhase.Draw -> Phase("Draw", "Draw one card from the deck or from the discard pile, by clicking on it."),
    CactusTurnPhase.Discard -> Phase("Discard", "Choose a card from your hand to discard."),
    CactusTurnPhase.DiscardEquals -> Phase("Discard equals cards", "You can discard a card equals to the one on top of the discard pile."),
    CactusTurnPhase.AceEffect -> Phase("Ace discarded", "Choose a player and have them draw a card from the deck."),
    CactusTurnPhase.EffectActivation -> Phase("Effect activation", "Activate the effect of the card you just discarded."),
    CactusTurnPhase.CallCactus -> Phase("Cactus", "Call \"Cactus\" and end the game."),
    BaseTurnPhase.End -> Phase("End", "The end of the turn.")
  )

  extension (color: Color)
    /**
     * Converts a color to its RGB representation.
     * @return string representation of the color.
     */
    def toRgbString: String =
      val red = (color.red * 255).toInt
      val green = (color.green * 255).toInt
      val blue = (color.blue * 255).toInt
      val alpha = color.opacity

      if (alpha == 1.0)
        s"rgb($red, $green, $blue)"
      else
        s"rgba($red, $green, $blue, $alpha)"


  /**
   * Gets the value of the combo box.
   *
   * @param comboBox the combo box.
   * @return the value of the combo box.
   */
  def value[A](comboBox: ComboBox[A]): A = comboBox.value.value
