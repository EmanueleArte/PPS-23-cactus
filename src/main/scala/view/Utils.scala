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
        s"rgba($red, $green, $blue, $alpha%.2f)"


//  object A:
//    extension (b: Int)
//      def foo(a: Int): Unit = println("Int")
//
//
//  object B:
//    extension (b: Double)
//      def foo(a: Int): Unit = println("Double")
//
//  object C:
//    import A.foo as fooA
//    import B.foo as fooB
//
//    extension (b: Any)
//      def foo(a: Int): Unit = b match
//        case x: Int => x fooA a
//        case x: Double => x fooB a
//
//  import C.*
//
//  def test: Unit =
//    println("TEST")
//    0.0 foo 1
//    0 foo 1
