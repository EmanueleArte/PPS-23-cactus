# Introduzione
In questo progetto si vuole realizzare un sistema che permetta ad un utente di giocare a diversi giochi di carte contro dei bot. Tra i giochi a cui è possibile giocare, inizialmente l'unico implementato è **_Cactus_**, ovvero un gioco di strategia, memoria e fortuna a turni (che si susseguono in senso anti-orario), dove vengono utilizzate le carte da poker.

L’obiettivo è fare meno punti degli altri giocatori (ogni carta vale un quantitativo di punti pari al numero scritto su di essa, esempio: 7 di cuori → 7 punti, mentre i Jack, Donna e Re → rispettivamente 11, 12 e 13 punti). 

All’inizio della partita vengono distribuite 4 carte coperte a ciascun giocatore, il quale può vedere solo 2 di esse, per una sola volta e gli è quindi richiesto di ricordarle. Ad ogni turno, per prima cosa, bisogna pescare una carta dal mazzo, guardarla e alla fine scartarne una (che può essere quella appena pescata oppure una già in possesso del giocatore). La carta scartata può, eventualmente, venire pescata dal giocatore successivo in sostituzione della pescata classica dal mazzo. Ogni giocatore, senza bisogno che sia il proprio turno, può scartare una delle proprie carte, se pensa che sia dello stesso valore di quella appena scartata dall'ultimo giocatore che ha terminato il turno (es: 6 di cuori in cima alla pila degli scarti, si può scartare uno o più 6 di un altro seme), nel caso si sbagli, come malus, si deve pescare una carta dal mazzo, da aggiungere alla propria mano, che non può essere guardata. Alcune carte hanno degli effetti speciali che si attivano nel momento in cui vengono scartate oppure durante il calcolo dei punti.

Il gioco termina quando, alla fine del proprio turno un giocatore chiama “cactus”, ovvero scopre le proprie carte (diventando anche immune agli effetti delle carte degli altri giocatori) e da’ inizio ad un ultimo giro di gioco a cui non potrà partecipare. Quando sarà di nuovo il turno di chi ha chiamato "cactus" il gioco si conclude effettivamente e si procede al calcolo dei punti.

## Effetti speciali
Come citato precedentemente ci sono alcune carte che hanno degli effetti speciali i quali servono a fornire un vantaggio durante la partita e che si attivano in due situazioni distinte: al momento in cui vengono scartate o durante il calcolo dei punti.

Le carte il cui effetto si attiva nel momento in cui vengono scartate sono:
- gli **Assi**: se uno di questi ultimi viene scartato, si deve far pescare una carta aggiuntiva, che non potrà essere guardata, ad un giocatore a scelta.
- i **Jack**: se uno di questi ultimi viene scartato, è possibile guardare una delle proprie carte coperte.
- le **Donne**: se una di queste ultime viene scartata, è possibile scambiare due carte, in possesso di giocatori differenti, tra loro (esempio: si scambia una propria carta con una di un altro giocatore, a propria scelta sia carta che giocatore. Oppure è possibile scegliere carte di altri giocatori e lasciare la propria mano invariata).

Tutti gli effetti precedentemente elencati non sono cumulabili (esempio: se vengono scartati due Jack, cosa permessa dal regolamento, non sarà possibile guardare due delle proprie carte coperte) e sono attivabili solo se l'azione di scartare la carta coincide con la fine del proprio turno.

Le carte il cui effetto si attiva nel momento del calcolo dei punti sono:
- i **Re rossi**: al momento del calcolo dei punti i Re appartenenti a semi di colore rosso (_Quadri_ e _Cuori_) valgono 0 punti.

[Back to index](../index.md) | 
[Next Chapter](../2-development-process/index.md)
