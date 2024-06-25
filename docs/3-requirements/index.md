# Requisiti
## Dominio
- Il sistema dovrà permettere all’utente di selezionare a quale gioco giocare, in cui si usano le carte da poker, da una lista (inizialmente ne sarà implementato solo 1, ovvero Cactus).
- Il giocatore dovrà giocare contro dei bot specifici per il gioco selezionato.

## Funzionali
### Utente
- Gli utenti dovranno interagire con il sistema tramite un’interfaccia grafica (GUI).
- Gli utenti possono visualizzare i diversi fattori che rappresentano lo stato attuale della partita:
  - la disposizione delle carte
  - la pila degli scarti
  - il giocatore di turno
  - lo stato finale della partita (punteggi, vincitore)
- Gli utenti possono interagire per effettuare le seguenti azioni:
  - selezionare il gioco
  - impostare il numero di bot (con un limite massimo)
  - impostare il comportamento dei bot
  - avviare la partita
  - guardare le proprie carte
  - mandare avanti i turni (sia il proprio sia quello dei bot)
  - effettuare una mossa in base alla situazione corrente

### Sistema
#### Generali
- La partita deve essere avviata con una certa configurazione che comprende:
  - il gioco da avviare
  - i bot con il relativo comportamento
  - la visualizzazione facoltativa di un tutorial
- Il passaggio del turno (sia dell’utente, sia dei bot) è lasciato all’utente tramite un pulsante visualizzato nella GUI, indipendentemente dal gioco.

#### Specifici per Cactus
- Il campo da gioco deve essere rappresentato dal mazzo di pesca, dalla pila degli scarti e dalle mani dei giocatori. Il mazzo di pesca e le mani dei giocatori sono composti da carte coperte, mentre la pila degli scarti no.
- In caso si termini il mazzo delle carte da pescare, verrà lasciata nella pila degli scarti solo l’ultima carta scartata, mentre il resto della pila verrà mescolato e usata come mazzo.
All’inizio della partita, ad ogni giocatore devono venire assegnate 4 carte casuali coperte che comporranno la mano.
- La mano di ogni giocatore deve preservare la posizione delle carte
- Mosse possibili:
  - **inizio partita**: cliccare su due carte che verranno mostrate all’utente fino a che non decide di continuare
  - **inizio turno**: cliccare sul mazzo o sulla pila degli scarti per scegliere da dove pescare. La carta pescata dal mazzo potrà essere vista.
  - **fine turno**: cliccare la carta da scartare che verrà scoperta e messa in cima alla pila degli scarti, se la carta ha un effetto speciale dare la possibilità di utilizzarlo oppure passare il turno normalmente
  - **qualsiasi turno**: se un bot scarta una carta e l’utente pensa di averne una dello stesso valore, può cliccare una delle sue carte, che viene scoperta. Se la carta cliccata ha lo stesso valore, viene aggiunta alla pila; altrimenti viene mostrato un avviso per informare l’utente che la carta è sbagliata. Una volta chiuso l’avviso, la carta viene coperta e viene aggiunta una carta alla mano dell’utente.
  - Quando viene cliccato il pulsante di fine turno, verrà mostrato un banner con due opzioni: “finisci il turno” oppure “chiama cactus”.
  - Nel caso in cui venga provata a fare un’azione non concessa, l’utente verrà notificato.
- Effetti speciali:
  - **Jack scartato**: si potrà continuare oppure cliccare su una delle proprie carte per mostrarla, prima di andare avanti
  - **Donna scartata**: si potrà continuare oppure cliccare su una coppia di carte in campo per scambiarle, prima di andare avanti
  - **Asso scartato**: si potrà continuare oppure cliccare un giocatore a cui far pescare una carta dal mazzo.
  - Quando viene chiamato “cactus”, il giocatore che l’ha chiamato mostra le carte ed è immune agli effetti. Viene effettuato un ultimo giro, fino al giocatore precedente a colui che ha chiamato la fine della partita (quando il giocatore prima finisce il turno, termina anche il gioco).
- Alla fine del gioco verranno mostrate tutte le carte e verrà calcolato in automatico il punteggio di ogni giocatore, mostrando il vincitore (o i vincitori in caso di parimerito).
- Alla fine della partita verranno mostrati i tasti per iniziare un’altra partita o uscire dal gioco.

## Non funzionali
- Realizzazione di un’interfaccia grafica che aiuti l’utente a realizzare le mosse in maniera intuitiva e rapida, ad esempio avvisando il giocatore se sta effettuando una mossa non permessa e suggerendo quella corretta.
- Sviluppo di una notazione standard per le carte.
- Il sistema deve essere progettato in maniera tale che sia facile implementare altri giochi (con i relativi bot).

## Implementazione
- Utilizzo di Scala 3.x
- Utilizzo di JDK 21+

## Opzionali
- Impostazioni aggiuntive relative alla GUI.
- Sviluppo di altri giochi (esempio: blackjack, poker).
- Sviluppo di un sistema di leaderboard con il numero di vittorie dei giocatori.
- Possibilità di fare partite lunghe sommando i punteggi parziali fino ad un valore scelto.

[Back to index](../index.md) | 
[Previous Chapter](../2-development-process/index.md) | 
[Next Chapter](../4-architectural-design/index.md)