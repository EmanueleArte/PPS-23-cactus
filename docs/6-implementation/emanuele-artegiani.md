# Emanuele Artegiani

Per quanto riguarda l’implementazione del sistema, io mi sono occupato principalmente di:
- creazione delle carte da gioco.
- creazione della logica di gioco, che si occupa dell'interazione tra i giocatori e le regole del gioco.
- gestione dell’effetto speciale riguardante le carte _Asso_ sia lato model sia lato view.
- creazione del menù iniziale.
- creazione di un gestore per le schermate di gioco.
- strutturare l'MVC dell'applicativo.

Inoltre ho contribuito alla creazione di un DSL per la gestione della view in ScalaFX e all'implementazione di alcuni 
metodi dei bot e del gioco vero e proprio; oltre ad altre varie piccole aggiunte e modifiche nel resto del codice del progetto.

I pattern che ho utilizzato sono (tra parentesi dove sono stati utilizzati):
- _Singleton_ (`ScalaFXStageManager`) 
- _Cake_ (nei vari `MVC`) 
- _Delegation_ (in `CactusLogic` e `GameLogic` usando `Game` e `Player`/`CactusPlayer`)
- _Type classes_ (`PeekableIterator`, `GenericCard`)
- _State_ (`CactusLogic`)

Di seguito riporto le parti più interessanti del codice da me realizzato.

## Carte da gioco

Le uniche carte da gioco implementate sono le carte da poker, che utilizzano il seguenti `enum`:
```scala
trait Suit

enum PokerSuit extends Suit:
  case Spades, Diamonds, Clubs, Hearts

object PokerCardName extends Enumeration:
  val Ace   = 1
  val Jack  = 11
  val Queen = 12
  val King  = 13
```

Le carte da poker sono rappresentate da un `case class PokerCard` che estende il `trait Card`:
```scala
case class PokerCard(value: Int, suit: PokerSuit) extends Card:
  override type Value = Int
  require(value >= 1, "Card value cannot be less than 1")
  require(value <= 13, "Card value cannot be greater than 13")
```

Visto che nei giochi di carte è spesso necessario coprire o scoprire queste ultime, è stato creato un `trait Coverable` 
che permette di applicare questa proprietà utilizzando la tecnica del _mixin_:
```scala
trait Coverable:
  private var _covered = true

  def isCovered: Boolean = _covered
  def cover(): Unit = _covered = true
  def uncover(): Unit = _covered = false
```

### DSL per le carte da poker

Grazie al codice mostrato sopra, è possibile implementare facilmente un DSL per la creazione delle carte da poker, che
permette di creare semplici carte oppure copribili e scopribili:
```scala
object PokerDSL:
  extension (value: Int)
    def of(suit: PokerSuit): PokerCard = PokerCard(value, suit)
    def OF(suit: PokerSuit): PokerCard & Coverable = new PokerCard(value, suit) with Coverable
```

## Logica di gioco

Una logica si occupa principalmente di gestire i turni dei giocatori in base alle regole del gioco.
Infatti, nel `trait Logic` è presente un iteratore circolare che permette di scorrere i giocatori in modo continuativo.
Di seguito un estratto del codice di `Logic` e il codice dell'iteratore custom creato per poter ottenere il giocatore corrente:
```scala
trait Logic:
  protected val _players: Players = List.empty[PlayerType]
  private val playerIterator: PeekableIterator[Player] = PeekableIterator(Iterator.continually(_players).flatten)
  
  def currentPlayer: PlayerType = playerIterator.peek.get.asInstanceOf[PlayerType] 
  def nextPlayer: Player = playerIterator.next()
```

```scala
class PeekableIterator[A](iterator: Iterator[A]) extends Iterator[A]:
  private var lookahead: Option[A] = None

  def peek: Option[A] =
    if (lookahead.isEmpty)
      if (iterator.hasNext)
        lookahead = Some(iterator.next())
    lookahead

  override def hasNext: Boolean = lookahead.isDefined || iterator.hasNext
  override def next(): A =
    val nextElement = peek.fold(throw new NoSuchElementException("next on empty iterator"))(identity)
    lookahead = None
    nextElement
```

