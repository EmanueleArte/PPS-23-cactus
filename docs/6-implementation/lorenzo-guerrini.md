# Lorenzo Guerrini

Il lavoro da me svolto all’interno del progetto comprende principalmente:
- creazione e sviluppo di giocatori e bot.
- gestione dell’effetto speciale riguardante le carte Jack sia lato model sia lato view.
- gestione dell’effetto speciale riguardante i Re rossi.
- gestione della fine del gioco e sviluppo dell’interfaccia grafica per la fine del gioco.

Di seguito riporto le parti più interessanti del codice da me realizzato.

## Bot e giocatori

Per gestire la memoria di un bot è stata creata un `enum Memory`, così definita:
```
enum Memory(val lossPercentage: Double):
    require(lossPercentage <= 1)
    require(lossPercentage >= 0)

    case Bad      extends Memory(0.8)
    case Normal   extends Memory(0.5)
    case Good     extends Memory(0.25)
    case VeryGood extends Memory(0.1)
    case Optimal  extends Memory(0)
```

Più lossPercentage è alto, più è probabile che la carta che il bot va a vedere venga dimenticata. Nella classe `CactusBotImpl` è presente una variabile `_knownCards: List[PokerCard]` che rappresenta le carte che il bot si ricorda. Il metodo per vedere una carta è quindi così definito:
```
override def seeCard(cardIndex: Int): Unit = cards match
  case c if c.isEmpty => throw new UnsupportedOperationException()
  case _ =>
    scala.util.Random.nextDouble() match
      case r if r >= _memory.lossPercentage => _knownCards = _knownCards ++ List(cards(cardIndex))
      case _                                => ()
```

In questo modo il bot si ricorderà della carta appena vista solo se il numero casuale generato è maggiore del suo lossPercentage (più tale valore è vicino allo 0 più ciò è probabile).

### DSL per i bot

Per la creazione di un bot è possibile utilizzare, ad esempio, questa riga di codice:

`"Bot" drawing DrawMethods.Deck discarding DiscardMethods.Known withMemory model.bot.CactusBotsData.Memory.Good`

È infatti stato definito un DSL per facilitare la generazione di bot, cosi strutturato:
```
object BotBuilder:
  class CactusBotImplWithDrawMethod(val name: String, val drawMethod: DrawMethods)
  class CactusBotImplWithDiscardMethod(val c: CactusBotImplWithDrawMethod, val discardMethod: DiscardMethods)

  object CactusBotDSL:
    extension (name: String)
      def drawing(drawMethod: DrawMethods): CactusBotImplWithDrawMethod = CactusBotImplWithDrawMethod(name, drawMethod)

    extension (c: CactusBotImplWithDrawMethod)
      def discarding(discardMethod: DiscardMethods): CactusBotImplWithDiscardMethod =
        CactusBotImplWithDiscardMethod(c, discardMethod)

    extension (c: CactusBotImplWithDiscardMethod)
      def withMemory(memory: Memory): CactusBotImpl =
        CactusBotImpl(c.c.name, List.empty[PokerCard & Coverable], c.c.drawMethod, c.discardMethod, memory)
```

In generale, per creare un bot con questo DSL, è sufficiente applicare ad una stringa (che sarà il nome del giocatore) il metodo drawing, seguito dal metodo discarding e successivamente da quello withMemory.

## Re rossi

Ogni `PokerCard` ha un valore intero da 1 (Asso) a 13 (Re). Per fare in modo che, nel momento di calcolare il punteggio, ogni Re rosso venga valutato 0 e non 13 è stata implementata questa funzione in `object ModelUtils`, oggetto rappresentante le funzioni utili e riutilizzate della parte di model:
```
  def isRedKing(c: PokerCard): Boolean = c.value match
    case PokerCardName.King => c.suit == PokerSuit.Hearts || c.suit == PokerSuit.Diamonds
    case _                  => false
```
Questa funzione è richiamata ad esempio in questo metodo nella classe CactusBotImpl, utile per calcolare il punteggio totale delle carte conosciute nella mano del bot, ma non è l'unico caso in cui viene utilizzata:
```
  private def totKnownValue: Int =
    _knownCards.map {
      case c if isRedKing(c) => 0
      case c                 => c.value
    }.sum
```

## Fine del gioco

### Schermata finale

Per visualizzare i punteggi di tutti i giocatori con la relativa classifica finale e vedere così chi ha vinto è stata creata appositamente una schermata. Tale schermata è comprensiva di controller e di view ed è visivamente molto semplice: presenta un titolo e una lista di giocatori in ordine di punteggio (dal più basso al più alto in quanto il giocatore o i giocatori col punteggio più basso vince/vincono). La creazione e implementazione della zona contenente la classifica viene fatta in questo metodo:
```
private def playersPane: VBox =
  val vbox = new VBox()
    .aligned(Pos.TopCenter)
    .spaced(10)
  var hboxes: Seq[HBox] = Seq.empty
  controller.playersScores.foreach((p, s) => {
    val hbox = Seq.fill(1)(
      new HBox()
        .aligned(Pos.Center)
        .spaced(50)
        .containing(LabelElement telling p.name bold)
        .containing(LabelElement telling s.toString)
    )
    hboxes = hboxes ++ hbox
  })
  vbox.children = hboxes
  vbox
```

Il metodo restituisce un `VBox` che sarà contenuto in un `VBox` più grande, che richiama il metodo semplicemente con `.containing(playersPane)`.

### Prima della schermata finale

Prima che venga mostrata la schermata finale (e che quindi venga chiuso il tavolo di gioco) tutte le carte di tutti i giocatori vengono scoperte. Dopodiché, solo alla pressione del pulsante Continue verrà aperta la pagina con i punteggi. Il tutto viene gestito da questo metodo:
```
  override def handleGameOver(): Unit =
    players.foreach(_.cards.foreach(_.uncover()))
    currentPhase_=(CactusTurnPhase.GameOver)
```
A questo punto, quando la `currentPhase` è impostata a `CactusTurnPhase.GameOver`, alla pressione del pulsante Continue verrà avviata la schermata finale, in quanto il metodo lanciato alla pressione di tale pulsante è:
```
override def continue(): Unit = context.model.currentPhase match
  case CactusTurnPhase.GameOver =>
    val finalScreenMVC = FinalScreenMVC
    finalScreenMVC.setup(
      ListMap(
        context.model.calculateScore.toSeq.sortWith(_._2 < _._2): _*
      )
    )
    finalScreenMVC.run()
  case _ =>
    context.model.continue()
    context.view.updateViewTurnPhase()
    context.view.updateDiscardPile()
```

`finalScreenMVC.setup(ListMap(context.model.calculateScore.toSeq.sortWith(_._2 < _._2): _*))` prepara il controller della schermata finale con i giocatori e i relativi punteggi già ordinati per la classifica definitiva.
