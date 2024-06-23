# Processo di sviluppo

## Strumenti di continuous integration

Per il processo di _continuous integration_ è stato adottato l'uso di GitHub Actions.
Data la complessità ridotta del progetto, si è pensato di automatizzare solamente le fasi di testing e di linting.
Nello specifico è stato usato un workflow _CI_ che si occuperà di eseguire due job:

- **lint**: utilizza l'action [super-linter](https://github.com/marketplace/actions/super-linter).
- **test**: esegue i test del progetto su tre sistemi operativi diversi (windows 2022, macos 12 e l'ultima versione di ubuntu).