### Logica di Cactus

La logica di Cactus è stata implementata in modo tale che possa essere instanziata sia passando il numero di giocatori sia
passando i parametri dei bot. Questo è stato possibile grazie all'utilizzo di `Either`, di seguito un estratto del codice:
```scala
class CactusLogic(playersInput: Either[Int, BotParamsType]) extends Logic with GameLogic with GameWithTurnPhases:

    override lazy val game: CactusGame = CactusGame()
    override val _players: Players = playersInput match
      case Left(nPlayers) => setup(nPlayers)
      case Right(players) => setupWithBots(players)
```

Per semplificarne l'istanziazione è stato creato un _Companion Object_ che permette di creare una nuova istanza di `CactusLogic`:
```scala
object CactusLogic:
  def apply(nPlayers: Int): CactusLogic = new CactusLogic(Left(nPlayers): Either[Int, BotParamsType])
  def apply(botsParams: BotParamsType): CactusLogic = new CactusLogic(Right(botsParams): Either[Int, BotParamsType])
```

### Esecuzione mossa da parte dei bot

Le mosse di un giocatore sono eseguite seguendo le regole del gioco in esecuzione e non sono particolarmente 
interessanti a livello di codice, essendo principalmente composte da condizioni relative alla fase di gioco attuale e alla 
mano del giocatore.

In certi momenti risulta necessario che tutti i bot effuttuino una mossa, ad esempio quando in Cactus un giocatore ha 
scartato una carta, nella relativa fase, e quindi gli altri giocatori hanno la possibilità di scartare carte dello stesso 
valore tutti contemporaneamente. 

Per fare ciò è stato implementato il seguente metodo generico che permette di iterare su tutti i bot presenti nel gioco 
e di eseguire una determinata azione su di essi:
```scala
def iterateBots(f: CactusBot => Unit): Unit =
  (1 to players.length).foreach(_ =>
    currentPlayer match
      case bot: CactusBot => f(bot)
      case _              =>
    nextPlayer
  )
```

Il seguente metodo è stato utilizzato per eseguire la mossa di scarto con malus da parte di un bot e può essere passato
come argomento al metodo `iterateBots`:
```scala

@tailrec
private def botDiscardWithMalus(bot: CactusBot): Unit = bot match
  case bot: CactusPlayer if !bot.calledCactus =>
    bot.chooseDiscardWithMalus(game.discardPile) match
      case Some(i) =>
        discardWithMalus(i)
        botDiscardWithMalus(bot)
      case _ => ()
  case _ => ()
```

## Menù iniziale

Il menù iniziale permette la selezione del:
- gioco da giocare.
- numero di giocatori.
- parametri dei bot.

Tutti questi parametri verranno processati da un `Game` per ottenere la lista di `Player` da utilizzare nel gioco:
```scala
override def setupGameWithBots(botsParams: BotParamsType): List[Player] =
  val (drawings, discardings, memories) =
    botsParams match
  case (drawings: Seq[DrawMethods], discardings: Seq[DiscardMethods], memories: Seq[Memory]) =>
    (drawings, discardings, memories)
  case _ => throw new IllegalArgumentException("Invalid bot params type")

  val player: CactusPlayer = CactusPlayer("Player", List.empty)
  setupCards(player)
  val bots: List[CactusBotImpl] = drawings
    .lazyZip(discardings)
    .lazyZip(memories)
    .zipWithIndex
    .map { case ((drawMethod, discardMethod, memory), i) =>
      s"Bot ${i + 1}" drawing drawMethod discarding discardMethod withMemory memory
    }
    .toList
    .map(bot =>
      setupCards(bot)
      (0 until cardsSeenAtStart).toList.foreach(bot.seeCard)
        bot
      )
  player :: bots
```
Il metodo `setupGameWithBots(botsParams: BotParamsType)` prende i parametri dei bot e, se sono coerenti, crea una lista 
di giocatori composta da un giocatore umano e da bot. Per ogni giocatore vengono inizializzate le carte presenti nella propria mano, 
rendendoli pronti per iniziare a giocare.

