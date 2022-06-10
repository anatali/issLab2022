.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _Actor model: https://en.wikipedia.org/wiki/Actor_model
.. _kotlinUnibo: ../../../../../issLab2022/it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html

.. _Data, Types and Variables: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#data
.. _Functions: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#funs
.. _Using lambda: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#lambda
.. _Closures, Callbacks and CPS: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#clos
.. _Towards Asynchronous Programming: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#async
.. _Introduction to coroutines: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#coroutinesIntro
.. _Dispatchers: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#dispatchers
.. _Suspending functions: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#suspend
.. _Kotlin Channels: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#channels
.. _Producers-consumers in Kotlin: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#kotlinprodcons
.. _Kotlin Actors: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#actors
.. _actorcounter: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#actorcounter
.. _Sequences (suspendable): ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#sequences
.. _Classes and Objects in Kotlin: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#classes 
.. _Kotlin object: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#kotlinObject 
.. _Kotlin class: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#kotlinclass 
.. _Property delegation: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#propdeleg 
.. _Delegated properties: https://kotlinlang.org/docs/delegated-properties.html
.. _Inline Functions: https://www.baeldung.com/kotlin/inline-functions

.. _Kotlin data class: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#dataclass 
.. _Companion object: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#companionobj 
.. _Enum Classes: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#enumclass 
.. _Enum class: https://kotlinlang.org/docs/enum-classes.html
.. _About initializazion: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#ill 
.. _Inheritance: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#inheritance 
.. _Sealed clsss: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html#sealedclass 

.. _Kotlin Multiplatform: https://kotlinlang.org/docs/multiplatform.html#common-code-for-mobile-and-web-applications
.. _Kotlin Worker: https://kotlinlang.org/docs/native-immutability.html#workers
.. _Kotlin Concurrency overview: https://kotlinlang.org/docs/multiplatform-mobile-concurrency-overview.html
.. _Continuation-passing style: https://en.wikipedia.org/wiki/Continuation-passing_style
.. _Chiusure lessicali: https://it.wikipedia.org/wiki/Chiusura_(informatica)
.. _Canali Kotlin: https://kotlinlang.org/docs/channels.html
.. _KotlinChannel: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel/index.html
.. _Attori Kotlin: https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html#actors
.. _Asynchronous I/O: https://en.wikipedia.org/wiki/Asynchronous_I/O
.. _Coroutine video: https://www.youtube.com/watch?v=lmRzRKIsn1g  
.. _Elizarov video: https://www.youtube.com/watch?v=_hfBv0a09Jc:
.. _Coroutines basics: https://kotlinlang.org/docs/coroutines-basics.html#extract-function-refactoring
.. _Deferred values: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/

.. _I/O bound: https://it.wikipedia.org/wiki/I/O_bound
.. _Scala: https://en.wikipedia.org/wiki/Scala_(programming_language)
.. _Android: https://en.wikipedia.org/wiki/Android_(operating_system)
.. _Kotlin wikipedia: https://en.wikipedia.org/wiki/Kotlin_(programming_language)
.. _Kotlin org: https://kotlinlang.org/
.. _Kotlin Playgound: https://play.kotlinlang.org/#eyJ2ZXJzaW9uIjoiMS42LjIxIiwicGxhdGZvcm0iOiJqYXZhIiwiYXJncyI6IiIsIm5vbmVNYXJrZXJzIjp0cnVlLCJ0aGVtZSI6ImlkZWEiLCJjb2RlIjoiLyoqXG4gKiBZb3UgY2FuIGVkaXQsIHJ1biwgYW5kIHNoYXJlIHRoaXMgY29kZS5cbiAqIHBsYXkua290bGlubGFuZy5vcmdcbiAqL1xuZnVuIG1haW4oKSB7XG4gICAgcHJpbnRsbihcIkhlbGxvLCB3b3JsZCEhIVwiKVxufSJ9
.. _Kotlin Online: https://play.kotlinlang.org/#eyJ2ZXJzaW9uIjoiMS42LjIxIiwicGxhdGZvcm0iOiJqYXZhIiwiYXJncyI6IiIsIm5vbmVNYXJrZXJzIjp0cnVlLCJ0aGVtZSI6ImlkZWEiLCJjb2RlIjoiLyoqXG4gKiBZb3UgY2FuIGVkaXQsIHJ1biwgYW5kIHNoYXJlIHRoaXMgY29kZS5cbiAqIHBsYXkua290bGlubGFuZy5vcmdcbiAqL1xuZnVuIG1haW4oKSB7XG4gICAgcHJpbnRsbihcIkhlbGxvLCB3b3JsZCEhIVwiKVxufSJ9
.. _Kotlin Documentation: https://kotlinlang.org/docs/kotlin-pdf.html
.. _Kotlin Learning materials:  https://kotlinlang.org/docs/learning-materials-overview.html
.. _Get started with Kotlin: https://kotlinlang.org/docs/getting-started.html
.. _Kotlin Basic syntax: https://kotlinlang.org/docs/basic-syntax.html#for-loop

