.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _Product Backlog : https://www.scrum.org/resources/what-is-a-product-backlog
.. _SPRINT : https://www.scrum.org/resources/what-is-a-sprint-in-scrum
.. _Agile software development : https://en.wikipedia.org/wiki/Agile_software_development
.. _IOT: https://en.wikipedia.org/wiki/Internet_of_things

.. _unibo.basicrobot22: ../../../../../unibo.basicrobot22
.. _basicrobot.qak: ../../../../../unibo.basicrobot22/src/basicrobot.qak
.. _basicRobot22.yaml: ../../../../../unibo.basicrobot22/basicRobot22.yaml
.. _basicrobotConfig.json: ../../../../../unibo.basicrobot22/basicrobotConfig.json
.. _stepTimeConfig.json: ../../../../../unibo.basicrobot22/stepTimeConfig.json

.. _unibo.pathexecutor: ../../../../../unibo.pathexecutor
.. _pathexecutor.qak: ../../../../../unibo.pathexecutor/src/pathexecutor.qak

.. _webForActors: ../../../../../webForActors

.. _unibo.mapperQak22: ../../../../../unibo.mapperQak22
.. _mapemptyroom22.qak: ../../../../../unibo.mapperQak22/userDocs/mapemptyroom22.qakt
.. _mapwithobstqak22.qak: ../../../../../unibo.mapperQak22/userDocs/mapwithobstqak22.qakt
.. _unibo.robotappl: ../../../../../unibo.robotappl


.. _NanoRobot: ../../../../../unibo.basicrobot22/userDocs/LabNanoRobot.html
.. _Mbot: ../../../../../unibo.basicrobot22/userDocs/Mbot2020.html

.. _kotlinUnibo: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html

.. _basicrobotqak: ../../../../../unibo.basicrobot22/src/basicrobot.qak 
.. _connQak.sysConnKb.kt: ../../../../../unibo.basicrobot22/resources/connQak/connQak.sysConnKb.kt

.. _virtualrobotSupport: ../../../../../unibo.basicrobot22/resources/robotVirtual/virtualrobotSupport2021.kt
.. _nanoSupport: ../../../../../unibo.basicrobot22/resources/robotNano/nanoSupport.kt
.. _motors: ../../../../../unibo.basicrobot22/resources/robotNano/Motors.c
.. _mbotSupport: ../../../../../unibo.basicrobot22/resources/robotNano/nanoSupport.kt

.. _wssupportAsActorKotlin: ../../../../../it.unibo.kotlinSupports/userDocs/wssupportAsActorKotlin.html
.. _RobotService: ../../../../../it.unibo.kotlinSupports/userDocs/RobotService.html
.. _BasicStepRobotService: ../../../../../it.unibo.kotlinSupports/userDocs/BasicStepRobotService.html
.. _ActorWithKotlinSupport: ../../../../../it.unibo.kotlinSupports/userDocs/ActorWithKotlinSupport.html



=========================================
Applicazioni 2022
=========================================
Il :ref:`QActor (meta)model` è stato introdotto come supporto a *metodi agili di sviluppo* (`Agile software development`_) 
di *sistemi software distribuiti ed eterogenei*.

In particolare, la possibilità di eseguire un :ref:`modello QAk<QActor (meta)model>` di un sistema 
già al terminae della fase di analisi del problema può essere molto utile per:

- un'interazione più produttiva con il cliente al fine di comprendere meglio i requisiti
- definire una :ref:`archittetura logica<L'architettura logica>` di riferimento per il sistema da produrre 
  e per la definizione del Product Goal
- definire un piano di lavoro e impostare il primo  `SPRINT`_ definendone:
  
    - il  `Product Backlog`_  
    - un insieme significativo di test funzionali (piani di testing) già espressi in modo formale come programmi.

.. ``

Per dare esempi di questo modo di procedere,  svilupperemo un insieme di progetti applicativi di 'ispirazione `IOT`_' 
a complessità crescente.

---------------------------------
Premessa operativa
---------------------------------

