.. role:: red
.. role:: blue  
.. role:: remark   

======================================
Indicazioni
====================================== 
 
-------------------------------------
Il motto 
-------------------------------------


:remark:`Non c'è codice senza progetto.`

:remark:`Non c'è progetto senza analisi del problema.`

:remark:`Non c'è problema senza requisiti.`


---------------------------
Il template
---------------------------
Il documento ``template2021.html`` costituisce un punto di riferimento ma è
'process agnostic', cioè non indica il processo di sviluppo che adottiamo
per costruirlo.

---------------------------
L'analisi dei requisiti
---------------------------
L'analisi dei requisiti mira a:

:remark:`definire/chiarire COSA il committente intende.`

Occorre fare una analisi del testo che precisi in modo non ambiguo
il significato dei termini usati e le informazioni non esplicitamente espresse.
La costruzione di un *dizionario*  in linguaggio naturale è utile ma non risolutiva, 
in quanto esprime informazione ancora affetta da ambiguità se non da incoeranza e inconsistenza.

Dunque, le informazioni date in linguaggio naturale servono solo in una fase preliminare
dei lavori. 
Occorre esprimere le informazioni in modo 'formale', cioè in modo 
**comprensible alla macchina**.


A tal fine sono utili i linguaggi che permettono di definire :red:`modelli`
capaci di catturare gli aspetti essenziali, lasciando sullo sfondo dettagli 
anche importanti, ma non rilevanti nelle prime fasi.

Gli *Usecases* possono essere utili come linea-guida per definire una o più `TestUnits` funzionali.
Le specifiche degli *Usecases* espresse in linguaggio naturali possono essere inserite
come commenti in tali `TestUnits`.

Le :blue:`domande` da porre al committente mirano a chiarire:

-  la natura delle entità, cioè se dal punto di vista software, 
   una entità è (modellabile come un) 
   oggetto (POJO), un servizio, un processo, un attore

- se le varie entità devono essere distribuite su diversi nodi computazionali

- i vincoli tecnologici, cioè se il committente propone già qualche specifica tecnologia 
  o qualche specifico supporto per una data entità

- le interazioni (a procedure-call, a messaggi, sincrone, asincrone) tra le diverse entità 

Al termine della analisi dei requisiti dovrebbe essere definito 
un modello del sistema che mette in lune i macro-componenti 
(entità) e le loro interazioni, cioè
una prima :red:`architetture logica` del sistema.


---------------------------
L'analisi del problema
---------------------------
L'analisi del problema mira a:

:remark:`definire/chiarire le problematiche implicate dai requisiti.`

:remark:`fornire informazioni utili sui costi/tempi/risorse necessari.`

L'analisi del problema :red:`NON IMPLICA ancora alcuna progettazione`,
ma mira a fornire un quadro delle possibili soluzioni tecnologighe
individuando quelle più utili per il superamento delle problematiche poste dai requisiti.
La scelta finale sarà fatta dal progettista (che potrebbe anche ampliare lo spettro
della indagine - ricordiamo che siamo in un :blue:`processo iterativo a spirale` ).

L'analisi del problema serve per capire quali sono le maggiori problematiche 
da affrontare, le tecnologie da usare e le risorse (umane e temporali) necessarie.  
Inoltre gettano le basi per impostare il primo sprint di sviluppo e quindi per 
costruire un primo 'prototipo' funzionante del sistema da estendere poi in modo 
incrementale con gli sprint succesivi dopo una opportuna sprint-review con 
il committente



+++++++++++++++++++++++++
Architettura logica
+++++++++++++++++++++++++

Il risultato della analsi può essere sintetizzato nella definizione di una 
:red:`architettura logica` (che specializza/esetende quella scaturita dai requisiti )
che definisce la NATURA (oggetti, processi, servizi, attori, database, etc.) 
dei MACRO-COMPONENTI del sistema e della loro interazione, 
NON COME SOLUZIONE DI PROGETTO, ma come VINCOLI IMPLICATI dal problema.

:remark:`Il risultato della analisi del problema dovrebbe essere condiviso .`
 

L'analista potrebbe/dovrebbe dare uno sguardo complessivo al problema,
cercando anche di organizzare tutte le funzionalità per importanza, e come queste
funzionalità debbano essere  distribuite tra i vari MACRO-componenti.

+++++++++++++++++++++++++
Sviluppo incrementale
+++++++++++++++++++++++++

Un approccio INCREMENTALE (in stile SCRUM, ma non solo) è utile per 
aggredire la complessità del problema e per
mettere in luce in primis LE COSE
PIU' IMPORTANTI e PIU' CRITICHE senza perdersi in dettagli che possono distogliere
l'attenzione per formarsi un quadro generale di riferimento.

I vari SPRINT dovrebbero effettuare uno ZOOMING entro MACRO-COMPONENTI
innescando un processo ITERATIVO di analisi, progetto , sviluppo e testing di
quel componente o del SOTTOSISTEMA  che lo sprint vuole costruire.
Per questo può essere opportuno impostare ogni sprint come un 'sottoprogetto'
con sua propria spiegazione e testing. 

Lo sprint :math:`n+1` dovrebbe partire dai
risultati dello sprint :math:`n` e preparare un sottosistema (funzionante) 
che sarà l'input dello sprint :math:`n+2`.

------------------------------------------
Uso di modelli
------------------------------------------

L'uso dei modelli è utile (per non dire indispensabile) PER CONCENTRARE
l'attenzione SUGLI ASPETTI RITENUTI SALIENTI (tenendo conto anche
dei tempi, delle tecnologie disponibili  e dei costi):

L'architettura logica espressa mediante un modello eseguibile può essere il riferimento per la impostazione di
PIANI di testing (unit, integration, functional, acceptance) espressi in MODO
NON discorsivo (formale), comprensibile a una macchina. 

Per questo noi abbiamo perseguito l'idea di modelli eseguibili, sviluppando
una meta-modello in modo custom, visto che si trova ancora
nulla di questo tipo in rete.

Come già previsto da UML, è opportuno definire modelli come risultato delle 
fasi di analisi dei requsiti, analisi del problema e progetto.



------------------------------------------
Passi operativi
------------------------------------------
#. Costruire un repository GIT del progetto
#. Definire un primo modello del sistema come risultato della analisi del problema (e non del progetto della soluzione)
#. Includere nel documento di analisi gli appropriati riferimenti al modello
#. Definire qualche testplan significativo (cioè legato ai casi di uso) basato sul modello

