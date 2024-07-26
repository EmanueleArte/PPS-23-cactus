package view

import scalafx.scene.control.ComboBox
import scalafx.scene.paint.Color

/** Contains utility methods for the view. */
object Utils:
  val topPosition: Int = 0
  val leftPosition: Int = 0
  val topLeftCorner: ViewPosition = ViewPosition(topPosition, leftPosition)

  /**
   * Converts a color to its RGB representation.
   * @param color color to convert.
   * @return string representation of the color.
   */
  extension (color: Color)
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
