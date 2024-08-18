# Enrico Tagliaferri

Il lavoro da me svolto all'interno del progetto comprende principalmente:
- Sviluppo della logica relativa al mazzo di carte e alla pila degli scarti.
- Sviluppo dell'interfaccia principale del gioco.
- DSL per agevolare l'utilizzo di ScalaFx.

Inoltre ho contribuito alle parti riguardanti la gestione delle fasi di gioco e delle fasi iniziali e finali del gioco _Cactus_.

## Gestione mazzo

### Inizializzazione del mazzo

Per la creazione del mazzo e della pila degli scarti, si è optato per un approccio il più funzionale possibile, prediligendo l'immutabilità dove possibile.
Infatti, la lista di carte, che nell'effettivo compone il mazzo, è una lista immutabile.

Dato che un mazzo può essere inizializzato mescolato o no (decidibile via parametro passato al costruttore), la lista delle carte viene inizializzata mediante l'uso di un'ulteriore lista `inputCards`.
A differenza del valore `_cards`, che viene creato nella classe astratta `AbstractDeck`, `inputCards` viene inizializzato nella classe concreta che estende `AbstractDeck`.
Questo viene fatto perché alla classe astratta viene lasciato solo il compito di creare un mazzo mescolato o no, mentre il compito di quali carte inserire nel mazzo è delegato alle classi concrete, più specifiche.
Di seguito un estratto del codice, in cui viene mostrato proprio questo aspetto.

```scala
abstract class AbstractDeck[C <: Card](shuffled: Boolean) extends Deck[C]:
  protected val inputCards: List[C] = List[C]()
  private val _cards: List[C] = 
    if shuffled then Random.shuffle(inputCards)
    else inputCards

/* Mazzo generico in cui la lista di carte viene passata direttamente dall'esterno. */
case class GenericDeck(override val inputCards: List[Card & Coverable], shuffled: Boolean)
  extends AbstractDeck[Card & Coverable](shuffled)

/* Mazzo di carte da poker regolare, senza le carte Jolly. */
case class PokerDeck(shuffled: Boolean) extends AbstractDeck[PokerCard & Coverable](shuffled):
    override val inputCards: List[PokerCard & Coverable] = for
      suit  <- PokerSuit.values.toList
      value <- Ace to King
    yield value OF suit
```

### Reset

Per resettare un mazzo, è possibile usare due metodi:
- `reset()`: ripristina il mazzo corrente.
- `resetWithPile(pile: DiscardPile)`: ripristina il mazzo, impostando come carte quelle presenti nella pila (invertendone l'ordine).

Per rendere il codice il più generico e riutilizzabile possibile, è stato usato il design pattern **template method**.
Infatti entrambi i metodi sopracitati sono implementati nella classe astratta `AbstractDeck`, però la creazione vera e propria del nuovo mazzo è lasciata alle classi concrete.
Questo è dovuto al fatto che per la classe astratta non sarebbe possibile creare un mazzo specifico, con un certo tipo di carte.
I metodi lasciati da implementare alle sottoclassi sono:
- `createDeck()`: crea il nuovo mazzo resettato.
- `pile`: restituisce una pila degli scarti specifica, con cui inizializzare il nuovo mazzo resettato.

```scala
abstract class AbstractDeck[C]:
  protected def pile: DiscardPile[C]
  protected def createDeck: Deck[C]
  
  def resetWithPile(pile: DiscardPile[C]): Deck[C] =
    head = INITIAL_HEAD_VALUE
    createDeck(pile.cards.reverse)

  def reset(): Deck[C] =
    val discardPile: DiscardPile = pile
    cards.foreach(discardPile.put(_))
    resetWithPile(discardPile)

case class PokerDeck extends AbstractDeck[PokerCard & Coverable]:
  override protected def createDeck(cards: List[PokerCard & Coverable]): Deck[PokerCard & Coverable] = new PokerDeck(false):
    override def cards: List[PokerCard & Coverable] = PokerDeck.this.cards
  override protected def pile: DiscardPile[PokerCard & Coverable] = PokerPile()
```

## Interfaccia principale

Uno degli aspetti più importanti dell'interfaccia è stato l'aggiornamento degli elementi, in conseguenza a delle modifiche della logica.
Infatti al variare di alcuni parametri (come per esempio le carte in mano a un giocatore), l'interfaccia deve aggiornarsi, mostrando le nuove informazioni.
Per questo scopo sono stati sfruttati degli oggetti di ScalaFx, chiami [proprietà](https://www.scalafx.org/docs/properties/).

Questi oggetti permettono di definire un valore e di osservare eventuali suoi aggiornamenti.
Il tutto è possibile grazie al fatto che a una proprietà è possibile associare un handler, chiamato quando il valore cambia.

## DSL per la View

Per rendere la manipolazione degli elementi della View più agile, è stato pensato un semplice DSL, più _user friendly_.
Nello specifico il suddetto DSL è pensato per la libreria ScalaFx, dato che l'interfaccia è stata implementata usando questa libreria.

Uno scopo del DSL è quello di fornire dei costruttori per alcuni elementi, in modo da creare degli oggetti con delle caratteristiche uniformi.
Per esempio, per quanto riguarda i pulsanti, esiste un metodo `Button` che crea un elemento `scalafx.scene.control.Button`, con alcuni parametri già impostati (come larghezza, altezza e lo stile).
In questo modo è più semplice mantenere la consistenza dell'aspetto degli elementi.

Un altro scopo per cui è stato pensato questo DSL, è quello di creare degli elementi, usando una sintassi più _umana_.
Questo è stato realizzato tramite l'utilizzo di extension methods, con dei nomi che richiamassero azioni umane.
Ognuno di questi extension methods prende come primo parametro un elemento ScalaFx e restituisce lo stesso, con il parametro modificato.
L'elemento preso in input deve essere il più generico possibile, così che un metodo copra più elementi possibile.
Se si considera il caso del metodo `at`, usato per impostare la posizione di un elemento, questo ha come _signature_:

```scala
extension [T <: Region](node: T) def at(position: (Int, Int)): T
```

così è possibile usare il metodo `at` per tutti gli elementi che estendono `scalafx.scene.layout.Region`.

In questo modo, per impostare la posizione di un pulsante alle coordinate `(0, 0)`, basta eseguire:

```scala
val button: Button = new Button() at (0, 0)
```

Alternativamente, se bisogna impostare più parametri, dato che la sintassi precedente potrebbe diventare confusionaria, può essere eseguito il seguente codice:

```scala
val button: Button = new Button()
	.at((0, 0))
	.saying("Click me!")
	.doing(_ => println("Hello from console"))
```

Questo DSL è stato pensato per semplificare l'utilizzo di ScalaFx, una libreria già esistente.
Questo ha portato al fatto che alcuni metodi sono specifici per alcuni elementi e sono quindi presenti delle "ripetizioni" tra i metodi.
Un esempio è il metodo per impostare il testo di un pulsante e quello per impostarlo a un elemento `scalafx.scene.text.Text`.
Nonostante il metodo specifico si chiami sempre `setText`, non avendo un parente in comune con questo metodo, è stato necessario creare due metodi diversi, uno per `Button` e uno per `Text`.