.. _Programmazione funzionale: https://it.wikipedia.org/wiki/Programmazione_funzionale


.. _demoBasic.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoBasic.kt
.. _demoFun.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoFun.kt
.. _demoClasses.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoClasses.kt
.. _demoLambda.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoLambda.kt
.. _demoCps.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoCps.kt
.. _demoCpsAsynch.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoCpsAsynch.kt
.. _democoroutinesintro.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/democoroutinesintro.kt
.. _demodispatchers.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demodispatchers.kt
.. _demosequences.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demosequences.kt
.. _demosuspended.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demosuspended.kt
.. _demochannels.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demochannels.kt
.. _prodconskotlin.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/prodCons/prodConsKotlin.kt
.. _prodmanyconskotlin.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/prodCons/prodManyConsKotlin.kt
.. _simpleproducerkotlin.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/prodCons/simpleProducerKotlin.kt
.. _simpleReceiverKotlin.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/prodCons/simpleReceiverKotlin.kt
.. _demoactors.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoactors.kt
.. _demoactorcounter.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoactorcounter.kt
.. _demo2022.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demo2022.kt
.. _demoOOP2022.kt: ../../../../../it.unibo.kotlinIntro/app/src/main/kotlin/kotlindemo/demoOOP2022.kt



.. video 5.44

===============================================
KotlinNotes
===============================================

Kotlin nasce nel 2011, per introdurre un nuovo linguaggio per la JVM, ispirato a `Scala`_, con l'oiettivo di una 
compilazione più efficiente. Nel 2020 Kotlin è diventato il linguaggio più usato per lo svluppo di applicazioni Android.

Il supporto per la programmazione multipiattaforma è uno dei principali vantaggi di Kotlin
(si veda  `Kotlin Multiplatform`_ ). 
Riduce il tempo impiegato per scrivere e mantenere lo stesso codice per piattaforme diverse, 
pur mantenendo la flessibilità ei vantaggi della programmazione nativa.

.. image:: ./_static/img/Kotlin/kotlin-multiplatform.png
   :align: center
   :width: 40%


In rete si trova molto materiale relativo a questo linguaggio, che ne permette uno studio efficace. 
Riportiamo qui, per comodità, alcuni riferimenti:

- `Kotlin wikipedia`_: fornisce notizie storiche e dettagli sul linguaggio
- `Kotlin org`_: il sito ufficiale
- `Kotlin Documentation`_: presenta il file pdf della documentazione
- `Get started with Kotlin`_: il sito con tutto quello che c'è da sapere su Kotlin
- `Kotlin Basic syntax`_: panoramica sui costrutti sintattici di Kotlin (parte di `Get started with Kotlin`_)
- `Kotlin Learning materials`_: panoramica sulle risorse utili per lo studio di Koltin (parte di `Get started with Kotlin`_)
- `Kotlin Online`_: permette di eseguire programmi Kotlin


---------------------------------------
Koltin per ISS-72939
---------------------------------------

E' ovvio che la presentazione e lo studio di Kotlin avrebbe bisogno di un congruo numero di ore.
In relazione al corso di *Ingegneria dei Sitemi software*, ci poniamo due obiettivi principali:

#. comprendere il ruolo degli `Attori Kotlin`_ all'interno della classe :ref:`ActorBasic.kt`   
#. fornire informazioni-base utili per scrivere :ref:`CodedQActors` in Kotlin e frasi Kotlin 
   all'interno dei :ref:`modelli eseguibili QAk<Qak specification template>`.

Il file `kotlinUnibo`_ contiene una introduzione a Kotlin condotta attraverso esempi, 
tenendo conto di un percorso logico che distingue (al solito) una parte 
relativa alla *organizzazione strutturale* dei programmi, una parte relativa alla 
*esecuzione* (concorrente) di attività e una parte relativa alla *interazione* tra attività.

Il file `demo2022.kt`_ contiene una sintesi di esempi che coprono il percorso *funzioni-cps-corouitnes-canali-attori*.

