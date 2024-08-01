package model.game

import model.bot.BotBuilder.CactusBotDSL.{discarding, drawing, withMemory}
import model.bot.Bots.{BotParamsType, CactusBotImpl}
import model.bot.CactusBotsData.{DiscardMethods, DrawMethods, Memory}
import model.card.Cards.{Card, Coverable, PokerCard}
import model.card.CardsData.{PokerCardName, PokerSuit}
import model.deck.Decks.{Deck, PokerDeck}
import model.deck.Piles.{DiscardPile, PokerPile}
import model.logic.Logics.Players
import model.player.Players.{CactusPlayer, Player}

/**
 * An opaque type representing the scores of players in a game.
 * Internally it is a [[ Map[Player, Int] ]].
 */
opaque type Scores = Map[Player, Int]

/**
 * Companion object of [[Scores]] opaque type.
 * Provides methods to interact with `Scores` values.
 */
object Scores:
  /**
   * Creates a [[Scores]] from a [[ Map[Player, Int] ]].
   *
   * @param map the map of players and their scores.
   * @return t [[Scores]] representing the provided map.
   */
  def apply(map: Map[Player, Int]): Scores = map

  /**
   * Converts a [[Scores]] to a Score.
   *
   * @param scores the [[Scores]] to convert.
   * @return the underlying [[Scores]] of the provided [[Scores]].
   */
  def toMap(scores: Scores): Map[Player, Int] = scores

  extension (scores: Scores)

    /**
     * Returns the size of the provided [[Scores]].
     *
     * @return the number of player-score pairs in the [[Scores]].
     */
    def size: Int = scores.size

    /**
     * Retrieves the score associated with a specific player in this [[Scores]].
     *
     * @param player The player whose score is to be retrieved.
     * @return an [[ Option[Int] ]] containing the score of the player if present, or `None` if the player is not in this [[Scores]].
     */
    def get(player: Player): Option[Int] = scores.get(player)

    /**
     * Retrieves the players contained in the [[Scores]].
     *
     * @return an [[ Iterable[Player] ]] containing the keys of the Score associated to [[Scores]].
     */
    def players: Iterable[Player] = scores.keys

    /**
     * Checks if the provided [[Scores]] is empty.
     *
     * @return `true` if the [[Scores]] is empty, `false` otherwise.
     */
    def isEmpty: Boolean = scores.isEmpty

/** Generic card game. */
trait Game:
  /**
   * Setup method to call before start the game with pre-defined bot params.
   * @param playersNumber number of players in the match.
   * @return a list with the initialized players.
   */
  def setupGame(playersNumber: Int): Players

  /**
   * Setups method to call before start the game with custom bots' params.
   * @param botsParams parameters to setup the bots.
   * @return a list with the initialized players.
   */
  def setupGameWithBots(botsParams: BotParamsType): Players

  /**
   * Calculate the scores for each player.
   * @param players list of players to which calculate the scores.
   * @return a [[Scores]] with the scores for each player.
   */
  def calculateScores(players: List[Player]): Scores

/** Cactus game implementation. */
class CactusGame() extends Game:
  /** Deck with the cards to draw. */
  val deck: Deck[PokerCard & Coverable] = PokerDeck(shuffled = true)

  /** Pile with the discarded cards. */
  var discardPile: PokerPile        = PokerPile()
  val initialPlayerCardsNumber: Int = 4

  export deck.{size => deckSize}

  override def setupGame(playersNumber: Int): List[Player] =
    CactusPlayer("Player", (1 to initialPlayerCardsNumber).toList.map(_ => deck.draw().get)) +:
      (1 until playersNumber).toList
        .map(index =>
          CactusBotImpl(
            s"Bot-$index",
            (1 to initialPlayerCardsNumber).toList.map(_ => deck.draw().get),
            DrawMethods.Deck,
            DiscardMethods.Random,
            Memory.Normal
          )
        )

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def setupGameWithBots(botsParams: BotParamsType): List[Player] =
    val (drawings, discardings, memories) =
      botsParams.asInstanceOf[(Seq[DrawMethods], Seq[DiscardMethods], Seq[Memory])]
    (CactusPlayer("Player", List.empty) :: drawings
      .lazyZip(discardings)
      .lazyZip(memories)
      .zipWithIndex
      .map { case ((drawMethod, discardMethod, memory), i) =>
        s"Bot ${i + 1}" drawing drawMethod discarding discardMethod withMemory memory
      }
      .toList)
      .map(p =>
        (1 to initialPlayerCardsNumber).foreach(_ => p.draw(deck))
        p.cards.foreach(_.cover())
        p
      )

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  private def isRedKing(c: PokerCard): Boolean = c.value match
    case PokerCardName.King => c.suit == PokerSuit.Hearts || c.suit == PokerSuit.Diamonds
    case _                  => false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def calculateScores(players: List[Player]): Scores = Scores(
    players.zipWithIndex
      .map((player, index) => (player, player.cards))
      .filter((player, cards) =>
        cards.count {
          case card: PokerCard => true
          case _               => false
        } == cards.size
      )
      .map((player, cards) =>
        (player, cards.collect(c => PokerCard(c.value.asInstanceOf[Int], c.suit.asInstanceOf[PokerSuit])))
      )
      .map((player, cards) =>
        (
          player,
          cards.collect {
            case card if isRedKing(card) => 0
            case card                    => card.value
          }.sum
        )
      )
      .map((player, score) => player -> score)
      .toMap
  )

  /**
   * Check if the last discarded card has an effect on the game.
   *
   * @return the effect related of the last discarded card.
   */
  def checkCardEffect(): CardEffect =
    discardPile.draw() match
      case Some(card) =>
        discardPile = discardPile.put(card)
        card.value match
          case PokerCardName.Ace  => CactusCardEffect.AceEffect
          case PokerCardName.Jack => CactusCardEffect.JackEffect
          case _                  => CactusCardEffect.NoEffect
      case _ => CactusCardEffect.NoEffect
