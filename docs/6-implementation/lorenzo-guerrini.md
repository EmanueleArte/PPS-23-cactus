# Lorenzo Guerrini

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

Più lossPercentage è alto, più è probabile che la carta che il bot va a vedere venga dimenticata. Nella classe `CactusBotImpl` è presente una variabile `_knownCards: List[PokerCard]` che rappresenta le carte che il bot si ricorda. La funzione per vedere una carta è quindi così definita:
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
