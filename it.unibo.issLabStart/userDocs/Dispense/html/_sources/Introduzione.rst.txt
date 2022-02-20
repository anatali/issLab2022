.. role:: red
.. role:: blue  
.. role:: remark   

 
======================================
Costruire software
======================================
Il software puo essere definito come:

- l'insieme di *frasi espresse in un qualche linguaggio formale* al fine di istruire un elaboratore 
  o una rete di elaboratori, 

Il :blue:`software non ha consistenza fisica`; può consumare energia ed altre risorse, 
produrre effetti utili o dannosi, avere conseguenze rilevanti sul piano economico e sociale, 
ma il software è del tutto privo di massa.

Le conseguenze di questa caratteristica sono molteplici, sia sul piano pragmatico che sul piano filosofico. 
Limitando il discorso al contesto della produzione industriale, si è diffusa la convinzione che 
la costruzione del software non richieda, per sua natura, processi di produzione tipici dell'ingegneria tradizionale.

Nell'**ingegneria tradizionale** (meccanica, edile, etc) il costo del materiale costituisce spesso più del 50% 
del costo totale di un progetto, mentre nella produzione del software è il costo del lavoro ad essere preponderante: 
si parte dal 70% fino a giungere quasi al 100%. L'ingegneria tradizionale ha anche sperimentato che un cambiamento 
di costo 1 in fase di analisi potrebbe costare 1000 in fase di produzione. 
Per questo l'ingegneria classica diversifica le fasi di produzione delineando un ben noto flusso di lavoro 
(*workflow*) costituito da un insieme di passi (o tasks): 

#. definizione dei requisiti,
#. analisi del problema, 
#. progetto della soluzione, 
#. realizzazione del prodotto, 
#. collaudo 
#. messa in opera 

spesso eseguiti uno dopo l'altro, in un classico :blue:`processo di sviluppo sequenziale o a cascata`.

Nella produzione industriale del software è invece piuttosto comune cercare di abbattere i costi 
di progetto e di sviluppo, anche limitando le dimensioni del gruppo di lavoro, 
e aggredire il mercato prefissando una data di distribuzione del prodotto, che viene di frequente 
rilasciato non completamente finito, accollando all'utente parte dell'onere di collaudo. 

Sotto la spinta di stringenti vincoli di *time to market* (TTM) molte aziende adottano uno schema del tipo 
"scrivi, prova e correggi", mirando alla produzione di codice al minor "costo immediato" possibile. 
Le :blue:`fasi di analisi e progetto` anche se accuratamente svolte, non sempre sono adeguatamente documentate, 
e *quasi mai corralete in modo sistematico con il codice prodotto*.

Il processo di costruzione del sofware è quindi :blue:`influenzato da una potente forza`, 
legata alla natura stessa del software: la spinta a impostare la costruzione di un prodotto in modo **bottom-up**, 
a partire da una specifica tecnologia costituita da un linguaggio di programmazione, 
o da un framework applicativo o da una piattafforma operativa.

La principale conseguenza negativa di questa forza è l'assenza di una esplicita descrizione di progetto 
che permetta di anticipare la valutazione dei rischi e le potenziali difficoltà connesse allo sviluppo. 
In molti casi adeguate fasi di analisi e di progettazione hanno luogo, anche in modo sistematico; 
ma ciò putroppo quasi sempre accade solo nella mente dei costruttori; 
nel codice finale non vi è più traccia alcuna di queste fasi, se non qualche debole segnale legato a sporadici commenti.

Tuttavia, anche se il codice fosse accuratamente documentato sia in relazione all'analisi sia in relazione 
alle scelte di progetto, la **riduzione del prodotto al solo codice sarebbe non accettabile**, 
se non nel caso di sistemi software semplici. 

All'aumentare della complessità infatti, la :blue:`mente umana ha bisogno`, 
per comprendere, di decomporre il problema in parti di ampiezza limitata, 
:blue:`articolando la descrizione in livelli di astrazione diversi`; 
poiché il codice deve inevitabilmente esprimere il sistema finale nei suoi minimi dettagli, 
la maggior parte delle persone sarebbe incapace di leggerlo con profitto anche se a conoscenza 
delle regole sintattiche del linguaggio di programmazione.