Il file `demoOOP2022.kt`_ contiene una sintesi di esempi inerenti il tema *OOP in Kotlin*.

Riportiamo qui i punti salienti per ottenere i nostri obiettivi, indicando gli indice degli esempi di `demo2022.kt`_:

- elementi essenziali della  sintassi Kotlin : **1**
- *classi ed oggetti* in Kotlin : si veda `demoOOP2022.kt`_
- il supporto Kotlin allo *stile funzionale* (:blue:`chiusure, callbacks e CPS`): **2, 3**
- il supporto Kotlin alla *programmazione asincrona in stile CPS*  **4**
- le :blue:`coroutines` Kotlin come 'thread leggeri' che possono essere sospesi 
  senza bloccare il thread che le esegue: : **5, 6, 7, 8, 9, 10, 11, 12**
- i Kotlin :blue:`channels` come 'code' che consentono *suspending send* e *suspending receive*: **17, 18, 19, 20**
- i Kotlin :blue:`Actors` come supporto al modello degli Attori **21, 22, 23**



---------------------------------------
La parte strutturale
---------------------------------------

+++++++++++++++++++++++
Dati e tipi di dato
+++++++++++++++++++++++

Kotlin intende promuovere l'idea della `Programmazione funzionale`_, per cui :ref:`Le funzioni` sono 
gli elementi di base di  qualsiasi computazione (si veda `Data, Types and Variables`_ ). 

Un :blue:`dato` è concettualmente un valore prodotto da una funzione, il cui tipo viene **inferito**     

      .. list-table::
         :widths: 25,75
         :width: 100%

         * - `demoBasic.kt`_ 
           - `var, val, Type, Any, Unit, NullaleType, Smart/Explicit cast (as operator), ==, ===, Range, ArrayOf, Property`

+++++++++++++++++++++++
Oggetti e classi
+++++++++++++++++++++++

Kotlin supporta *Object Oriented Programming* (**OOP**)  e fornisce funzionalità come astrazione, incapsulamento, ereditarietà
ma in modo diverso da Java  (si veda `Classes and Objects in Kotlin`_). 

      .. list-table::
         :widths: 35,65
         :width: 100%

         * - `demoClasses.kt`_
           - ``SingleCounter`` 
             ``Person``  
             ``PersonILL``   
             ``Expr``  
             ``PersonCo``  
             ``Student``  
             ``Delegate`` 
             ``Color`` 
         
         * - `Kotlin Object`_
           - ``SingleCounter`` 
         
         * - `Kotlin Class`_
           - ``Person`` 

         * - `About initializazion`_
           - ``PersonILL, lateinit, lazy`` 

         * - `Sealed clsss`_
           - ``Expr`` 

         * - `Companion object`_
           - ``PersonCo`` 

         * - `Inheritance`_
           - ``Student`` 

         * - `Property delegation`_
           - ``Delegate``,  see `Delegated properties`_

         * - `Enum classes`_
           - ``Color``,  see `Enum class`_ 



+++++++++++++++++++++++++++++++++++
Le funzioni
+++++++++++++++++++++++++++++++++++

Le funzioni sono *oggetti di prima classe* che hanno un tipo, possono essere assegnati a variabili e 
usati come argomenti di funzioni o come valori di ritorno.  



.. list-table::
   :widths: 25,75
   :width: 100%

   * - `demoFun.kt`_ (**1**)
     - `Top-level fun, One-line fun, FunctionType, Lambda Exp Literal, Anonymous fun` (si veda `Functions`_)
         
   * - `demoLambda.kt`_ (**2**)
     - ``Lambda syntax shortcut``, ``Function reference``, ``let, run, it`` (si veda `Using Lambda`_)

   * - `demoCps.kt`_ (**3**)
     - ``Lexical Closures``, ``Callbacks``, ``Continuation Passing Style`` (**CPS**)  (si veda `Closures, Callbacks and CPS`_)


Una lettura interessante: `Inline Functions`_:

  In this tutorial, first we’re going to enumerate two seemingly unrelated issues about lambdas and generics and then, 
  after introducing :blue:`Inline Functions`, we’ll see how they can address both of those concerns.

-----------------------------------
La parte concorrente
-----------------------------------

#. Il runtime Kotlin/Native **non incoraggia** un classico modello di concorrenza orientato ai thread 
   con blocchi di codice che si escludono a vicenda e variabili condizionali, poiché questo modello 
   è noto per essere soggetto a errori e inaffidabile. 
