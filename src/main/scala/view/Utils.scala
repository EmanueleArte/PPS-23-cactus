package view

import scalafx.scene.paint.Color


object Utils:

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