-------------------------------------
La crisi del software 
-------------------------------------

Impostare un processo di produzione in assenza di descrizioni del sistema che permettano di anticipare 
la valutazione dei rischi espone il processo stesso a un potenziale fallimento; 
non meraviglia dunque che si senta spesso parlare di crisi del software.

La letteratura [Glass97], [ Software_engineering_disasters] riporta casi di fallimento di un numero 
sorprendentemente rilevante di progetti software, evidenziando un insieme di cause principali:

- Cattiva specifica e gestione dei requisiti.
- Comunicazioni ambigue ed imprecise tra i diversi attori del processo di produzione 
  (utenti, manager, analisti, progettisti, implementatori).
- Architetture finali del sistema fragili (non robuste).
- Inconsistenze tra requisiti, progetto e realizzazione.
- Collaudi inadeguati o insufficienti.
- Inadeguata capacità di valutare e gestire i rischi e di controllare la propagazione dei cambiamenti.

Queste potenziali fonti di insuccesso hanno amplificato la loro influenza nel momento in cui 
l'intera disciplina ha vissuto la :blue:`transizione` da una dimensione prevalentemente :blue:`algorimtico-trasformazionale` 
a un dimensione fortemente :blue:`sistemistico-architetturale`.

-------------------------------------
Il ruolo dell'architettura
-------------------------------------

La moderna costruzione del software riconosce all'architettura del sistema un ruolo strategico, 
nonostante il termine *architettura* sia tra i vocaboli più sovraccarichi di significato.

Normalmente, si parla di architettura di un sistema quando ci si vuole riferire all'insieme delle 
macro-parti in cui il sistema si articola, includendo le loro responsabilità, relazioni e interconnessioni. 

Per molti, il termine *architettura* potrebbe però evocare l'immagine di uno schema in cui compare una rete 
di blocchi e linee di connessione; questa visione andrebbe meglio indicata col termine mappa o "topologia". 

Per altri, l'*architettura* evoca l'idea di uno schema concettuale di soluzione riferito a un certo dominio applicativo, 
come ad esempio nella frase architetture web; in questo caso sarebbe più appropriato utilizzare il termine framework.

L'Open Group Architectural Framework definisce architettura:

- "a set of elements depicted in an architectural model and a specification of how these elements are connected 
  to meet the overall requirements of an information system".

In [BCK03] si dice che:

- "the software architecture of a program or computing system is the structure 
  or structures of the system, which comprises software components, the externally-visible properties 
  of these components and the relationships among them".

La IEEE Computer society definisce (nel 2000) l'architettura:

- "the fundamental organization of a 
  system embodied in its components their relationships to each other and to the environment, 
  and the principles guiding its design and evaluation".

Tra le altre accezioni possibili, una delle più curiose, su cui vale la pena di riflettere, è quella per cui:
 
 - l'architettura è ciò che rimane di un sistema quando non si può più togliere nulla, 
   continuando a comprenderne la struttura e il funzionamento.

Le prime esperienze collettive nello studio delle architetture software possono essere fatte 
risalire al workshop OOPSLA del 1981 guidato da Bruce Anderson che mirava allo sviluppo 
di un "architecture handbook". 
A questo periodo può anche essere fatto risalire l'idea di pattern culminata nella pubblicazione 
nel 1995 dell'ormai famoso testo sui Design Pattern [GHJV95] della così detta 
GoF (Gang-of-Four: Erich Gamma, Richard Helm, Ralph Johnson e John Vlissides). 
Da allora si sono susseguiti molte altre conferenze e lavori. 
I riferimenti più noti sono i cinque testi sulle Patten Software Architectures 
([POSA1], [POSA2], [POSA3], [POSA4], [POSA5] ) e i convegni PLoP (Pattern Languages of Programming).

-------------------------------------
Dimensioni
-------------------------------------


