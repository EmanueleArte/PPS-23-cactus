package view.module

/** Represents the view module. */
trait ViewModule:
  /** Type representing the view of the game. */
  type ViewType <: View

  /** View of the game. */
  trait View:
    /** Shows the view. */
    def show(): Unit

  /** Provider for a [[View]]. */
  trait Provider:
    val view: ViewType

  /** Dependencies for the view. */
  type Requirements
