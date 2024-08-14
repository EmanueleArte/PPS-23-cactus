# Design di dettaglio

## Bot e giocatori

Per far sì che il gioco sia giocabile, sono necessari i giocatori. Alla base di tutto ci sta l'interfaccia `Player`, un trait che rappresenta un giocatore base di qualsiasi gioco di carte.
Al suo interno troviamo:
- il type CardType, che rappresenta il tipo di carte con il quale il giocatore avrà a che fare durante il gioco.
- val name, il nome del giocatore.
- i metodi generici per un qualsiasi giocatore di un qualsiasi gioco di carte:
  - cards: List[CardType]: restituisce le carte nella mano del giocatore.
  - draw(drawable: Drawable[CardType]): Unit: pesca una carta dal mazzo passato come parametro.
  - drawCovered(drawable: Drawable[CardType]): Unit: pesca una carta dal mazzo passato come parametro, ma la carta rimane coperta.
  - discard(cardIndex: Int): CardType: scarta una carta dalla mano e la restituisce.
  - isEqualTo(anotherPlayer: Player): Boolean: confronta due giocatori e restituisce se sono uguali.

Un'implementazione dell'interfaccia Player specifica per il gioco Cactus è la case class `CactusPlayer`. Il CardType è definito come PokerCard & Coverable e sono implementati i metodi dell'interfaccia `Player` con un'aggiunta di altri due metodi:
- calledCactus: Boolean: restituisce se il giocatore ha chiamato cactus.
- callCactus(): Unit: permette al giocatore di chiamare cactus.
Questi metodi sono specifici per il gioco Cactus, per questo sono stati inseriti solamente nell'implementazione dell'interfaccia relativa a tale gioco.

Per rendere completa l'applicazione erano necessari anche i giocatori avversari al giocatore umano. Per questo è stata creata l'interfaccia `CactusBot`, la quale definisce tutti i metodi necessari ad un bot di Cactus per giocare. La classe `CactusBotImpl` implementa l'interfaccia CactusBot ed estende CactusPlayer, in quanto il bot è nell'effettivo un giocatore di Cactus e i metodi implementati in CactusPlayer sono necessari anche per i CactusBot.
I CactusBot sono pensati per essere più o meno intelligenti a seconda delle impostazioni che l'utente può definire prima della partita. Per questo nell'oggetto `CactusBotData` sono presennti tre enum:
- enum DrawMethods: definisce i metodi di pesca delle carte (dal mazzo principale, dalla pila degli scarti, casuale o intelligente in base alla carta in cima alla pila degli scarti).
- enum DiscardMethods: definisce i metodi di scarto delle carte in mano (una carta conosciuta, una sconosciuta o casuale).
- enum Memory(val lossPercentage: Double): definisce la memoria (non buona, normale, buona, molto buona o ottima).
Un valore per ciascuno di questi enum viene passato al CactusBot in fase di creazione.

## Carte da gioco e mazzi

