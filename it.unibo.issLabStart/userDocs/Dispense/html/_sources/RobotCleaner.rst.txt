.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo


.. _visione olistica: https://it.wikipedia.org/wiki/Olismo
.. _Macchina di Moore: https://it.wikipedia.org/wiki/Macchina_di_Moore

==============================
RobotCleaner
==============================



-------------------------------------------
Aprile 2002: requisiti
-------------------------------------------
Muovere il :ref:`VirtualRobot` in modo da coprire tutta la superficie di una stanza vuota.


-------------------------------------------
RobotCleaner: analisi dei requisiti
-------------------------------------------

+++++++++++++++++++++++++++++++
Analisi del testo
+++++++++++++++++++++++++++++++

- Il VirtualRobot (detto brevemente robot) è quello introdotto in :ref:`VirtualRobot`.
- La stanza ha pavimento piano, forma rettangolare ed è delimitata da muri.
- Il robot parte dalla **HOME**, definita come l'angolo formato dai muri denominati ``wallUp`` e  ``wallLeft``.

I requisiti possono essere meglio specificati come segue:
 
+++++++++++++++++++++++++++++++
Requisiti funzionali
+++++++++++++++++++++++++++++++

#. *Copertura*: il robot deve seguire una strategia di movimento che garantisca di 
   esplorare la superficie in modo completo.
#. *VerificaLavoro*: deve essere possibile controllare in modo automatico che la copertura sia stata realizzata.
#. *TipoDelRobot*: il committente ha specificato di essere interessato a un sistema che 
   controlla il :ref:`VirtualRobot`, ma ha anticipato che in futuro vorrà usare un robot reale.
   Al momento però, non siamo obbligati ad affrontare questo requisito.

+++++++++++++++++++++++++++++++
Requisiti non funzionali
+++++++++++++++++++++++++++++++

#. *Proattività*: il robot deve muoversi in modo autonomo fino a compimento del lavoro.
#. *Reattività*: il committente ha prospettato la possibilità che il robot sospenda il lavoro
   in caso di allarmi o altre condizioni (comandi di un operatore, temperatura ambiente elevata, etc.).
   Al momento però, non siamo obbligati ad affrontare requisiti di questo tipo.
#. *Sicurezza*: non vi sono requisiti specifici, se non l'assunzione che vi sia un unico programma di controllo.

-------------------------------------------
RobotCleaner: analisi del problema
-------------------------------------------

Come analisti, poniamo in evidenza i seguenti punti.

+++++++++++++++++++++++++++++++
Il cuore del problema
+++++++++++++++++++++++++++++++

Il problema consiste nel definire un controllore capace di realizzare una strategia di comando sistematica
che permetta di soddisfare i requisiti di *Copertura* e di  *VerificaLavoro*.

+++++++++++++++++++++++++++++++
Strategia di movimento
+++++++++++++++++++++++++++++++

Si possono pensare diverse possibili strategie di movimento sistematico che permettono la verifica.
Ad esempio:

.. list-table:: 
  :widths: 33,33,33
  :width: 100%

  * - .. image::  ./_static/img/VirtualRobot/columnMove.PNG
         :align: center 
         :width: 80%

    - .. image::  ./_static/img/VirtualRobot/spiralmove0.PNG
         :align: center 
         :width: 80%
    
    - .. image::  ./_static/img/VirtualRobot/OndeConcentriche.PNG
         :align: center 
         :width: 80%


 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Movimento per colonne
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nel caso della figura di sinistra, il robot lavora 'per colonne'. In particolare,
procede lungo il muro ``wallLeft``  fino a incontrare ``wallDown``. Qui gira a sinistra,
fa un :blue:`passo laterale` di lunghezza adeguaata, poi gira di nuovo  a sinistra e procede diritto verso il
muro ``wallUp``; quando lo incontra gira a destra fa un :blue:`passo laterale`, rigira verso destra e di nuovo
procede verso ``wallDown``.

*CompletamentoLavoro*:  durante un *passo laterale*, il robot incontrerà di sicuro ``wallRight``: 
come ultima mossa procede lungo 
tale muro fino a che non incontra di nuovo un muro (``wallUp`` o  ``wallDown``) e qui si ferma, 
considerando terminato il lavoro.

