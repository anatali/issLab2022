.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo


.. _UniboPlanner: ../../../../../it.unibo.planner20/userDocs/LabPlanner.html

========================================
Uso di un planner
========================================



La parte proattiva di un robot consiste spesso nella esecuzione di una sequenza di movimenti che portano il robot
in un posizione voluta dello spazio di lavoro.

La sequenza di movimenti può venire 'cablata' nel codice di controllo del robot o può essere 
costruita dinamicamente, per tenere conto dello stato delrobot e dell'ambiente circostante.

Un modo per costruire dinamicamente la sequenza di mosse utile a raggiungere un dato obiettivo è 
l'utilizzo di un pianificatore, come quello introdotto in `UniboPlanner`_

Pensiamo al caso del :ref:`RobotCleaner` che deve interrompere la sua parte proattiva per eseguire 
un comando diverso dal un semplice :ref:`stop&resume<RobotCleaner reattivo a comandi>`, 
quale, ad esempio,  un comando di ``BacktoToHome``.

-------------------------------------------------------
BackToHome: analisi del requisito e del problema
-------------------------------------------------------

Un comando di ritorno in HOME senza ulteriori indicazioni, potrebbe essere eseguito in modo semplice,
avvalendosi del fatto che il robot potrebbe proseguire lungo la direzione corrente fino a incontrare:

- *wallUp* : in questo caso il robot si gira sinistra e percorre il borso superiore
- *wallDown* :  in questo caso il robot si gira a destra e percorre prima il borso inferiore e poi il bordo sinistro.

In altre parole esiste una strategia di soluzione che **non richiede conoscenza** della
posizione corrente del robot e/o il percorso svolto fino a quel punto.

In geenerale però, la :blue:`conoscenza del persorso e della posizione` potrebbe essere importante e necessaria.
Si pensi ad esempio al caso in cui il committente precisi il requisito nel modo che segue:

- **BackToHomeFast**: al comando ``returnToHome``, il robot deve tornare in HOME seguendo il percorso più breve (o, in alternativa,
  con il numero minimo di spostamenti)

Oppure
 
- **BackToHomeClean**: al comando ``returnToHome``, il robot deve tornare in HOME limitando al minimo di ripercorrere il percorso già fatto
  (il pavimento pulito) o limtando.

In generale poi, le applicazioni che richiedono il controllo di un robot mobile (tra cui il ``RobotCleaner``) raramente 
si svolgono in una stanza vuota; di solito sono presenti vari ostacoli fissi all'interno della stanza.
In questo caso, ammesso che tali ostacoli siano stati opportunamente evitati durante la fase di pulizia, 
la strategia di ritono potrebbe complicarsi.

-------------------------------------
Uso di un pianificatore
-------------------------------------

- Progetto: **unibo.planner22** code:*unibo....* jar::blue:`unibo.planner22-1.0.jar`

   Definisce il pianificatore e fornisce la classe utlity ``unibo.kotlin.planner22Util``

- Progetto: **unibo.wenvUsage22** code:*unibo.wenvUsage22.planner.MainPlanner22demo*.  

   Mostra un primo esempio di uso del pianificatore

++++++++++++++++++++++++++++++++
SpiralWalker
++++++++++++++++++++++++++++++++

- Progetto: **unibo.wenvUsage22** code:*unibo.wenvUsage22.spiral.SpiralWalker*.  

   Usa il pianificatore per muovere il VirtualRobot a spirale in una stanza vuota

++++++++++++++++++++++++++++++++
RobotMapperBoundary
++++++++++++++++++++++++++++++++

- Progetto: **unibo.wenvUsage22** code:*unibo.wenvUsage22.mapper.RobotMapperBoundary*.  

   Usa il pianificatore per muovere il VirtualRobot lungo il perimetro di una stanza vuota
   (come il :ref:`BoundaryWalker<BoundaryWalkerAnnot>`)
   creando una mappa della stanza e salva la mappa in un file

++++++++++++++++++++++++++++++++
RobotUsingMap
++++++++++++++++++++++++++++++++

- Progetto: **unibo.wenvUsage22** code:*unibo.wenvUsage22.mapper.RobotUsingMap*.  

   Usa il pianificatore e una mappa già costruita per muovere il VirtualRobot  in una data posizione
   e poi tornare a HOME

 
------------------------------------
RobotMapperBoundary on Web
------------------------------------

Progetto: **webForActors** code:*unibo.Robots.mapper.RobotMapperBoundary*.  

Porta il costruttore della mappa :ref:`RobotMapperBoundary`  
entro la :ref:`WebApplication con SpringBoot` e visualizza la mappa sulla pagina web

- utilizza le features  del pianificatore `UniboPlanner`_ per creare una mappa della stanza 
  come matrice di celle quadrate di lunghezza  pari a una :ref:`unità robotica<Il robot come unità di misura>`.
- ha un  comportamento definito come un :ref:`FSM<Un primo automa a stati finiti>` che utilizza 
  le guardie 'versione Lenzi'
- rende visibile una rappresentazione corrente della mappa  ad ogni cambio di direzione 

Per questa applicazione, il WebServer offre la pagina definita in ``RobotNaiveGui.html``.
 