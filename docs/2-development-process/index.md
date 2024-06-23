# Processo di sviluppo

## Scrum
La metodologia di sviluppo applicata su questo progetto è di tipo _Agile_, in particolare
si è adottata la variante _Scrum_.

Il processo di sviluppo Scrum è un processo _iterativo_ e _incrementale_, in cui a ogni
iterazione si aggiungono nuove funzionalità al sistema o si raffinano delle funzionalità
pre-esistenti.

La prima attività all'interno del processo di sviluppo Scrum è quella di redarre il
_Product Backlog_, ovvero una lista di funzionalità, dette _items_.

Le iterazioni del processo, chiamate _sprint_, saranno svolte settimanalmente. Ogni sprint
prevede le seguenti attività:
- **Sprint Planning**: riunione iniziale in cui si selezionano gli item da implementare
  durante lo sprint e si scompongono in sotto-funzionalità, di cui il team di sviluppo
  fornisce stime sulla loro complessità e alcuni design di dettaglio. Al termine dello
  Sprint Planning si produce uno _Sprint Backlog_ che assegna a ogni membro del team
  dei _task_ da eseguire.

Al termine dello sprint si prevedono tre ulteriori attività:
- **Product Backlog Refinement**: una riunione in cui il team di sviluppo di accorda sulle
  migliorie che devono essere apportate al Product Backlog.
- **Sprint Review**: si valuta il progresso del progetto ottenuto al termine dello sprint,
  verificando che sia un _PSPI (Potentially Shippable Product Increment)_.
- **Sprint Retrospective**: si valuta il processo di sviluppo adottato, discutendo possibili
  cambiamenti che potrebbero aumentare l'efficacia del team.

L'organizzazione del personale all'interno del processo Scrum prevede tre ruoli:
- **Product Owner**: si occupa di redarre il _Product Backlog_ e di verificare l'adeguatezza del
  sistema realizzato. Il ruolo, nel nostro caso specifico, è condiviso tra tutti i membri del team.
- **Development Team**: si occupa di progettare soluzioni adeguate ai task definiti dal Product
  Owner, stimando tempi di realizzazione e proponendo modifiche sul sistema al Product Owner.
  Il team di sviluppo sarà composto da:
    - Enrico Tagliaferri
    - Emanuele Artegiani
    - Lorenzo Guerrini
 
## Test-Driven Development
Durante lo sviluppo del sistema è stato scelto di applicare il più possibile il _Test-Driven Development (TDD)_,
il cui scopo è quello di anticipare il prima possibile la fase di testing per minimizzare i costi di manutenzione e 
il rischio di fallimento del progetto.

Il processo seguito durante il TDD è un processo iterativo chiamato _Red-Green-Refactor (RGR)_, che prevede a ogni
iterazione le seguenti fasi:
1. **Red**: scrivere un test che fallisca per una certa funzionalità da implementare
2. **Green**: scrivere il codice di produzione che soddisfi il test definito precedentemente
3. **Refactor**: ristrutturare sia il codice di testing che quello di produzione

A supporto di questo processo, è stato adottato il seguente strumento: _ScalaTest_: framework per la definizione di unit test per scala.

## Quality Assurance
Per il controllo della qualità del sistema sono stati adottati i seguenti strumenti:
- **Scala Formatter**: controlla lo stile del codice scala
- **Wart Remover**: individua possibili difetti nel codice scala

## Build Automation
Per automatizzare i processi di compilazione, testing e release del codice sviluppato si è deciso di utilizzare **Sbt**
come strumento di _Build Automation_.

## Strumenti di continuous integration
Per il processo di _continuous integration_ è stato adottato l'uso di GitHub Actions.
Data la complessità ridotta del progetto, si è pensato di automatizzare solamente le fasi di testing e di linting.
Nello specifico è stato usato un workflow _CI_ che si occuperà di eseguire due job:

- **lint**: utilizza l'action [super-linter](https://github.com/marketplace/actions/super-linter).
- **test**: esegue i test del progetto su tre sistemi operativi diversi (windows 2022, macos 12 e l'ultima versione di ubuntu).

[Back to index](../index.md) | 
[Previous Chapter](../1-introduction/index.md) | 
[Next Chapter](../3-requirements/index.md)
