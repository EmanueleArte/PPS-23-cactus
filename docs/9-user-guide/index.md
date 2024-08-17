# Guida utente

All'apertura dell'applicazione ci si trova in un menù. Da questo menù è possibile scegliere il gioco (in questo caso solamente Cactus è implementato) e le impostazioni relative a tale gioco con la quale si vuole proseguire. Dopo aver selezionato il numero di giocatori (compreso tra 2 e 6), è possibile personalizare le impostazioni relative ai bot, partendo dai draw methods:
- Deck: il bot pescherà solo dal mazzo.
- Pile: il bot pescherà solo dalla pila degli scarti.
- RandomDeck: il bot pescherà dal mazzo o dalla pila degli scarti ogni volta in maniera casuale.
- PileSmartly: il bot pescherà dalla pila degli scarti se la carta in cima ad essa è una buona carta, altrimenti pescherà dal mazzo.

È possibile impostare anche i discard methods:
- Unknown: il bot scarterà una carta scelta casualmente tra quelle che non conosce. Nel caso le conosca tutte applicherà il metodo known.
- Known: il bot scarterà la carta di valore più alto tra quelle che conosce. Nel caso non conosca carte, il bot applicherà il metodo unknown.
- Random: il bot scarterà una carta scelta casualmente.

Per quanto riguarda la memoria dei bot sono possibili le seguenti impostazioni:
- Bad: il bot avrà una pessima memoria e farà fatica a ricordarsi le carte che guarderà.
- Normal: il bot avrà il 50% di probabilità di ricordarsi la carta appena vista.
- Good: il bot avrà una buona memoria.
- VeryGood: il bot avrà una memoria molto buona.
- Optimal: il bot avrà una memoria ottima che gli permetterà di ricordarsi tutte le carte che guarderà.

[Back to index](../index.md) |
[Previous Chapter](../8-conclusion/index.md)
