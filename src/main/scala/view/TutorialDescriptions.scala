package view

import mvc.PlayableGame

/** Contains the tutorials of the playable games. */
object TutorialDescriptions:
  /** Enum with the game rules. */
  enum GameTutorialValue:
    case Name, Description, Rules, Points

  /** Map with the tutorial for each game. */
  val gamesDescriptions: Map[PlayableGame, Map[GameTutorialValue, String]] =
    Map[PlayableGame, Map[GameTutorialValue, String]](
      PlayableGame.Cactus -> Map[GameTutorialValue, String](
        GameTutorialValue.Name -> (PlayableGame.Cactus.name + " Tutorial"),
        GameTutorialValue.Description -> ("Cactus is a card game based on memory. The goal is to have less points in your hand than the other players.\n" +
          "The game is played with a Poker deck. The game ends when anyone has done another turn after the call of \"Cactus\" made by a player."),
        GameTutorialValue.Rules -> ("The game is played in turns, where each player has to draw a card from the deck or the discard pile, watch it and then discard one from his hand. " +
          "Some cards when discarded in the latter phase activate special effects. \nAfter the previous discarding and the, eventual, effect resolution there is a phase where all the players" +
          "can discard a card with the same value of the one on top of the discard pile. \nIf they don't want to, they can proceed with the game.\n" +
          "Before the end of his turn the player can call \"Cactus\".\n" +
          "At the start of the first turn each player has to watch two cards in his hand and remember them.\n" +
          "Every card added to a player's hand is placed as last item of the hand.\n" +
          "Some cards have special effects: " +
          "\n- Aces, when discarded, allows the player to choose another player that has to draw a card from the deck without seeing it." +
          "\n- Jacks, when discarded, allows the player to see a card in his hand."),
        GameTutorialValue.Points -> ("The points are calculated as follows: " +
          "\n- Number cards have their value as points (e.g. Ace -> 1 point, Two of Spades -> 2 points, ...)." +
          "\n- Jacks are 11 points." +
          "\n- Queens are 12 points." +
          "\n- Kings are 13 points." +
          "\n- Red Kings are 0 points.")
      )
    )
