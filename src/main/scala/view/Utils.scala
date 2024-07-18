package view

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
  def colorToRgb(color: Color): String =
    val red   = (color.red * 255).toInt
    val green = (color.green * 255).toInt
    val blue  = (color.blue * 255).toInt
    val alpha = color.opacity

    if (alpha == 1.0)
      s"rgb($red, $green, $blue)"
    else
      s"rgba($red, $green, $blue, $alpha%.2f)"