#. Il runtime Kotlin/Native offre il concetto di :blue:`Worker`: 
   flussi di flusso di controllo eseguiti contemporaneamente con una coda di richiesta associata. 
   I Worker sono molto simili agli attori nell'`Actor Model`_. 

   - un `Kotlin Worker`_ può scambiare oggetti Kotlin con un altro Worker. L'oggetto o è immutabile
     o, se modificabile, è proprietà di un singolo Worker, in modo da garantire un singolo mutator
     ed evitare locking. La proprietà può essere trasferita. Si veda anche `Kotlin Concurrency overview`_.   
#. Le `Chiusure lessicali`_ sono spesso usati come :blue:`callbacks` in programmi con asincronismo `I/O bound`_,
   in accordo allo stile di programmazione **CPS** (`Continuation-passing style`_) .


+++++++++++++++++++++++++++++++++
Asynch
+++++++++++++++++++++++++++++++++

.. list-table::
   :widths: 25,75
   :width: 100%


   * - `demoCpsAsynch.kt`_ (**4**)
     - ``CPS in Asynchronous programming``: `kotlin.concurrent.thread, Single Abstract Method converstoion` (**SAM**) 
         (si veda `Towards Asynchronous Programming`_)


 


+++++++++++++++++++++++++++++++++
Kotlin concurrent overview
+++++++++++++++++++++++++++++++++

#. Per dare supporto alla *programmazione asincrona* (o *non bloccante*), evitando il noto callback hell
   (si veda `Asynchronous I/O`_), Kotlin introduce il meccanismo delle *coroutines*.
#. A coroutine can invoke other functions; it can also :ref:`suspend<Suspending functions>` its behavior (without 
   blocking its running thread).
   
   .. image:: ./_static/img/Kotlin/coroutines0.png
    :align: center
    :width: 40%

#. Interaction among concurrent activities (coroutines) can be supported by :ref:`channels<I canali>` that provide 
   suspensive send/receive operations.

   .. image:: ./_static/img/Kotlin/UsingChannelManyCoroutines.png
    :align: center
    :width: 80%
 

#. Combining a coroutine with a channel to communicate with other coroutines makes the idea 
   of Kotlin :ref:`actor<Gli attori>`.





+++++++++++++++++++++++++++++++++++
Le coroutines
+++++++++++++++++++++++++++++++++++

Una coroutine (si veda `Introduction to coroutines`_) è una *istanza di calcolo sospendibile* 
non vincolata a a nessun thread particolare. 
Può sospendere la sua esecuzione in un thread e riprendere in un altro.

  
Il concetto di coroutine si basa sull'idea di sospendere un calcolo senza bloccare un thread 
(si veda :blue:`suspend function` in `Coroutines basics`_) 
implementato utilizzando (dietro le quinte) una macchina a stati e :ref:`CPS<Le funzioni>` .

   .. image:: ./_static/img/Kotlin/coroutines.png
    :align: center
    :width: 40%


