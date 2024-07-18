package view

import scalafx.scene.layout.Pane

/** Contains the panes that can be used. */
object Panes:
  /** Basic interface for a pane used in a ScalaFX application. */
  trait ScalaFXPane:
    /**
     * Width of the [[Pane]].
     * @return width of the pane.
     */
    def paneWidth: Int

    /**
     * Height of the [[Pane]].
     * @return height of the pane.
     */
    def paneHeight: Int

    /**
     * Position of the [[Pane]] in the container. The position refers to the top-left corner of the pane.
     * If the pane is contained in an element, the position is relative to that element.
     * @return position of the pane in the container.
     */
    def position: ViewPosition

    /**
     * Returns the [[Pane]] represented by the implementation.
     * @return the [[Pane]] object.
     */
    def pane: Pane