- si veda :ref:`StartUp (versione Valastro-Marchegiani)`
- si esperimenti il sistema distribuito :ref:`resourcecore` e :ref:`External caller1` 
  (tratto da :ref:`demorequest.qak`)


---------------------------------
unibo.basicRobot22
---------------------------------

:remark:`Goal di basicRobot22`

- Costruire un robot 'astratto' capace di gestire diversi tipi di robot concreti e capace di eseguire 
  :ref:`comandi aril<Linguaggio di Comando: da cril a aril>`

Il progetto `unibo.basicRobot22`_ , descritto in :ref:`BasicRobot22`, propone il modello `basicRobot.qak`_
che definisce l'attore :blue:`basicrobot`, il quale
realizza la **risorsa di base** per tutte le applicazioni che seguono. Questa risorsa utilizza:

    - :ref:`CodedQActors`
    - :ref:`ExternalQActor` 
    - :ref:`Actors as streams`

+++++++++++++++++++++++++++++
pathexec
+++++++++++++++++++++++++++++

Il modello `basicRobot.qak`_ include anche la definizione dell'attore :blue:`pathexec` dapprima definito nel progetto
:ref:`unibo.pathexecutor`.

%%%%%%%%%%%%%%%%%%%%%%%%%%
unibo.pathexecutor
%%%%%%%%%%%%%%%%%%%%%%%%%%

Il progetto `unibo.pathexecutor`_ propone il modello `pathexecutor.qak`_ che definisve
l'attore :blue:`pathexec`, il quale utilizza :ref:`BasicRobot22` 
per eseguire la richiesta di esecuzione 
di un path dato,  con possibile successo o fallimento. Nel caso di fallimento, la risposta
fornisce il path ancora da compiere.

+++++++++++++++++++++++++++++
Attivazione di basicRobot22
+++++++++++++++++++++++++++++

Il *sistema basicrobot* può essere attivato usando `basicRobot22.yaml`_ col comando:

.. code::

  docker-compose -f basicrobot22.yaml  up 
  //per attivare solo wenv:
  docker-compose -f basicrobot22.yaml run --service-ports wenv 

I files di configurazione sono:

  - `basicrobotConfig.json`_
  - `stepTimeConfig.json`_


---------------------------------
webForActors
--------------------------------- 
:remark:`Goal di webForActors`

- Costruire una WebGUI per il robot :ref:`unibo.basicRobot22` capace di visualizzare l'esito dei comandi


Il progetto `webForActors`_, introdotto in :ref:`Primi passi con SpringBoot`,
è stato modificato in modo da definire una GUI per il sistema
:ref:`unibo.basicRobot22`

.. image:: ./_static/img/Robot22/basicRobotCmdGui.png 
   :align: center
   :width: 70%

 
---------------------------------
unibo.mapperQak22
--------------------------------- 

:remark:`Goal di mapperQak22`

- Utilizzare :ref:`unibo.basicRobot22` per creare la mappa della stanza


Il progetto `unibo.mapperQak22`_ contiene due modelli:

    #. `mapemptyroom22.qak`_ : utilizza :ref:`BasicRobot22` e :ref:`UniboPlanner<Uso di un planner>` per creare 
       (e salvare) la mappa di una stanza rettangolare vuota (almeno sui bordi) secondo la politica *BoundaryWalker*.
       E' utile confrontare questo modello con:

        - :ref:`ActorWithObserverUsingWEnv`
        - :ref:`Un primo automa a stati finiti`
        - :ref:`BoundaryWalkerAnnot`
    
    #. `mapwithobstqak22.qak`_ :  utilizza :ref:`BasicRobot22` e :ref:`UniboPlanner<Uso di un planner>` per creare 
       (e salvare) la mappa di una stanza rettangolare che contiene **ostacoli fissi**

---------------------------------
unibo.robotappl
--------------------------------- 

:remark:`Goal di mapperQak22`

- Utilizzare :ref:`unibo.basicRobot22` e :ref:`pathexec` per creare una applicazione. 

Il progetto  `unibo.robotappl`_:  utilizza :ref:`pathexec` per spostare il robot in un dato punto della stanza, 
nota la mappa.