*VerificaLavoro*:  consiste nel tenere traccia del numero di passi laterali compiuti lungo il muro 
``wallDown``. Se ogni passo copre una distanza  DR pari alla lunghezza del robot, il numero totale
dei passi da compiere è ``DWallDown/DR + 1``.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Movimento a spirale
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nel caso della figura di centro, il robot procede coprendo aree quadrate di lato ``DR*N``, con ``N=1,2,..,M``.
Questa strategia risulta facile da realizzare per stanza quadrate o quasi, ma è più complicata per 
stanza rettangolari con lati di lunghezza diversa tra loro.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Movimento a onde
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nel caso della figura di destra, il robot segue i bordi della stanza, riducendo via via il lati della stanza di 
DR, lavrando in una 'stanza virtuale' di lati ``DWallDown - N*DR`` e ``DWallLeft - N*DR``, con ``N=1,2,..,M``.

*CompletamentoLavoro*:   quando la stanza virtuale perde una dimensione, il robot deve coprire quallo che rimane.

*VerificaLavoro*: se la parte che rimane ha lato ``L > DR``, può risultare complicato 


:remark:`come analisti, riteniamo adeguata la strategia di lavoro per colonne`

.. memorizzazione del lavoro svolto (del percorso effettuato)

+++++++++++++++++++++++++++++++
Il passo del robot
+++++++++++++++++++++++++++++++

In ogni caso, osserviamo che tutte le strategie di lavoro esaminate si basano su una stessa ipotesi:

:remark:`il robot si muove per passi di lunghezza DR`

La lunghezza **DR** può essere defiita come: 

:remark:`DR=diametro del cerchio di raggio minimo che circoscrive il robot`


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Il robot come unità di misura
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

In pratica, il robot (cioè **DR**) diventa la **unità di misura per le distanze**.
La stanza stessa può essere pensata come suddivisa in celle quadrate di lato DR.

.. image::  ./_static/img/VirtualRobot/plant0.PNG
    :align: center 
    :width: 30% 


+++++++++++++++++++++++++++++++++++++++++++
RobotCleaner: Interazioni
+++++++++++++++++++++++++++++++++++++++++++

Il :ref:`VirtualRobot` può ricevere comandi via HTTP o via WS (WebSocket).
Le prospettive delineate dal committente inducono a selezionare l'uso di :ref:`wsConnection`
e quindi di interazioni asincrone.


+++++++++++++++++++++++++++++++++++++++++++
Linguaggio di Comando: da cril a aril
+++++++++++++++++++++++++++++++++++++++++++

Il :ref:`VirtualRobot` comprende messaggi in :ref:`cril<Comandi-base per il robot in cril>`. Altri robot potrebbero 
richiedere comandi identici concettualmente, ma espressi in una sintassi diversa. 
Può essere opportuno introdurre un linguaggio di comando 'technology-independent', che qui 
denominiamo :blue:`aril` (**Abstract Robot Interaction Lanaguage**).




:worktodo:`WORKTODO: formalizzare l'analisi`

 

-------------------------------------------
Maggio 20022: prototipo dopo l'analisi 
------------------------------------------- 

- Occorre definire un **modello** del sistema che descrive in modo 'formale' (comprensibile/eseguibile da una macchina)  
  il risultato che l'analista intende esporre e fornire al progettista come punto di partenza per lo sviluppo.


++++++++++++++++++++++++++++++++++++
RobotCleaner: Architettura
++++++++++++++++++++++++++++++++++++

Il sistema è formato da un componente proattivo che può essere modellato come un attore che opera in un nodo computazionale.

Utlizzando il (meta)modello :ref:`QakActor22<QakActor22>`, la specifica formale può essere:

.. code:: Java

   @Context22(name = "pcCtx", host = "localhost", port = "8083")
   @Actor22(name = MainActorCleaner.myName, contextName = "pcCtx", implement = RobotCleaner.class)
   public class MainActorCleaner {
      ...
   }

Il componente proattivo che definisce la business logic può essere formalizzato come una attore che opera come una 
`Macchina di Moore`_

