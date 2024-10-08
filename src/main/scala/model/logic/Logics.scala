package model.logic

import model.bot.Bots.{BotParamsType, CactusBot}
import model.card.Cards.PokerCard
import model.game.{CactusCardEffect, CactusGame, Game, Scores}
import model.player.Players.{CactusPlayer, Player}
import model.utils.Iterators.PeekableIterator
import scala.annotation.tailrec

/** Logic of a game. */
object Logics:
  /** Type alias for a list of [[Player]]. */
  type Players = List[Player]

  /** Logic of a generic turn based game. */
  trait Logic:
    /** Type of the score of the game. */
    type Score

    /** Type of a player. */
    type PlayerType <: Player

    protected val _players: Players = List.empty[PlayerType]

    /**
     * Iterator of the players in the game.
     *
     * @return an iterator of the players in the game.
     */
    private val playerIterator: PeekableIterator[Player] = PeekableIterator(Iterator.continually(_players).flatten)

    /**
     * Getter for the list of players in the game.
     *
     * @return the list of players in the game.
     */
    def players: Players = _players

    /**
     * Getter for a player at a specific index.
     *
     * @param index index of the player in the list.
     * @return the player at the specified index.
     */
    def getPlayer(index: Int): PlayerType

    /**
     * Getter for the current player.
     *
     * @return the current player.
     */
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    def currentPlayer: PlayerType = playerIterator.peek.get.asInstanceOf[PlayerType]

    /**
     * Switch to the next player.
     *
     * @return the next player.
     */
    def nextPlayer: Player = playerIterator.next()

    /** Continue to the next step performing all necessary actions. */
    def continue(): Unit

    /**
     * Check if the game is over.
     *
     * @return `true` if the game is over, `false` otherwise.
     */
    def isGameOver: Boolean

    /**
     * Calculate the score of the game.
     *
     * @return a map with the [[Player]] and the [[Score]].
     */
    def calculateScore: Scores

    /**
     * Lets the player see a card in his hand.
     *
     * @param cardIndex index of the card in the player hand to see.
     */
    def seeCard(cardIndex: Int): Unit

    /**
     * Getter for the human player.
     *
     * @return the human player.
     */
    def humanPlayer: PlayerType

    /** Does the things to do after the game is over. */
    def handleGameOver(): Unit

  /** Provider of a [[Game]]. */
  trait GameProvider:
    /** Instance of the game to play. */
    lazy val game: Game

  /** Trait that represents a game logic based on a certain game. */
  trait GameLogic extends GameProvider:
    /**
     * Setup the game.
     *
     * @param nPlayers number of players in the game.
     * @return the list of players obtained after the setup.
     */
    def setup(nPlayers: Int): Players = game.setupGame(nPlayers)

    /**
     * Setup the game with bots.
     *
     * @param botsParams parameters to setup the bots.
     * @return the list of players obtained after the setup.
     */
    def setupWithBots(botsParams: BotParamsType): Players = game.setupGameWithBots(botsParams)

  /**
   * Logic of the Cactus game.
   *
   * @param playersInput can be either number of players or the collection of players in the game.
   */
  class CactusLogic(playersInput: Either[Int, BotParamsType]) extends Logic with GameLogic with GameWithTurnPhases:
    override type Score      = Int
    override type PlayerType = CactusPlayer

    override lazy val game: CactusGame = CactusGame()
    override val _players: Players = playersInput match
      case Left(nPlayers) => setup(nPlayers)
      case Right(players) => setupWithBots(players)
    private var turnsRemaining: Int = playersInput match
      case Left(nPlayers) => nPlayers
      case _              => players.length
    private var lastRound: Boolean = false

    override def getPlayer(index: Int): PlayerType = players(index) match
      case p: PlayerType => p

    override def humanPlayer: PlayerType = players.headOption.get match
      case p: PlayerType => p

    @tailrec
    final override def continue(): Unit = currentPhase match
      case CactusTurnPhase.EffectActivation =>
        handleCardEffect()
      case CactusTurnPhase.AceEffect =>
        currentPhase_=(CactusTurnPhase.DiscardEquals)
      case CactusTurnPhase.DiscardEquals =>
        currentPlayer.cards.foreach(_.cover())
        if isCactusAlreadyCalled then currentPhase_=(BaseTurnPhase.End) else currentPhase_=(CactusTurnPhase.CallCactus)
        if isBot(currentPlayer) then continue()
      case CactusTurnPhase.JackEffect =>
        if isBot(currentPlayer) then botTurn()
      case CactusTurnPhase.CallCactus =>
        if isBot(currentPlayer) then botTurn()
        else currentPhase_=(BaseTurnPhase.End)
      case BaseTurnPhase.End =>
        if isGameOver then handleGameOver()
        else
          currentPhase_=(CactusTurnPhase.DiscardEquals)
          iterateBots(botDiscardWithMalus)
          nextPlayer
          currentPhase_=(CactusTurnPhase.Draw)
          if isBot(currentPlayer) then botTurn()
      case _ => ()

    override def isGameOver: Boolean =
      lastRound && getNextPlayer.calledCactus && currentPhase == BaseTurnPhase.End

    override def calculateScore: Scores = game.calculateScores(players)

    /**
     * Make the current player to draw a card from the deck or the discard pile.
     *
     * @param fromDeck if `true` the card is drawn from the deck, if `false` it is drawn from the discard pile.
     */
    def draw(fromDeck: Boolean): Unit = currentPhase match
      case CactusTurnPhase.Draw =>
        currentPlayer.cards.foreach(_.cover())
        if fromDeck then currentPlayer.draw(game.deck)
        else currentPlayer.draw(game.discardPile)
        currentPhase_=(CactusTurnPhase.Discard)
      case _ => ()

    private def getNextPlayer: CactusPlayer =
      players
        .sliding(2)
        .find(_.headOption.get.isEqualTo(currentPlayer))
        .flatMap(_.lift(1))
        .orElse(if (players.lastOption.contains(currentPlayer)) players.headOption else None)
        .get match
        case p: CactusPlayer => p

    /**
     * Make the current player to discard a card and choose if the eventual effect should be activated.
     *
     * @param cardIndex index of the card in the player hand to discard.
     * @param withEffect if `true` the eventual effect of the card is activated, if `false` the effect is not activated.
     * @param player to which make discard a card.
     */
    private def discard(cardIndex: Int, withEffect: Boolean)(player: CactusPlayer): Unit = currentPhase match
      case CactusTurnPhase.Discard =>
        val discardedCard = player.discard(cardIndex)
        discardedCard.uncover()
        game.discardPile = game.discardPile.put(discardedCard)
        if withEffect then
          currentPhase_=(CactusTurnPhase.EffectActivation)
          continue()
        else currentPhase_=(CactusTurnPhase.DiscardEquals)
      case _ => ()

    /**
     * Make the current player to discard a card and activate the eventual effect.
     *
     * @param cardIndex  index of the card in the player hand to discard.
     */
    private def discard(cardIndex: Int): Unit = discard(cardIndex, true)(currentPlayer)

    /**
     * Make the current player to discard a card but with a malus if the card does not match the discard criteria.
     *
     * @param cardIndex index of the card in the player hand to discard.
     * @param player player that has to discard the card. Default is the current player.
     */
    @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
    def discardWithMalus(cardIndex: Int, player: CactusPlayer = currentPlayer): Unit =
      currentPhase match
        case CactusTurnPhase.DiscardEquals =>
          game.discardPile.draw() match
            case Some(card) if card.value != player.cards(cardIndex).value =>
              player.draw(game.deck)
              player.cards.lastOption match
                case Some(card) => card.cover()
                case _          => ()
              game.discardPile = game.discardPile.put(card)
            case Some(card) =>
              game.discardPile = game.discardPile.put(card)
              currentPhase_=(CactusTurnPhase.Discard)
              discard(cardIndex, false)(player)
            case _ => player.draw(game.deck)
        case _ => ()

    /** Make the current player to call Cactus. */
    def callCactus(): Unit = currentPhase match
      case CactusTurnPhase.CallCactus =>
        if !lastRound then
          lastRound = true
          currentPlayer.cards.foreach(_.uncover())
          currentPlayer.callCactus()
        currentPhase_=(BaseTurnPhase.End)
      case _ => ()

    override def seeCard(cardIndex: Int): Unit =
      require(cardIndex >= 0)
      require(cardIndex < currentPlayer.cards.size)
      if currentPlayer.cards.count(!_.isCovered) < game.cardsSeenAtStart then currentPlayer.cards(cardIndex).uncover()
      if currentPlayer.cards.count(!_.isCovered) == game.cardsSeenAtStart then _currentPhase = CactusTurnPhase.Draw

    override def handleGameOver(): Unit =
      players.foreach(_.cards.foreach(_.uncover()))
      currentPhase_=(CactusTurnPhase.GameOver)

    /**
     * Handles the player input according to the turn phase.
     *
     * @param index index of the card in the player hand or index of the player in the table.
     */
    def movesHandler(index: Int): Unit = currentPhase match
      case BaseTurnPhase.Start     => seeCard(index)
      case CactusTurnPhase.Discard => discard(index)
      case CactusTurnPhase.DiscardEquals =>
        if !humanPlayer.calledCactus then
          discardWithMalus(
            index,
            getPlayer(0)
          )
      case CactusTurnPhase.AceEffect =>
        val target = getPlayer(index)
        if !target.calledCactus && !target.isEqualTo(currentPlayer) then resolveAceEffect(target)
      case CactusTurnPhase.JackEffect =>
        resolveHumanPlayerJackEffect(index)
      case _ => ()

    /**
     * Resolves the effect of an ace discard.
     *
     * @param player the player that the effect is applied to.
     */
    private def resolveAceEffect(player: PlayerType): Unit =
      player.drawCovered(game.deck)
      currentPhase_=(CactusTurnPhase.DiscardEquals)

    /**
     * Resolves the effect of a jack discard.
     *
     * @param index the card index that the effect is applied to.
     */
    private def resolveHumanPlayerJackEffect(index: Int): Unit = currentPlayer match
      case currentPlayer: CactusBot =>
        throw new UnsupportedOperationException("Can't resolve jack effect of a bot as a human player.")
      case _ =>
        currentPlayer.cards(index).uncover()
        currentPhase_=(CactusTurnPhase.DiscardEquals)

    @tailrec
    private def botTurn(): Unit = currentPlayer match
      case bot: CactusBot =>
        currentPhase match
          case CactusTurnPhase.Draw =>
            draw(bot.chooseDraw(game.discardPile))
            bot.seeCard(bot.cards.size - 1)
            botTurn()
          case CactusTurnPhase.Discard =>
            discard(bot.chooseDiscard())
            botTurn()
          case CactusTurnPhase.AceEffect =>
            bot.choosePlayer(players.zipWithIndex.map((_, i) => getPlayer(i))) match
              case Some(p) =>
                resolveAceEffect(p)
              case _ => ()
            botTurn()
          case CactusTurnPhase.JackEffect =>
            bot.applyJackEffect()
            currentPhase_=(CactusTurnPhase.DiscardEquals)
            botTurn()
          case CactusTurnPhase.DiscardEquals => ()
          case CactusTurnPhase.CallCactus =>
            if !isCactusAlreadyCalled && bot.shouldCallCactus() then callCactus()
            else
              currentPhase_=(BaseTurnPhase.End)
              continue()
          case _ => continue()
      case _ => ()

    private def isCactusAlreadyCalled: Boolean =
      players.exists(p =>
        p match
          case p: CactusPlayer => p.calledCactus
          case _               => false
      )

    private def iterateBots(f: CactusBot => Unit): Unit =
      (1 to players.length).foreach(_ =>
        currentPlayer match
          case bot: CactusBot => f(bot)
          case _              =>
        nextPlayer
      )

    private def isBot(player: Player): Boolean = player match
      case _: CactusBot => true
      case _            => false

    @tailrec
    private def botDiscardWithMalus(bot: CactusBot): Unit = bot match
      case bot: CactusPlayer if !bot.calledCactus =>
        bot.chooseDiscardWithMalus(game.discardPile) match
          case Some(i) =>
            discardWithMalus(i)
            botDiscardWithMalus(bot)
          case _ => ()
      case _ => ()

    private def handleCardEffect(): Unit =
      game.checkCardEffect() match
        case CactusCardEffect.AceEffect  => currentPhase_=(CactusTurnPhase.AceEffect)
        case CactusCardEffect.JackEffect => currentPhase_=(CactusTurnPhase.JackEffect)
        case _                           => currentPhase_=(CactusTurnPhase.DiscardEquals)

  /** Companion object for [[CactusLogic]]. */
  object CactusLogic:
    /**
     * Factory method for [[CactusLogic]].
     *
     * @param nPlayers number of players in the game.
     * @return a new instance of [[CactusLogic]].
     */
    def apply(nPlayers: Int): CactusLogic = new CactusLogic(Left(nPlayers): Either[Int, BotParamsType])

    /**
     * Factory method for [[CactusLogic]].
     *
     * @param botsParams parameters of the bots in the game.
     * @return a new instance of [[CactusLogic]].
     */
    def apply(botsParams: BotParamsType): CactusLogic = new CactusLogic(Right(botsParams): Either[Int, BotParamsType])