Per scegliere e istanziare un gioco è stato utilizzato un `enum`:
```scala
enum PlayableGame:
  case Cactus

  def name: String = this match
    case Cactus => "Cactus"
    
  def gameMVC: GameMVC = this match
    case Cactus => CactusMVC()
```  

Quando il numero di giocatori viene modificato, è necessario aggiornare la lista dei giocatori (bot) in modo relativo al numero scelto,
quindi eliminando o aggiungendo giocatori. Questo è stato fatto utilizzando il seguente metodo:
```scala
override def updatePlayersDisplay(players: Seq[VBox], diff: Int): Unit =
  _playersBox.children.clear()
  _players = diff match
    case diff if diff < 0 =>
      drawMethods = drawMethods.dropRight(-diff)
      discardMethods = discardMethods.dropRight(-diff)
      memoryList = memoryList.dropRight(-diff)
      _players.dropRight(-diff)
    case _ => _players ++ players
  _playersBox.children = _players
```

## Gestore schermate di gioco

Per gestire le schermate di gioco è stato creato un `trait StageManager` che fornisce i metodi necessari per mostrare e cambiare la 
schermata corrente ed anche per creare una nuova finestra:
```scala
trait StageManager:
  type SceneType

  def show(): Unit
  def setScene(scene: SceneType, showScene: Boolean): Unit
  def newStage(newScene: SceneType): Unit
```

## Struttura MVC

Per creare l'MVC è stato utilizzato il _Cake Pattern_, il quale è basato su _Component Programming_ e permette di creare
un'architettura modulare e flessibile, dove le dipendenze vengono fornite in modo automatico. 
Per cambiare l'implementazione di un componente è sufficiente istanziarne uno diverso senza dover cambiare nient'altro.

Di seguito un estratto del codice dell'MVC del menù principale:
```scala
object MainMenuMVC
    extends MainMenuModelModule.Interface
    with MainMenuControllerModule.Interface
    with MainMenuViewModule.Interface:

  override lazy val model: ModelType      = MainMenuModelImpl()
  override val controller: ControllerType = MainMenuControllerImpl()
  override val view: ViewType             = MainMenuScalaFxView()

  def run(): Unit = view.show()
```

### Moduli MVC

Il `trait Provider`, che fornisce un'istanza del modulo, è definita nella superclasse, mentre il `trait Component` è definito
nella sottoclasse e contiene le possibili implementazioni del modulo. Il `trait Interface` è definito nella sottoclasse e 
permette la successiva composizione dei moduli.

Come esempio, vengono riportati di seguito estratti di codice dei moduli MVC relativi al menù principale:

- **Model**:
```scala
object MainMenuModelModule extends ModelModule:
  override type ModelType = MainMenuModel
 
  trait MainMenuModel:
    var selectedGame: PlayableGame

  trait Component:
    class MainMenuModelImpl extends MainMenuModel:
      ...

  trait Interface extends Provider with Component
```

- **Controller**:
```scala
object MainMenuControllerModule extends ControllerModule:
  override type ControllerType = MainMenuController
  override type Requirements   = MainMenuModelModule.Provider with MainMenuViewModule.Provider

  trait MainMenuController extends Controller:
    def selectGame(game: PlayableGame): Unit
    def startGame(nPlayers: Int): Unit
    def startCactusGameWithBots(
        drawings: Seq[DrawMethods],
        discardings: Seq[DiscardMethods],
        memories: Seq[Memory]
    ): Unit

  trait Component:
    context: Requirements =>

    class MainMenuControllerImpl extends MainMenuController:
      ...

  trait Interface extends Provider with Component:
    self: Requirements =>
```

- **View**:
```scala
object MainMenuViewModule extends ViewModule:
  override type ViewType = View
  override type Requirements = MainMenuControllerModule.Provider

  trait MainMenuView extends View:
    def showFromFinalScreen(): Unit

  trait Component:
    context: Requirements =>

    class MainMenuScalaFxView extends MainMenuView:
      ...

  trait Interface extends Provider with Component:
    self: Requirements =>
```

[Back to index](../index.md) |
[Back to implementation](index.md)