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

