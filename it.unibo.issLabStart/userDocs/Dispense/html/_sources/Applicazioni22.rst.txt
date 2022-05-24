.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _Product Backlog : https://www.scrum.org/resources/what-is-a-product-backlog
.. _SPRINT : https://www.scrum.org/resources/what-is-a-sprint-in-scrum
.. _Agile software development : https://en.wikipedia.org/wiki/Agile_software_development
.. _IOT: https://en.wikipedia.org/wiki/Internet_of_things

.. _unibo.basicrobot22: ../../../../../unibo.basicrobot22
.. _unibo.mapperQak22: ../../../../../unibo.mapperQak22
.. _unibo.cleanerQak22: ../../../../../unibo.cleanerQak22
.. _unibo.pathexecutor: ../../../../../unibo.pathexecutor


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

Per dare esempi di questo modo di procedere,  svilupperemo i segeunti progetti applicativi di 'ispirazione `IOT`_'  :

- Premessa operativa: 
    - si veda :ref:`StartUp (versione Valastro-Marchegiani)`
    - si esperimenti il sistema distribuito :ref:`resourcecore` e :ref:`External caller1` 
      (tratto da :ref:`demorequest.qak`)
 
- `unibo.basicRobot22`_: il progetto è descritto in :ref:`BasicRobot22` e realizza la risorsa di base per le 
  applicazioni che seguono. Il sistema deployed su DockerHub comprende anche un DDR robot virtuale e mostra anche
  l'uso di

    - :ref:`CodedQActors`
    - :ref:`ExternalQActor` 
    - :ref:`Actors as streams`
   
- `unibo.mapperQak22`_: utilizza :ref:`BasicRobot22` e :ref:`UniboPlanner<Uso di un planner>` per creare 
  (e salvare) la mappa di una stanza rettangolare secondo la politica *BoundaryWalker*.
  E' utile confrontare il modello con:

    - :ref:`ActorWithObserverUsingWEnv`
    - :ref:`Un primo automa a stati finiti`
    - :ref:`BoundaryWalkerAnnot`
  
- `unibo.cleanerQak22`_: utilizza :ref:`BasicRobot22` e :ref:`UniboPlanner<Uso di un planner>` per creare 
  (e salvare) la mappa di una stanza rettangolare che contiene **ostacoli fissi**
- `unibo.pathexecutor`_: utilizza :ref:`BasicRobot22` per eseguire la richiesta di esecuzione di un path dato 
  con possibile successo o fallimento. Nel caso di fallimento fornisce il path ancora da compiere