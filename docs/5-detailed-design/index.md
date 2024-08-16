# Design di dettaglio

## Carte da gioco e mazzi

Nella [figura](#card-deck-pile-uml) è possibile vedere il diagramma UML relativo alle relazioni tra le carte, il mazzo da gioco e la pila degli scarti.

<span id="card-deck-pile-uml"></span>
![card-deck-pile-uml.svg](card-deck-pile-uml.svg)

### Carte

Uno degli elementi alla base dell'applicazione sono le carte, dato che i giochi che potranno essere implementati si basano proprio su queste.
Card è un'interfaccia generica che rappresenta una carta con un valore (V) e un seme (S).

È un tratto che definisce le caratteristiche essenziali che ogni carta deve possedere:
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

## Flusso di gioco (Cactus)

Il flusso di gioco è si articola in fasi sequenziali, nelle quali il giocatore può effettuare solo determinate azioni.
Il corretto avanzamento attraverso queste fasi è essenziale per mantenere l'integrità e il ritmo del gioco, garantendo che ogni giocatore segua le regole stabilite e compia le azioni nel momento giusto.

Le fasi principali del gioco sono:
- **Inizio gioco**: prima fase da risolvere, ci si entra solo all'inizio del gioco; dopodiché non viene più acceduta.
In questa fase il giocatore deve scegliere due carte da vedere.
- **Pescare**: il giocatore pesca una carta dal mazzo o dalla pila degli scarti.
- **Scartare una carta**: il giocatore scarta una carta dalla sua mano di gioco.
- **Scartare carte uguali**: i giocatori possono scartare delle carte con lo stesso valore dell'ultima scartata.
- **Chiamare cactus**: il giocatore può chiamare _Cactus_, ponendo fine alla partita.

All'interno del gioco verrà mantenuto un riferimento alla fase corrente.
Questo verrà aggiornato ogni volta che l'azione effettuata in una fase terminerà.
Per esempio, dopo la fase _Pescare_ viene sempre quella di scartare una carta, quindi dopo che il giocatore ne ha pescato una, il riferimento verrà aggiornato direttamente dal metodo associato alla fase _Pescare_.

L'oggetto `Logic`, che gestirà il gioco nella sua componenete più dinamica, metterà a disposizione dei metodi per gestire le interazioni base che il giocatore può effettuare (come per esempio, cliccare il mazzo, la pila degli scarti, una carta della propria mano o un altro giocatore).
Ognuno di questi metodi, dovrà controllare se può essere chiamato nella fase corrente, dato che ognuno di essi sarà associato a un'azione dell'utente.
Quindi se il giocatore clicca sul mazzo, per pescare una carta, verrà chiamato un metodo `draw()`.
All'interno di questo, verrà eseguito l'handler associato all'azione _Pesca_, solo se la fase è _Pescare_; altrimenti la fase non viene aggiornata e si attende che l'utente compia l'azione giusta.