Le coroutines non sono parte del linguaggio e nemmeno della libreria standard; fanno parte di una libreria separata 


      .. list-table::
         :widths: 25,75
         :width: 100%

         * - `demoCoroutinesIntro.kt`_ (**5-16**)
           - `GlocalScope, CoroutineScope, Launch, Join, runBlocking, Dispatchers, manyThreads, manyCoroutines, await`
 
         * - `demoDispatchers.kt`_  
           - `Dispatchers.Default, Dispatchers.IO, newSingleThreadContext, Dispatchers.Unconfined` (si veda `Dispatchers`_)

         * - `demoSequences.kt`_  
           - `Sequences, Suspendable sequences (yield) ``


+++++++++++++++++++++++++++++++++++
Suspending functions
+++++++++++++++++++++++++++++++++++

Una funzione di sospensione è una normale funzione di Kotlin con l'aggiunta del modificatore **suspend**
che indica che la funzione può sospendere l'esecuzione di una coroutine.

.. image:: ./_static/img/Kotlin/coroutineSuspend1.png
   :align: center
   :width: 40%
 
Le *suspending function* possono richiamare qualsiasi altra funzione regolare, ma per sospendere effettivamente 
l'esecuzione, questa deve essere un'altra funzione di sospensione.

      .. list-table::
         :widths: 25,75
         :width: 100%

         * - `demoSuspended.kt`_
           - `Delay,async coroutine builder`  (si veda `Suspending functions`_)


-----------------------------------
La parte interazione
-----------------------------------

I `Deferred values`_ forniscono un modo conveniente per trasferire un singolo valore tra coroutine. 

#. I `Canali Kotlin`_ forniscono un modo per trasferire un flusso di valori (uno :blue:`stream`).

#. Un *attore Kotlin* è una combinazione di una coroutine, lo stato in essa incapsulato/confinato e un canale 
   per comunicare con altre coroutine.

+++++++++++++++++++++++++++++++++++
I canali
+++++++++++++++++++++++++++++++++++

A `KotlinChannel`_ is conceptually very similar to a *BlockingQueue*. 
One key difference is that instead of a blocking put operation it has a *suspending send*, 
and instead of a blocking take operation it has a *suspending receive*. 
They are used for providing and consuming objects from the channel, implemented with a FIFO strategy.

  
   .. list-table::
      :widths: 25,75
      :width: 100%

      * - `demoChannels.kt`_ (**17,18**)
        - `Channel<Int>`  (si veda `Kotlin channels`_) :remark:`Utile anche per Dispatchers`
      *  - `simpleProducerKotlin.kt`_ 
         - `ReceiveChannel<Int>`  (si veda `Producers-consumers in Kotlin`_)
      *  - `prodConsKotlin.kt`_ (**19**)
         - `ManyType producer` (si veda `Producers-consumers in Kotlin`_)
      *  - `prodManyConsKotlin.kt`_  (**20**)
         - `Many consumers` (si veda `Producers-consumers in Kotlin`_)

+++++++++++++++++++++++++++++++++++
ReceiveChannel
+++++++++++++++++++++++++++++++++++

Channels implement the ``SendChannel`` and ``ReceiveChannel`` interfaces.

Dunque un produttore si può pensare come un oggetto computazionale che implementa una ``ReceiveChannel`` interface in modo che un 
consumatore che possiede un riferimento al produttore possa eseguire una ``receive()``.

Per un esempio, si veda 

   .. list-table::
      :widths: 25,75
      :width: 100%

      *  - `simpleProducerKotlin.kt`_ 
         - `ReceiveChannel<Int>`  (si veda `Producers-consumers in Kotlin`_)

Altri esempi:

   .. list-table::
      :widths: 25,75
      :width: 100%

      *  - `prodConsKotlin.kt`_ (**19**)
         - `ManyType producer` (si veda `Producers-consumers in Kotlin`_)
      *  - `prodManyConsKotlin.kt`_  (**20**)
         - `Many consumers` (si veda `Producers-consumers in Kotlin`_)          


+++++++++++++++++++++++++++++++++++
SendChannel
+++++++++++++++++++++++++++++++++++

Un consumatore si può pensare come un oggetto computazionale che implementa una ``SendChannel`` interface in modo che un 
produttore  che possiede un riferimento al produttore possa eseguire una ``send()``.

Per un esempio, si veda 

   .. list-table::
      :widths: 25,75
      :width: 100%

      *  - `simpleReceiverKotlin.kt`_ 
         - `SendChannel<Int>`  (si veda `Producers-consumers in Kotlin`_)




+++++++++++++++++++++++++++++++++++
Gli attori
+++++++++++++++++++++++++++++++++++

Un attore è un oggetto computazionale che implementa una ``SendChannel`` che incapsula una variabile ``channel`` (mailbox)
per rendere più conveniente l'uso della ``receive()``.

.. code::

  scope.actor<String> {
    var msg = channel.receive()
    ... 
    for (v in channel) {
        // Process Messages 
        when (v) {
          ...
        }
    }

Gli `Attori Kotlin`_ promuovono uno stile di programmazione basato su messaggi che supera
la tradizionale interazione di chiamata di procedura senza perdere in prestazioni.

 
      .. list-table::
         :widths: 25,75
         :width: 100%

         * - `demoActors.kt`_ (**21,22**)
           -  (si veda `Kotlin Actors`_)
              
               .. image:: ./_static/img/Kotlin/demoActor0.png
                 :align: center
                 :width: 50% 
               
              
 
         * - `demoActorCounter.kt`_ (**23**)
           - (si veda `actorcounter`_)
             
               .. image:: ./_static/img/Kotlin/demoActorCounter.png
                 :align: center
                 :width: 30% 


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
ActorBasic.kt incapsula un attore
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Si dovrebbe ora comprendere come la classe :ref:`ActorBasic.kt` realizzi in modo 
efficiente il concetto di un ente computazionale dotato di flusso di controllo autonomo, capace di recevere e gestire
messaggi in modo FIFO, sfruttando un attore Kotlin incapsulato:


     .. image::  ./_static/img/Qak/ActorBasic.png
      :align: center 
      :width: 50% 