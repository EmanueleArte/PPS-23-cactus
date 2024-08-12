package model.bot

object CactusBotsData:

  /** Represents the type of draw methods of a cactus bot. */
  enum DrawMethods:
    case Deck, Pile, RandomDeck, PileSmartly

  /** Represents the type of discard methods of a cactus bot. */
  enum DiscardMethods:
    case Unknown, Known, Random

  /** Represents the type of memories of a cactus bot. */
  enum Memory(val lossPercentage: Double):
    require(lossPercentage <= 1)
    require(lossPercentage >= 0)

    case Bad      extends Memory(0.8)
    case Normal   extends Memory(0.5)
    case Good     extends Memory(0.25)
    case VeryGood extends Memory(0.1)
    case Optimal  extends Memory(0)
