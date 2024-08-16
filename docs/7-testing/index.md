# Testing

All'interno del progetto, il processo di testing è stato un elemento cruciale per garantire la qualità e la robustezza del codice.
Per questo motivo, è stato adottato un approccio di unit testing utilizzando [ScalaTest](https://www.scalatest.org/), una delle librerie più versatili e potenti per il testing in Scala.

In particolare, è stato scelto lo stile _FlatSpec_ per la sua semplicità e la chiarezza con cui consente di strutturare i test.
Questo stile si distingue per la sua capacità di esprimere i casi di test in un linguaggio naturale, rendendo il codice dei test facile da leggere e mantenere.
La scelta di FlatSpec ha permesso di scrivere test concisi e organizzati, facilitando la comprensione del comportamento del sistema anche da parte di chi non è direttamente coinvolto nello sviluppo.

Il testing è stato parte integrante dello sviluppo del model, che è stato realizzato seguendo i principi del _Test Driven Development_ (TDD).
Questo approccio ha consentito di focalizzarsi sulla creazione di codice che fosse non solo funzionale, ma anche testabile fin dalle prime fasi dello sviluppo.
Ogni nuova funzionalità è stata implementata solo dopo aver scritto i test corrispondenti, garantendo così una copertura completa dei possibili casi d'uso.

L'adozione del TDD si è rivelata essenziale non solo per individuare e correggere tempestivamente eventuali anomalie, ma anche per mantenere la coerenza del codice esistente.
Grazie ai test automatici, è stato possibile effettuare refactoring e miglioramenti del codice in modo sicuro, con la certezza che il comportamento del sistema rimanesse invariato.

L'unica parte del progetto che non è stata coperta da test è quella relativa all'interfaccia grafica.
Nonostante il mancato utilizzo di strumenti dedicati, il testing è risultato possibile date le poche interazioni che l'utente è necessario che esegua col sistema.


[Back to index](../index.md) |
[Previous Chapter](../6-implementation/index.md) |
[Next Chapter](../8-conclusion/index.md)