Nella [figura](#card-deck-pile-uml) è possibile vedere il diagramma UML relativo alle relazioni tra le carte, il mazzo da gioco e la pila degli scarti.

<span id="card-deck-pile-uml"></span>
![card-deck-pile-uml.svg](card-deck-pile-uml.svg)

### Carte

Uno degli elementi alla base dell'applicazione sono le carte, dato che i giochi che potranno essere implementati si basano proprio su queste.
Card è un'interfaccia generica che rappresenta una carta con un valore (V) e un seme (S).

È un trait che definisce le caratteristiche essenziali che ogni carta deve possedere:
- **value**: il valore della carta, il cui tipo è determinato dal generico V.
- **suit**: il seme della carta, il cui tipo è determinato dal generico S.
Questa interfaccia consente la creazione di carte con diverse combinazioni di valori e semi, mantenendo la flessibilità del sistema.

Per fornire maggiore flessibilità, è stata creata un'interfaccia `Coverable`, che fornisce a una carta dei metodi per gestire le azioni di copertura e scopertura di essa.

`PokerSuit` è un'enumerazione che rappresenta i quattro semi delle carte nel gioco del poker. Gli elementi inclusi sono:
- Spades (Picche)
- Diamonds (Quadri)
- Hearts (Cuori)
- Clubs (Fiori)
Questa enumerazione fornisce una rappresentazione standard dei semi nel contesto di un mazzo di carte da poker.

La classe `PokerCard` rappresenta una carta a semi francesi, il cui valore quindi è di tipo intero, mentre il seme è uno tra quelli specificati nell'enumerazione `PokerSuit`.

### Mazzo e pila degli scarti

Il mazzo e la pila degli scarti sono un elemento centrale nell'applicazione, insieme alle carte.
Infatti, oltre a essere presente un mazzo e una pila all'interno di ogni gioco, questi dovranno essere usati anche dai giocatori, in diverse fasi (come per esempio quella di pesca).

Dato che in diversi giochi, un giocatore può pescare sia dal mazzo, che dalla pila degli scarti, è stata pensata un'interfaccia comune che rappresenta un oggetto generico, da cui è possibile pescare delle carte.
Questa interfaccia `Drawable` mette a disposizione un metodo `draw()` che restituisce la prossima carta.
Nello specifico restituisce un valore opzionale, in quanto l'oggetto potrebbe essere esaurito, quindi non disporre più di carte.
`Drawable` è formata da un parametro, che specifica il tipo di carte di cui è composto.

L'interfaccia `Deck` estende da `Drawable` e rappresenta un mazzo generico.
È composta infatti dai metodi:
- cards(): List[C]: restituisce la lista di tutte le carte nel mazzo. 
- size(): Int: restituisce il numero di carte presenti nel mazzo. 
- shuffle(): Deck[C]: mescola le carte nel mazzo, restituendo un nuovo mazzo mescolato. 
- reset(): Deck[C]: resetta il mazzo allo stato iniziale. 
- resetWithPile(pile: DiscardPile[C]): Deck[C]: resetta il mazzo usando una pila degli scarti.

L'interfaccia `DiscardPile` estende da `Drawable` e rappresenta una pila degli scarti generica.
È composta infatti dai metodi:
- cards(): List[C]: restituisce la lista di tutte le carte nella pila di scarto. 
- size(): Int: restituisce il numero di carte presenti nella pila di scarto. 
- put(card: C): DiscardPile[C]: aggiunge una carta alla pila di scarto, restituendo la pila aggiornata. 
- empty(): DiscardPile[C]: svuota la pila di scarto, restituendo una pila vuota.

Le classi astratte `AbstractDeck` e `AbstractDiscardPile` forniscono alcune implementazioni di base e definiscono dei metodi protetti per la gestione interna.
Queste classi sono usate per la creazione di oggetti _mazzo_ e _pila degli scarti_ specifici.

Per quanto riguarda le classi concrete, `PokerDeck` e `PokerPile` rappresentano rispettivamente un mazzo e una pila di scarto, in cui vengono usate delle `PokerCard`.


## MVC scalabile per vari giochi

Nella [figura](#mvc-uml) è possibile vedere il diagramma UML relativo alle relazioni tra i componenti del pattern MVC, utilizzati per la realizzazione dell'applicazione.

<span id="mvc-uml"></span>
![mvc-uml.svg](mvc-uml.svg)

### MVC

Il pattern architetturale MVC è stato raggiunto con l'aiusilio di un design pattern chiamato **Cake Pattern** (basato su **Component programming**).

Quest'ultimo permette di creare un'architettura modulare e scalabile, in cui i componenti sono facilmente sostituibili e configurabili.
Nel Cake Pattern, i **"component providers"** sono definiti come trait con un valore astratto (simile a un singleton). 
Altri componenti verranno mescolati con i provider, ricevendo automaticamente le dipendenze. 
I provider, l'interfaccia, l'implementazione, i requisiti, ecc., possono quindi essere incollati insieme con facilità.

### Model

`ModelModule` è un'interfaccia generica che rappresenta il modulo di un model.

Il modulo in questione deve possedere un **Provider**, che fornisce un'istanza del model. Inoltre, per essere il più generico possibile, 
quest'ultimo contiene il type alias `ModelType`, che rappresenta il tipo del modello, il quale viene definito dall'implementazione dell'interfaccia.

Inoltre, le classi che implementano `ModelModule` possono possedere un trait `Component`, che contiene le classi, le quali sono quindi sottotipo di `ModelType`, che possono essere 
istanziate e successivamente fornite dal provider. 

### Controller

`ControllerModule` è un'interfaccia generica che rappresenta il modulo di un controller.

Analogamente a `ModelModule`, sono presenti un **Provider**, che fornisce un'istanza del controller e un type alias `ControllerType`, che rappresenta il tipo del controller.
Le classi che implementano `ControllerModule` possono possedere un tratto `Component` con un funzionamento identico a quello presente nelle implementazioni di `ModelModule`.

In più, nel modulo del controller il type `Requirements` rappresenta le dipendenze che il controller richiede per poter funzionare, senza necessariamente averle già istanziate al prima dell'uso.
Dentro `Component`, l'oggetto **context** fornisce accesso alle dipendenze definite in `Requirements`.

### View

`ViewModule` è un'interfaccia generica che rappresenta il modulo di una view.

La sua struttura è completamente analoga a quella di `ControllerModule`, con la differenza che il type alias `ViewType` rappresenta il tipo della view.

### Composizione dei moduli

L'implementazione di ognuno dei moduli precedentemente descritti contiene un trait `Interface`, il quale è essenziale per utilizzare il Cake Pattern al meglio e
che estende `Provider` e `Component`, i quali rispettivamente forniscono un'istanza del componente e le classi che possono essere istanziate e fornite dal provider.

La classe che ha il compito di unire i moduli estende `Interface` del model, `Interface` del controller e `Interface` della view, inoltre 
istanzia i componenti forniti dai vari provider e definisce eventuali metodi. In questo modo i componenti vengono mescolati 
e le dipendenze vengono ricevute automaticamente.