Sia nella fase di analisi che in quella di progetto, la descrizione di un sistema software può avvenire 
focalizzando l'attenzione su almeno tre diversi punti di vista:
- l'organizzazione del sistema in parti (struttura);
- il modo in cui le diverse parti scambiano informazione implicita o esplicita tra loro (interazione);
- il funzionamento del tutto e di ogni singola parte (comportamento).
ù
Questi punti di vista costituiscono tre indispensabili dimensioni in cui articolare lo spazio della descrizione 
del sistema, qualunque sia il linguaggio utilizzato per esprimere questa descrizione.
Costrutti per esprimere strutture (di dati e di controllo), forme di comportamento e meccanismi di interazione 
sono presenti in tutti i linguaggi di programmazione. 
Un punto importante consiste nel capire fino a che punto i costrutti di un linguaggio debbano influenzare 
il progettista (se non lo stesso analista). 
Fino alla fine degli anni 90 il linguaggio di programmazione è stato il veicolo principale per introdurre 
nuovi concetti sia sul piano computazionale sia sul sul piano della organizzazione del software.

L'avvento della programmazione ad oggetti sembra avere segnato il culmine di questo processo; 
un motivo può certo essere il raggiungimento di una sufficiente maturità nella capacità espressiva 
in ciascuna delle dimensioni citate. Tuutavia, il motivo principale della relativa (apparente) 
stagnazione nello sviluppo di nuovi linguaggi può essere ricondotto all'idea che un linguaggio 
non deve essere necessariamente accompagnato da una sintassi concreta ma può essere suffciente 
definire una sintassi astratta utilizzando un meta-linguaggio come ad esempio MOF 
(si veda Meta Object Facility) unitamente alla semantica del linguaggio e a un framework (oo) di supporto.

Questa idea è sviluppata oggi con riferimento ai domain specific languages.


++++++++++++++++++++++++++++++++++++++++++++++
Struttura
++++++++++++++++++++++++++++++++++++++++++++++
Per impostare in modo sistematico la definzione a livello strutturale di un elemento può essere conveniente, 
sia in fase di analisi sia in fase di progetto, cercare di dare risposta ad alcune domande quali:

- l'elemento è atonico o composto? Nel caso sia composto quali sono le parti che lo formano?
- l'elemento è dotato di stato modificabile? In caso affermativo, quali sono le operazioni di modifica dello stato? 
  (si veda la sezione sul comportamento)
- quali sono le proprietà dell'elemento, cioè quali attributi lo caratterizzano ?
- da quali altri elementti dipende e secondo quale forma di dipendenza?

Si noti che un elemento composto implica la definizione ricorsiva della struttura di ogni parte e 
la definizione di operazioni denominate selettori.
Notiamo anche che l'individuazione di una struttura composta porta spesso alla individuazione 
di un insieme di operazioni primitive sulla base delle quali costruire ogni altra operazione 
di manipolazione/gestione dell'elemento.  


++++++++++++++++++++++++++++++++++++++++++++++
Interazione
++++++++++++++++++++++++++++++++++++++++++++++
Le interazioni possono essere sincrone o asincrone e riguardare informazioni o stream di dati. 
In questo secondo caso esse possono essere anche isocrone.

In una interazione asincrona la comunicazione è "bufferizzata" senza alcuna 
limitazione sulle dimensioni del buffer. 
L'emittente non deve attendere alcuna informazione di ritorno anche quando invia informazione 
ad uno specifico destinatario. Il ricevente attende solo quando il buffer è vuoto. 
Nel caso di stream, non vi sono vincoli di tempo per la ricezione.
In una interazione sincrona la comunicazione avviene senza l'uso di alcun buffer. 
L'emittente e il desinatario scambiano informazione unificando concettualmente le proprie attività. 
Nel caso di stream, il destinatario si aspetta di ricevere i dati con un ritardo (delay) 
che non supera un massimo prefissato.
Una interazione isocrona riguarda solo stream; il destinatario si aspetta di ricevere i dati 
con un delay compreso tra un minimo e un massimo.


Per impostare in modo sistematico la dimensione interazione è opportuno chiarire le diverse forme che questa può assumere. 
Nel seguito faremo riferimento alla seguente terminologia:

- Evento (event): informazione emessa (più o meno consapevolmente) in modo asincrono da una sorgente 
  senza alcuna particolare nozione di ricevente e senza alcuna aspettativa da parte dell'emittente.
