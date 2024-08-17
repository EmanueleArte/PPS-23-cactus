# Enrico Tagliaferri

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