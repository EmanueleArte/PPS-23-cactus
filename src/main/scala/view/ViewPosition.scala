package view

/**
 * Position of an element of the view.
 * Is similar to an algebraic vector, with the only difference that the y axe is reverted.
 */
trait ViewPosition:
  /**
   * X coordinate of the position.
   * @return the x coordinate.
   */
  def x: Int

  /**
   * Y coordinate of the position.
   * @return the y coordinate.
   */
  def y: Int
private final case class ViewPositionImpl(x: Int, y: Int) extends ViewPosition

/** Companion object of [[ViewPosition]] */
object ViewPosition:
  /**
   * Creates a new [[ViewPosition]].
   * @param x coordinate.
   * @param y coordinate.
   * @return a new [[ViewPosition]]
   */
  def apply(x: Int, y: Int): ViewPosition = ViewPositionImpl(x, y)

  extension (position: ViewPosition)
    /**
     * `Division` operation for [[ViewPosition]].
     * Returns a new [[ViewPosition]] with the coordinates divided by `divisor`.
     * @param divisor the divisor of the division.
     * @return new [[ViewPosition]] with the coordinates divided by `divisor`.
     */
    def /(divisor: Int): ViewPosition =
      require(divisor > 0)
      ViewPosition(Math.floorDiv(position.x, divisor), Math.floorDiv(position.y, divisor))

    /**
     * `Subtract` operation for [[ViewPosition]].
     * Returns a new position, where each coordinate is the result of the subtraction between the parameters.
     * @param position2 from which subtract.
     * @return new [[ViewPosition]].
     */
    def -(position2: ViewPosition): ViewPosition = ViewPosition(position.x - position2.x, position.y - position2.y)
