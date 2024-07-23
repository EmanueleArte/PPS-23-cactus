package model.logic

/** Phases of a game turn. */
trait TurnPhase

/** Phases of a base game turn. */
enum BaseTurnPhase extends TurnPhase:
  case Start, End

/** Phases of a cactus game turn. */
enum CactusTurnPhase extends TurnPhase:
  case Draw, Discard, EffectActivation, EffectResolution, DiscardEquals

/** Represents a game with turn phases. */
trait GameWithTurnPhases:
  protected var _currentPhase: TurnPhase = BaseTurnPhase.Start

  /**
   * Getter for the current phase.
   *
   * @return the current phase of the game.
   */
  def currentPhase: TurnPhase = _currentPhase

  /**
   * Setter for the current phase.
   *
   * @param phase the new phase to set.
   */
  def currentPhase_=(phase: TurnPhase): Unit = _currentPhase = phase
