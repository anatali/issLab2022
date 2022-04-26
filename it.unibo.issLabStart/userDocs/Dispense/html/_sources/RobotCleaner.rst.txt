.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo


.. _visione olistica: https://it.wikipedia.org/wiki/Olismo

==============================
RobotCleaner
==============================

-------------------------------------------
RobotCleaner: requisiti
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






-------------------------------------------
RobotCleaner: prototipo dopo l'analisi 
------------------------------------------- 


- aril con  mossa  step (p) che può avere successo o fallire inviando due messggi diversi.