++++++++++++++++++++++++++++++++++++
RobotCleaner Behaviour
++++++++++++++++++++++++++++++++++++

Progetto: **unibo.wenvUsage22** code: *unibo.wenvUsage22.cleaner.RobotCleaner*.


- Gli stati dell'automa che opera secondo un :ref:`Movimento per colonne` sono

   ``activate, start, goingDown, turnGoingDown, goingUp, turnGoingUp, lastColumn, completed, endJob``

- L'automa invia (stati ``going..``) al robot comandi di movimento a passi in avanti  come indicato in :ref:`Il passo del robot` e di rotazione, 
  (stati ``turn..``)  definiti nella classe ``VRobotMoves``.

- La *VerificaLavoro* prospettata in :ref:`Movimento per colonne` viene realizzata contando il numero di passi verso ``wallRight`` che deve 
  risultare, nello stato  ``completed `` non inferiore `DWallDown/DR + 1``. La distanza `DWallDown`` può essere misurata facendo muovere l'automa
  per passi lunghi DR da ``wallLeft`` a ``wallRight``. Questi compito può essere svolto da una applicazione ad hoc.

   :worktodo:`WORKTODO: Realizzare una applicazione che calcola DWallDown`

- L'automa comunica con il robot (al momento il :ref:`VirtualRobot`) in modo asincrono (attraverso una :ref:`WsConnection`) 
  e riceve dal supporto il messaggio ``endMoveOk`` oppure ``endMoveKo`` al termina di ogni movimento e rotazione.

- Il supporto che trasforma le informazioni di :ref:`WEnv` in messaggi ``endMoveOk`` o ``endMoveKo`` è realizzato a livello applicativo 
  da un POJO, observer di :ref:`WsConnection`, definito dalla classe ``WsConnApplObserver`` che implementa :ref:IObserver`.

   .. code:: Java

      public class WsConnApplObserver extends WsConnSysObserver implements IObserver{
        ...
        @Override
	      public void update(String data) {
         //data : {"endmove":,,,,"move":"..."}
         //data : {"collision":"...","target":"..."}
         //Genera SystemData.endMoveOk o SystemData.endMoveKo
         }
      }
 
- Le transizioni di stato avvegono in conseguenza della ricezione di un messaggio ``endMoveOk`` oppura ``endMoveKo``

.. image::  ./_static/img/Spring/RobotCleanerFsm.PNG
    :align: center 
    :width: 80% 

Il modello eseguibile è riportato in RobotCleaner.java

.. code:: Java

   public class RobotCleaner extends QakActor22FsmAnnot{

   	@State( name = "activate", initial=true)
	   @Transition( state = "start",   msgId= SystemData.startSysCmdId  )
      protected void activate( IApplMessage msg ) { ... }


      @State( name = "start" )
      @Transition( state = "goingDown",   msgId="endMoveOk"  )
      @Transition( state = "endJob",      msgId="endMoveKo"  )
      protected void start( IApplMessage msg ) { ... }
   }


------------------------------------------------------
RobotCleaner reattivo a comandi
------------------------------------------------------

*RobotCleaner reattivo*: Estendere il funzionamento di il :ref:`RobotClenaer<RobotCleaner Behaviour>` in modo da eseguire i seguenti comandi inviati da un utente.
(umano o macchina):

- start (id = ``SystemData.startSysCmdId``) attiva il robot, che parte dalla posizione HOMe
- stop (id = ``SystemData.stopSysCmdId``): ferma il robot nella posizione corrente
- resume (id = ``SystemData.resumeSysCmdId``): riattiva il robot dalla posizione corrente


Pianificazione del lavoro:

#. Si estende il behavior introddto in :ref:`RobotCleaner Behaviour`, tenendo conto dei nuovi possibili messaggi
#. Si realizza un attore che simula l'operatore umano che invia i comandi
#. Si verifica il funzionamento del nuovo prototipo
#. Si realizza una Web User Interface utlizzando SpringBoot. Per questa parte si veda :ref:`WebApplications con SpringBoot`
   e :ref:`Una WebConsole per il RobotCleaner`