- Segnale (signal): informazione inviata in modo asincrono a N (N>=1) destinatari, noti o meno all'emittente, 
  con l'aspettativa che venga ricevuta da qualcuno, al fine di eseguire un'azione che potrebbe portare vantaggio 
  all'emittente e/o al sistema nel suo complesso.
- Messaggio (message): informazione inviata in modo asincrono a N (N>=1) specifici destinatari, 
  noti alla emittaente, con l'aspettativa che questi lo ricevano e lo elaborino, 
  senza attesa di una risposta esplicita.
- Invito (invitation): messaggio inviato a N (N>=1) destinatari, con l'aspettativa che almeno uno lo riceva 
  e invii al mittente un messaggio di conferma.
- Richiesta (request): messaggio inviato a uno specifico receiver; il contenuto del messaggio 
  rappresenta la richiesta di esecuzione di una attività, con aspettativa da parte del sender 
  che questa attività si concluda con una risposta pertinente alla richiesta.
- Conferma (reply, acknowledgment): messaggio inviato da un receiver al sender di un invito. 
  Il contenuto del messaggio rappresenta un riconoscimento di avvenuta ricezione.
- Risposta (response): messaggio inviato da un receiver al sender di una richiesta.
  il contenuto del messaggio rappresenta la risposta alla richiesta.
- Risultato (result): messaggio inviato dal receiver di una richiesta ad uno o più destinataori, 
  noti e meno; il contenuto del messaggio rappresenta la risposta alla richiesta.

Le interazioni vengono spesso suddivise secondo quattro pattern [POSA3]; con riferimento alla terminologia precedente; :

- Fire and forget: il caso di invio di eventi, segnali, messaggi.
- Sync with server: il caso di invio di invitation.
- Poll objects: il sender invia una request delegando ad un oggetto (poll object) la responsabilità 
  di ricevere la risposta. Il sender usa il poll object per verificare ed acquisire la disponibilità della risposta.
- Result callback: il sender invia una request specificando un oggetto (callback object) che implementa 
  un metodo che verrà invocato dal supporto non appena il receiver invierà la risposta.



++++++++++++++++++++++++++++++++++++++++++++++
Comportamento
++++++++++++++++++++++++++++++++++++++++++++++


-------------------------------------
Modelli
-------------------------------------


Nel linguaggio comune, il termine modello è spesso usato per denotare un'astrazione 
di qualcosa che esiste nella realtà, come ad esempio il modello che posa per un artista, 
una riproduzione in miniatura, un esempio di modo di svolgere un'attività, una forma 
da cui ricavare vestiti, un ideale da seguire, etc.. 

Alcuni (tra cui gli ingegneri) intendono per modello un sistema matematico o fisico che ubbidisce 
a specifici vincoli e che può essere utilizzato per descrivere e comprendere un sistema 
(fisico, biologico, sociale, etc.) attraverso relazioni di analogia.

Nel contesto dei processi di costruzione del software, il termine modello va primariamente 
inteso come un insieme di concetti e proprietà volti a catturare aspetti essenziali di un sistema, 
collocandosi in un preciso spazio concettuale. 

Per l'ingegnere del software quindi un modello costituisce una visione semplificata di un sistema 
che rende il sistema stesso più accessibile alla comprensione e alla valutazione e facilita 
il trasferimento di informazione e la collaborazione tra persone, 
soprattutto quando è espresso in forma visuale.

Nel concepire un modello come visione semplificata di un sistema software si assume che il sistema 
abbia già una sua esistenza concreta. 
In alcune fasi di lavoro (in particolare nella fase di analisi) il sistema è il modello; 
un raffinamento o una variazione del modello corrisponde in questo caso ad una variazione del sistema.

La produzione esplicita di modelli si rivela utile in quanto i diversi attori di un processo 
di produzione di software (committenti, analisti, progettisti, utenti, etc) 
operano a diversi livelli di astrazione. 
Definendo opportuni modelli del sistema da realizzare, in ogni fase del processo di produzione 
l'attenzione può essere focalizzata sugli aspetti rilevanti in quella fase, utilizzando una 
forma di comunicazione comprensibile ad attori diversi. 
Per garantire coesione e interoperabilità, si cerca di individuare regole di corrispondenza 
e di trasformazione automatica tra modelli 

.. (si veda Architecture model-driven).
