.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _Akka actor model: https://doc.akka.io//docs/akka/current/typed/guide/actors-motivation.html
.. _Hewitt: https://en.wikipedia.org/wiki/Carl_Hewitt#Actor_model
.. _Kotlin: https://kotlinlang.org/
.. _kotlinUnibo: ../../../../../it.unibo.kotlinIntro/userDocs/LabIntroductionToKotlin.html

.. _Eclipse Xtext: https://www.eclipse.org/Xtext/download.html
.. _Qak syntax: ./_static/Qactork.xtext
.. _Uso di Prolog: ./_static/LabQakPrologUsage2021.html
.. _shortcut: ./_static/LabQakPrologUsage2021.html#shortcut
.. _Xtext: https://www.eclipse.org/Xtext/: https://www.eclipse.org/Xtext/
.. _Moore machine: https://it.wikipedia.org/wiki/Macchina_di_Moore
.. _Coroutine context and dispatchers: https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html
.. _FSMKotlin: ./_static/FSMKotlin.html
.. _tuProlog: http://amsacta.unibo.it/5450/7/tuprolog-guide.pdf
.. _PrologUsage: ./_static/LabQakPrologUsage2020.html

.. _Reactive programming: https://en.wikipedia.org/wiki/Reactive_programming
.. _Observer: https://en.wikipedia.org/wiki/Observer_pattern
.. _Iterator: https://en.wikipedia.org/wiki/Iterator_pattern
.. _Functional programming: https://en.wikipedia.org/wiki/Functional_programming

.. _build2022.gradle: ./_static/build2022.gradle

=============================================
QActor (meta)model
=============================================

QActor è il nome dato al linguaggio personalizzato ispirato al `Akka actor model`_ basato, 
a sua volta, sul lavoro di `Hewitt`_ al MIT .

La **Q/q**  nella parola *QActor*, significa "quasi" poiché il linguaggio non è inteso come un linguaggio 
di programmazione generico, ma piuttosto un linguaggio di modellazione per l'analisi e il progetto di applicazioni 
distribuiti e per la definizione di modelli di comportamento di attori 
che si comportano come Finite State Machines.

L'aggiunta di **k** al prefisso (es ``qak``, ``Qak``) significa che stiamo facendo riferimento alla versione 
implementata in `Kotlin`_ (), senza utilizzare alcun supporto Akka (come fatto nella prima versione del linguaggio).

:remark:`Introduzione all'uso di Kotlin`

- si veda anche `kotlinUnibo`_
 
Il linguaggio ``Qak`` è definito utilizzando il framework `Xtext`_  e si basa su un nucleo di concetti 
che compongono il :blue:`metamodello QActor` . Questi concetti possono essere così riassunti:

- A :blue:`QA-System` is a collection of active entities (*QActors*) each working in a 
  computational node (:blue:`Context`).

   .. image::  ./_static/img/Qak/qacontexts.png
      :align: center 
      :width: 40% 

- :blue:`QActors interact` by using :blue:`Messages` of different types (*Dispatch, Request,...*) and *Events*.

- A QActor can deliver information to another QActor (both local or remote) by using a 
  proper operations or by emitting events.

- High-level send-operations do not use low-level references, but only **actor-names**.
   
- Each context owns a set QActors that can interact with components (actors or 'aliens') working on 
  a different node, by means of the following protocols:

  - :blue:`TCP` : on the port specified by the Context
  - :blue:`CoAP` : on the port specified by the Context
  - :blue:`MQTT` : using the broker specified in the ``mqttBroker`` declaration


  
  .. deployed by the it.unibo.issLabStart/resources/plugins

--------------------------------------
The QActor software factory
--------------------------------------

The mapping between the high-level communication actions and a specific protocol 
is done by the QActor-infrastructure with the help of the *Eclipse QActor software factory*. 

   .. image::  ./_static/img/Qak/qakSoftwareFactory.png
      :align: center 
      :width: 70% 


The metamodel is supported by the :blue:`qak-infrastructure` defined in the :blue:`project 
it.unibo.qakactor` and deployed in **it.unibo.qakactor-2.7.jar**.

+++++++++++++++++++++++++++++++++++
System description
+++++++++++++++++++++++++++++++++++

Given a system named ``xxx``, the Qak Software Factory generates a file ``xxx.pl`` that includes a description (written in Prolog) of 
the system. 
Moreover, it generates a utility file ``sysRules.pl`` (see `Uso di Prolog`_) that is used by the QActor-infrastructure
when it needs knowledge about the system.


++++++++++++++++++++++++++++++++++++++
ActorBasic.kt
++++++++++++++++++++++++++++++++++++++


:blue:`ActorBasic.kt` is an abstract class that implements the concept of qakactor as 
a **message-driven** entity that handles messages by delegating the work to the abstract 
the method *actorBody*.

     .. image::  ./_static/img/Qak/ActorBasic.png
      :align: center 
      :width: 50% 

%%%%%%%%%%%%%%%%%%%%%%%%%%%
kactor
%%%%%%%%%%%%%%%%%%%%%%%%%%%

*ActorBasic.kt* includes a Kotlin actor (let us name it as **kactor**) associated to a :blue:`dispatcher` 
defined (see `Coroutine context and dispatchers`_)
according two arguments (**confined** and **iobound**) given in the constructor:

- If **confined=true**, the actor is activated with a *kotlinx.coroutines.newSingleThreadContext*   
  that makes use of just 1 Thread.
- If **confined=false and iobound=true**, the actor is activated with a 
  *kotlinx.coroutines.newFixedThreadPoolContext* with 64 Threads.
- If **confined=false and iobound=false**, the :blue:`default` of type 
  *kotlinx.coroutines.newFixedThreadPoolContext* is selected, that handles as many Threads 
  as the number of CPUs available.

The class *ActorBasic.kt* can be used to define applicative actors working in **message-driven** way:

     .. image::  ./_static/img/Qak/ApplActorBasic.png
      :align: center 
      :width: 50% 



++++++++++++++++++++++++++++++++++++++
ActorBasicFsm.kt
++++++++++++++++++++++++++++++++++++++


- :blue:`ActorBasicFsm.kt` is an abstract class that **extends ActorBasic.kt** by defining 
  the method *actorBody*, so to implement the behavior of a FSM.

     .. image::  ./_static/img/Qak/ApplActorBasicFsm.png
      :align: center 
      :width: 65% 




%%%%%%%%%%%%%%%%%%%%
fsmwork
%%%%%%%%%%%%%%%%%%%%

This class *ActorBasicFsm.kt* is designed according the same principles exposed in 
`FSMKotlin`_. 
The abstract methid actorBody of :ref:`ActorBasic.kt` overcomes the message-driven policy, by calling 
an internal method :blue:`fsmwork` that implements the behavior of a FSM `Moore machine`, as described later, in  
a:ref:`Message handling rules`. 

--------------------------------------
Messages and Events
--------------------------------------


In the QActor metamodel:

- a :blue:`message` is intended as information sent in **asynchronous way** 
  by some source to some specific destination.

  For :blue:`asynchronous` transmission, we intend that the messages can be 'buffered' by the infrastructure, 
  while the 'unbuffered' transmission is said to be :blue:`synchronous`.
  
- an :blue:`event` is intended as information emitted by some source without any explicit destination.

  Events whose identifier start with the prefix **local_** are :blue:`not propagated outside` the context in 
  which they are generated.

+++++++++++++++++++++++++
ApplMessage.kt
+++++++++++++++++++++++++

A  message has type **ApplMessage.kt** (see :ref:`ApplMessage<La classe ApplMessage>`), that requires the `tuProlog`_ library. 
Some help in building and sending messages is given by the class: **MsgUtil.kt**.

++++++++++++++++++++++++++++++++
Message handling rules
++++++++++++++++++++++++++++++++

With reference to a user-defined QAkactor ``qa`` of type ``ActorBasicFsm``, let us call:

- **currentState**: the name the current state of ``qa``;
- **currentMsg**: the msgId of the message that qa is processing;
- **kaq**: the message-queue of the Kotlin :ref:`kactor`;
- **msgQueueStore** the message-queue local ActorBasicFsm ;
- **tset**: the set of messages mentioned in the transition related to the currentState.

.. The messages sent to the actor ``qa``  and events are inserted in the ``kaq``.


The method :ref:`fsmwork` is called in a message-driven way  by the :ref:`kactor` loop , 
while ``qa``  is in **currentState**. 

.. code:: 

	suspend fun fsmwork(applMsg: ApplMessage) {	
		...
	}

Its behavior is:

#. ``qa`` checks for a transition related to ``applMsg``:

   - if it is possible to fire a transition, set ``currentMsg=applMsg``, change the ``currentState`` and goto 2)
   - if no transition can be fired and ``discardMessages=false``, store the message in the ``msgQueueStore``;

#. qa executes the actions of a state.
   When the state actions terminate, if there is an empty-move goto 3) else goto 4);
#. qa executes a empty-move:
   ``set currentMsg=NoMsg=noMsg`` , change the ``currentState``, and goto 2)
#. qa looks at the ``msgQueueStore`` and

  - if a message ``ms`` is found in ``tset`` : call ``fsmwork(ms)``;
  - if no message is found : terminate ``fsmwork`` (the next call will be perfomed by the :ref:`kactor` loop );

++++++++++++++++++++++++++++++++
Message delivery rules
++++++++++++++++++++++++++++++++

- A message sent from actor ``qa`` to a local actor ``qb``, is inserted in the ``kaq`` of the :ref:`kactor` of ``qb``.

- An event raised in some Context, is delivered to all the other known Contexts of the system and to all the 'alien' connected via TCP or via MQTT.


When a message sent from ``qa`` to actor ``qb`` working in a different context (on a different node), 
the Qak-Infrastructure attempts to find the *ipaddress-port* of the receiver context:

- If information about the context of ``qb`` is found and a MQTT broker is specified in the model, 
  the message is sent via MQTT; otherwise it is sent via CoAP.

- If no information about the context of ``qb`` is found, the message to deliver should be a *reply* 
  to a request made by some 'alien'.
  The system first checks for the existence of an active TCP connection with the receiver 
  (the 'alien' made a request via TCP).
  In such a connection is found, the message is sent over it.
  Otherwise, an attempt is made to send the *reply* via MQTT, hoping that the 'alien' was MQTT-connected.






++++++++++++++++++++++++++++++++
Event propagation rules
++++++++++++++++++++++++++++++++

#. The event emitted by a QActor that belongs to a qak-system (``qasys``) is propagated 
   via Context to all the other QActor of ``qasys``.
#. A standalone QActor  that does not use MQTT, does not propagate events to QActors that use it, 
   neither can perceive events emitted by them.
#. An event emiited by an 'alien' component connected to a QActor via TCP will be perceived by 
   the connected QActor only (i.e. it is not propagated to the other Contexts of the system).
#. The event that reaches a Context (since propagated from another Contexts or emitted by an 'alien') 
   is propagated only to the actors internal to that Context.
#. The event emitted by a QActor of a ``qasys`` that uses MQTT are propagated on the specified topic.

--------------------------------------
High-level message-operations
--------------------------------------

The methods that an application designer can use to deliver messages are:

- :blue:`forward` for a Dispatch
- :blue:`request`, :blue:`replyTo`, :blue:`askFor` for a Request
- :blue:`emit` for an Event

:remark:`The QActor metamodel does not define any explicit receive operation.`

In fact, the behavior of a QActor is modeled as a `Moore machine`_ in which state-transitions 
are triggered by messaged and events.





++++++++++++++++++++++++++++++++
Transitions and guards in Qak
++++++++++++++++++++++++++++++++

- A :blue:`Transition` is 'fired' if the related condition (``whenTime, whenEvent, whenMsg, ...``) 
  together with the related guard (if any) is true.

- A :blue:`guard` is expressed as a condition written in user-defined Kotlin code.

 
-------------------------------------------
StartUp (versione Valastro-Marchegiani)
-------------------------------------------

#. Scarica gradle (versione usata qui: :blue:`Gradle 7.4.2`)
#. Scarica `Eclipse Xtext`_   (versione usata qui: :blue:`Eclipse  2022-03 (4.23.0)`)

   - Java compiler compliance level:  :blue:`11`
   - Installed JRE: :blue:`jdk 11.08 (default)`

#. Copia nella directory **dropins** di Eclipse i file che costituiscono il supporto al metamodello-qak: 

    - :blue:`it.unibo.Qactork_1.2.5.jar`
    - :blue:`it.unibo.Qactork.ui_1.2.5.jar`
    - :blue:`it.unibo.Qactork.ide_1.2.5.jar`

+++++++++++++++++++++++++++++++++++++++
Creazione di un nuovo progetto
+++++++++++++++++++++++++++++++++++++++

#. In **un'area di lavoro vuota**, crea un nuovo progetto Java  utilizzando 
  
   .. code::

     gradle init
        selezionare 1, 1, default, default
#. Copiare nell'area di lavoro la directory :blue:`unibolibs` con le librerie 
#. Importare il progetto in Eclipse come **Existing Gradle project**
#. Aggiungere la **natura Java** al progetto
#. Aggiungere due **source folder**: di nome :blue:`src` e di nome  :blue:`resources`
#. Creare in **src** un file :blue:`qak` e aggiungere la **natura Xtext**  
  
   A questo punto Eclipse dovrebbe presentare una finestra come la seguente:
   
   .. image::  ./_static/img/Qak/qakStarting.png
      :align: center 
      :width: 50% 
#. Scrivere il contenuto del file **qak** e salvare. I plugin *Qactork*: 

   - creano il file  `build2022.gradle`_
   - creano i files ``sysRules.pl`` e ``sysXXX.pl`` essendo ``sysXXX`` il nome del **System** nel modello


#. Copiare il contenuto del  file `build2022.gradle`_ nel file :blue:`build.gradle`
   ( o eliminare questo e ridenomicare il precedente. Il file `build2022.gradle`_ verrà rigenerato al prossimo
   salvataggio del modello)
#. Inserire codice Kotlin di utilità usato nel modello entro la directory :blue:`resources`
#. Eseguire:

   .. code::
    
      gradlew run
  


Ricordamo che:

  :remark:`Un file qak include la definizione (testuale) di un modello`

  - che definisce :blue:`struttura, interazione e comportamento` di un sistema distribuito.

+++++++++++++++++++++++++++++++++++
Qak specification template
+++++++++++++++++++++++++++++++++++
Un modello Qak viene definito organizzando la sua descrizione in base al seguente template:

.. code:: 

  System < NAME OF THE SYSTEM >
  //mqttBroker "broker.hivemq.com" : 1883 //OPTIONAL 

  //DECLARATION OF MESSAGES AND EVENTS

  //DECLARATION OF CONTEXTS
  Context CTXNAME ip [host="HOSTIP" port=PORTNUM]

  //DECLARATION OF ACTORS

+++++++++++++++++++++++++++++++++++
The Qak syntax
+++++++++++++++++++++++++++++++++++

The syntax of the language is defined in `Qak syntax`_) using the `Xtext`_ framework. Riportiamo alcuni esempi.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Dichiarazione dei messaggi
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

I diversi tipi di messaggio sono dichiarati usando una *sintassi* Prolog-like (si veda `tuProlog`_ ):

.. code::

  Event:    "Event"     name=ID  ":" msg = PHead  ;
  Dispatch: "Dispatch"  name=ID  ":" msg = PHead  ;
  Request:  "Request"   name=ID  ":" msg = PHead  ;
  Reply:    "Reply"     name=ID  ":" msg = PHead  ;

  PHead :	PAtom | PStruct	| PStructRef ;
  ...


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Operazioni di invio-messaggi
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Le operazioni di invio messaggio sono le seguenti:

.. code::

  Forward   : "forward" dest=[QActorDeclaration] 
                        "-m" msgref=[Dispatch] ":" val = PHead ;
  Emit      : "emit" msgref=[Event] ":" val = PHead	;
  Demand    : "request" dest=[QActorDeclaration] 
                        "-m" msgref=[Request]  ":" val = PHead ;
  Answer    : "replyTo" reqref=[Request]  
                        "with"    msgref=[Reply]   ":" val = PHead ;
  ReplyReq  : "askFor"  reqref=[Request]  
                        "request" msgref=[Request] ":" val = PHead ;


Vediamo ora alcuni esempi di uso del linguaggio.

Progetto: **unibo.introQak22** code: *src/….*

++++++++++++++++++++++++++++++++
QAk: primi esempi 
++++++++++++++++++++++++++++++++

Il linguaggio Qak mira a esprimere modelli eseguibili, ma   
**non è completo dal punto di vista computazionale**.  Dunque, parte del comportamento potrebbe talvolta 
dover essere espresso direttamente in Kotlin. Ma occorre non  esagerare l'uso di una tale possibilità.


%%%%%%%%%%%%%%%%%%%%%%%%
demonottodo.qak
%%%%%%%%%%%%%%%%%%%%%%%%



Questo esempio definisce un attore che, una volta attivato, calcola il numero di Fibonacci di posizione ``7``
usando codice Kotlin.

.. code::  

  System demonottodo
  Context ctxdemonottodo ip [host="localhost" port=8055]

  QActor demonottodo context ctxdemonottodo{
    State s0 initial { 	 
    [#
      fun fibo(n: Int) : Int {
        if( n < 0 ) throw Exception("fibo argument must be >0")
        if( n == 0 || n==1 ) return n
        return fibo(n-1)+fibo(n-2)
      }
      println("---------------------------------------------------- ")
      println("This actor definies its activity completelyin Kotlin")	
      val n = 7
      val v = fibo(n)	
      println("fibo($n)=$v")
      println("----------------------------------------------------- ")
    #]
    }   
  }

Quando questo file viene salvato, la Qak Software Factory genera il file ``demonottodo.pl``:

.. code::  

  context(ctxdemonottodo, "localhost",  "TCP", "8055").
  qactor( demonottodo, ctxdemonottodo, "it.unibo.demonottodo.Demonottodo").

%%%%%%%%%%%%%%%%%%%%%%%%
demobetter.qak
%%%%%%%%%%%%%%%%%%%%%%%%

Per limitare l'uso diretto di codice Kotlin, è opportuno introdurre classi di utilità e invocarne i metodi.

.. code::  

  System demobetter
  Context ctxdemobetter ip [host="localhost" port=8055]

  QActor demobetter context ctxdemobetter{
    [# var n = 7  #] //Global variable 
    State s0 initial { 	 
      [#  ut.outMsg( "fibo($n)=" + ut.fibo(n))    #]
    }   
  }

La utility **ut** potrebbe essere codice scritto in Java o in Kotlin. Se viene definita nel progetto in corso (ad esempio
in una directory :blue:`resource`) è bene sia scritta in Kotlin. Ad esempio:

.. code::  kotlin
 
  object ut {    
    fun fibo(n: Int) : Int {
      if( n < 0 ) throw Exception("fibo argument must be >0")
      if( n == 0 || n==1 ) return n
      var v = fibo(n-1)+fibo(n-2)
      return v
    }   
    fun outMsg( m: String ){
      ColorsOut.outappl(m, ColorsOut.GREEN);
    }
  }  


:remark:`Per usare codice Java, fare ricorso a file jar`

+++++++++++++++++++++++++
run
+++++++++++++++++++++++++

L'operazione built-in ``run ccc.xxx()``  invoca  il metodo *static* ``xxx`` della classe ``ccc``.

+++++++++++++++++++++++++
qrun
+++++++++++++++++++++++++

L'operazione built-in ``qrun ccc.xxx()``  invoca  il metodo *static*  ``xxx`` della classe ``ccc``. Il 
metodo deve avere come primo argomento un riferimento all'attore corrente (ad esempio **myself**).



--------------------------------------
CodedQActors
--------------------------------------

La :ref:`Qak factory<The QActor software factory>` introduce un editor guidato dalla sintassi che facilita la scrittura di modelli
in linguaggio QAk. 
Questa facilitazione è utile soprattutto quando i modelli sono un *risultato della analisi del problema* 
(o anche, in qualche caso, dei requisiti).

In altre situazioni però, non è escluso che sia preferibile introdurre attori scritti direttamente in Kotlin (o in Java)
ed utlizzarli come una sorta di componenti predefiniti in modelli descritti in linguaggio QAk.

+++++++++++++++++++++++++++++
Sonar come CodedQActor
+++++++++++++++++++++++++++++

Si consideri ad esempio un sistema che deve utilizzare dati prodotti da un sonar ed è quindi logicamente composto
da due componenti:

- un componente (``sonarDataGen``) che gestisce il Sonar, rendendo disponibili i dati che questo genera 
- un componente (``datahandler``) interessato ai dati prodotti dal sonar

Entrambi questi componenti possono essere modellati come attori che interagiscono tramite **eventi**.

.. image::  ./_static/img/Qak/sonarDataGen.png
      :align: center 
      :width: 50% 


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Modello logico
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Un modello QAk del sistema può essere espresso come segue:

.. code::

  System /*-trace*/ democodedqactor 
  Dispatch start   : start( ARG )
  Event sonarrobot : sonar( DATA ) 
  
  Context ctxdemocodedqactor ip [host="localhost" port=8065]

    CodedQActor sonargen  context ctxdemocodedqactor 
                          className "codedActor.sonarDataGen"    
    QActor datahandler context ctxdemocodedqactor{ ...   }  
  }

In questo modello, il sonar è introdotto come un :ref:`CodedQActor` di cui si specifica la classe che lo realizza.

+++++++++++++++++++++++++++++
CodedQActor
+++++++++++++++++++++++++++++

- A CodedQActor is an actor completely written in Kotlin that can be included in a QAk-model by specifying its class name. 

- A CodedQActor is usually defined as a specilization of ActorBasic:

  .. code::

    class qacoded (name : String ) : ActorBasic( name ) {
      override suspend fun actorBody(msg: ApplMessage) {
        //...
      }
    }

- The QA-infrastructure handles a CodedQActor as a usual; in particular,  
  it 'injects' into the CodedQActor the context specified by the model.


+++++++++++++++++++++++++++++
sonarDataGen.kt
+++++++++++++++++++++++++++++

Come sempre, nelle fasi preliminari della costruzione di un sistema può essere opportuno introdurre un 
:ref:`CodedQActor` che realizza la simulazione di un Sonar, focalizzando l'attenzione sulla informazione 
emessa come evento.

.. code::

    class sonarDataGen ( name : String ) : ActorBasic( name ) {
      
      val data = sequence<Int>{
        var v0 = 20
        yield(v0)
        while(true){
          v0 = v0 - 1
          yield( v0 )
        }
      }
        
      override suspend fun actorBody(msg : IApplMessage){
        println("$tt $name | received  $msg "  )
        if( msg.msgId() == "start") startDataReadSimulation(   )
      }
        
      suspend fun startDataReadSimulation(    ){
            var i = 0
          while( i < 10 ){
            val m1 = "sonar( ${data.elementAt(i*2)} )"
            i++
            val event = MsgUtil.buildEvent( name,"sonarrobot",m1)								
            //println("$tt $name | emits $event")
            this.emit( event )
            delay( 300 )
            }			
          terminate()
      }
    } 

:worktodo:`WORKTODO: realizzare sonarDataGen per un sonar reale HC-SR04`


:remark:`Codice vs. modelli`

- notiamo che il ``sonarDataGen`` non esprime in modo evidente il tipo di informazione che emette come evento.
  Si tratta infatti di codice vero e proprio e *non di un modello* che intende catturare aspetti essenziali.
- leggendo il codice capiamo che l'evento emesso ha la forma ``sonarrobot:sonar(DATA)`` e che corrisponde quindi 
  a una dichiarazione QAk quale quella introdotta in :ref:`Modello logico`:

  .. code:: 

    Event sonarrobot : sonar( DATA )

+++++++++++++++++++++++++++++
datahandler 
+++++++++++++++++++++++++++++

Il modello che esprime la logica di funzionamento del gestore dei dati emessi dal sonar è un attore che 
attiva il sonar inviando il dispatch start e poi attende gli eventi, terminando se non giunge nulla entro 
1 sec (si veda :ref:`whenTime`).

.. code::

     QActor datahandler context ctxdemocodedqactor{
      State s0 initial { 	  
        printCurrentMessage
        forward sonargen -m start : start(do)
      }   
      Transition t0 whenEvent sonarrobot -> handleSonarevent
      State handleSonarevent { 
        printCurrentMessage
      }
      Transition t0 whenTime 1000 -> end 
                     whenEvent sonarrobot -> handleSonarevent
      State end{
        println("BYE")
      }
    }  



 

--------------------------------------
QAk: un esempio più articolato
--------------------------------------

Questo esempio descrive un attore che realizza l'automa rappresentato nella figura che segue:

.. image::  ./_static/img/Qak/demoDSL.png
    :align: center 
    :width: 50% 


++++++++++++++++++++++++++++++++
demo0.qak
++++++++++++++++++++++++++++++++

In accordo alla  `Qak syntax`_, la descrizione del modello inizia con la :ref:`Dichiarazione dei messaggi`
e del Contesto:

.. code::

    System demo0    
      Dispatch msg1 : msg1(ARG)
      Dispatch msg2 : msg2(ARG)  
      Event alarm   : alarm( KIND )    

    Context ctxdemo0 ip[host="localhost" port=8095]

Successivamente vengono definiti gli attori 

.. code::

    QActor demo0 context ctxdemo0{ 
    }

    QActor sender context ctxdemo0{
      ...
    }
    QActor perceiver context ctxdemo0{
      ...
    }

- ``demo0`` : definisce il modello eseguibile del  :ref:`diagramma<demo0.qak>`
- ``sender``: attore che invia i messaggi gestiti da ``demo0`` e genera (opzionalmente) un evento
- ``perceiver``: attore che gestisce gli eventi emessi da ``sender``

%%%%%%%%%%%%%%%%%%%%%%%%%
QActor demo0
%%%%%%%%%%%%%%%%%%%%%%%%%

.. code::

    QActor demo0 context ctxdemo0{ 
      State s0 initial { 	    
        discardMsg Off  //discardMsg On
        //[# sysUtil.logMsgs=true #]
      }     
      Goto s1  	
      State s1{
        printCurrentMessage
      }
      Transition t0 whenMsg msg1 -> s2
                    whenMsg msg2 -> s3 
      State s2{ 
        printCurrentMessage
        onMsg( msg1:msg1(ARG) ){
          println("s2: msg1(${payloadArg(0)})")
          delay 1000  
        }
      }
      Transition t0 whenMsg msg2 -> s3
      State s3{ 
        printCurrentMessage 
        onMsg( msg2:msg2(1) ){ 
          println("s3: msg2(${payloadArg(0)})")
        } 
      }
      Goto s1      
    }

L'attore ``demo0`` mostra l'uso di: 

- :blue:`discardMsg On/Off`: seleziondando ``discardMsg Off`` i messaggi che non sono di interesse 
  in un certo stato vengono conservati, mentre con ``discardMsg On``, essi vengono eliminati.
- :blue:`sysUtil.logMsgs`: crea dei file di log dei messaggi ricevuti  
- :blue:`onMsg( msg:msg(ARG1, ARG2, ...) ){ ... }`` : esegue il body solo se il *messaggio corrente*  
  ha identificatore ``msg`` e se il suo payload 
  può essere **unificato in Prolog** con il template di messaggio definito nella dichiarazione **e**
  con il template specificato in *onMsg*.
- :blue:`payloadArg(N)`: si veda `shortcut`_  (in `Uso di Prolog`_)

 
%%%%%%%%%%%%%%%%%%%%%%%%%
QActor sender
%%%%%%%%%%%%%%%%%%%%%%%%%

Il ``sender`` invia alcuni messaggi e genera un evento se ``emitEvents = true``

.. code::

  QActor sender context ctxdemo0{
  [# var emitEvents = false #]
    State s0 initial { 	
      forward demo0 -m msg1 : msg1(1)
      delay 300
      forward demo0 -m msg1 : msg1(2)
      delay 300
      forward demo0 -m msg2 : msg2(1)		  
      if [# emitEvents #] { 
        emit alarm : alarm( fire )  
      } 
    }    
  }  

%%%%%%%%%%%%%%%%%%%%%%%%%
QActor perceiver
%%%%%%%%%%%%%%%%%%%%%%%%%

Il ``perceiver`` gestisce l'evento ``alarm`` e poi ne attende un altro per un tempo (``timeout``) prefissato .
Se il ``timeout`` scade, l'attore transita nello stato finale.

.. code::

    QActor perceiver context ctxdemo0{
      State s0 initial { 	
        println("perceiver waits ..")
      }
      Transition t0 whenEvent alarm -> s1          
      State s1{
        printCurrentMessage
      }
      Transition t0 whenTime 100 -> s2  
                    whenEvent alarm -> s1    
      State s2{
        printCurrentMessage
        println("BYE")
      }
    } 


%%%%%%%%%%%%%%%%%%%%%%%%%
whenTime
%%%%%%%%%%%%%%%%%%%%%%%%%

Questo esempio evidenzia che:

 - un attore non deve rimanare in attesa perenne di messaggi, in quanto può fare una empty-move 
   dopo un certo tempo (**timeOut**) 
 - lo scadere del *timeOut* provoca l'emissione di un evento, con indentificatore  
   ``local_tout_aaa_sss`` ove ``aaa`` è il nome dell'attore e ``sss`` è  il nome dello stato corrente


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Analisi dei risultati
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


:blue:`Output con discardMsg On` 


.. code::

  //Caso sender: emitEvents = false
  perceiver waits ..
  demo0 in s1 | msg(local_noMsg,event,demo0,none,noMsg,4)
  demo0 in s2 | msg(msg1,dispatch,sender,demo0,msg1(1),10)
  s2:msg1:msg1(1)
  demo0 in s3 | msg(msg2,dispatch,sender,demo0,msg2(1),12)
  s3:msg2:msg2(1)
  demo0 in s1 | msg(local_noMsg,event,demo0,none,noMsg,4)

Questo caso evidenzia anche che:

 - una empty-move è realizzata con emissione di un evento ``local_noMsg`` 
 - una empty-move non crea indicazioni sui messaggi da elaborare: i messaggi in arrivo 
   sono memorizzati in :ref:`msgQueueStore<Message handling rules>`

.. code::

  //Caso sender: emitEvents = true
  perceiver waits ..
  demo0 in s1 | msg(local_noMsg,event,demo0,none,noMsg,4)
  demo0 in s2 | msg(msg1,dispatch,sender,demo0,msg1(1),10)
  s2:msg1:msg1(1)
  perceiver in s1 | msg(alarm,event,sender,none,alarm(fire),13)
  perceiver in s3 | msg(local_tout_perceiver_s1,event,timer,none,local_tout_perceiver_s1,14)
  BYE
  demo0 in s3 | msg(msg2,dispatch,sender,demo0,msg2(1),12)
  s3:msg2:msg2(1)
  demo0 in s1 | msg(local_noMsg,event,demo0,none,noMsg,4)


Questo esempio evidenzia che:

  - lo scadere del *timeOut* provoca l'emissione di un evento, con indentificatore  ``local_tout_perceiver_s1``.


:blue:`Output con discardMsg Off` 

.. code::

   //Caso sender: emitEvents = false
  perceiver waits ..
  demo0 in s1 | msg(local_noMsg,event,demo0,none,noMsg,4)
  demo0 in s2 | msg(msg1,dispatch,sender,demo0,msg1(1),10)
  s2:msg1:msg1(1)
  %%%  ActorBasicFsm demo0 |  
     added msg(msg1,dispatch,sender,demo0,msg1(2),11) in msgQueueStore
  demo0 in s3 | msg(msg2,dispatch,sender,demo0,msg2(1),12)
  s3:msg2:msg2(1)
  demo0 in s1 | msg(local_noMsg,event,demo0,none,noMsg,4)
  demo0 in s2 | msg(msg1,dispatch,sender,demo0,msg1(2),11)
  s2:msg1:msg1(2)

-------------------------
demorequest.qak
-------------------------

Un esempio di attere (``caller``) che invia una request (``r1`` con payload *hello(world)*) 
a un altro attore locale (``called``) 
e poi attende il messaggio di reply  (``a1``) che avrà payload *called_caller_hello(world)*.

.. image::  ./_static/img/Qak/demorequest.png
    :align: center 
    :width: 60% 

.. code::

    System  /* -trace */ -msglog demorequest 
    //mqttBroker "broker.hivemq.com" : 1883

    Request r1 : r1(X)     
    Reply   a1 : a1(X)    //answer

    Context ctxdemoreq ip [host="localhost" port=8010]    
    
    QActor caller context ctxdemoreq {
      State init initial {
        //[# sysUtil.logMsgs = true #]
        println("	callera1 starts")
        request called -m r1 : r1(10)
      }
      Goto work 
      
      State work{
          //printCurrentMessage
      }
      Transition t0 whenReply a1 -> handleReply
      
      State handleReply{
        printCurrentMessage
      }
      Goto work	
    }
    
    QActor called context ctxdemoreq {    
      State init initial {
        println("called waits ...") 
      }
      Transition t0  
        whenRequest r1 -> handleRequest
    
      State handleRequest{  
        printCurrentMessage		 
        onMsg( r1 : r1(X) ){  
          [# val Answer = "${currentMsg.msgSender()}_${payloadArg(0)}" #]
          replyTo r1 with a1 : a1( $Answer )  
        } 
      } 
      Goto init   
    }   


-------------------------
demorequest_a.qak
-------------------------

Un esempio di due attori che inviano richieste dello stesso tipo, ma con payload diverso ad un terzo attore,
il quale invia la risposta all'attore chiamante.

.. image::  ./_static/img/Qak/demorequest_a.png
    :align: center 
    :width: 60% 

Il sistema viene inizialmente eseguito in un unico contesto, in modo da verificare la corretteza delle business logic in una 
configurazione 'di testing confortevole'.

.. code::

    System  /* -trace */ -msglog demorequest_a
    //mqttBroker "broker.hivemq.com" : 1883

    Request r1 : r1(X) 
    Reply   a1 : a1(X)    //answer 

    //for first run (debug)
    Context ctxddemorequest_a ip [host="localhost" port=8010]   
    //Context ctxcallers ip [host="localhost" port=8072]     (1)
    //Context ctxcalled  ip [host="127.0.0.1" port=8074]    (2)

Il sistema viene inizialmente configurato con tutti i componenti in un unico contesto, in modo da verificare la corretteza delle business logic in una 
configurazione 'di testing confortevole'.

I componenti (attori) che inviano richieste sono strutturati nel medesimo modo:

.. code::

    QActor callerqa1 context ctxddemorequest_a {
      State init initial {
        println("	callera1 starts")
        request qacalled -m r1 : r1(data(10))
        delay 500 
        request qacalled -m r1 : r1(data(20))
      }
      Goto work 
      
      State work{
      }
      Transition t0 whenReply   a1 -> handleReply
      
      State handleReply{
        printCurrentMessage
      }
      Goto work	
    } 


    QActor callerqa2 context ctxddemorequest_a {
      State init initial {
        println("	callera2 starts")
        request qacalled -m r1 : r1(val(100))
        delay 500
        request qacalled -m r1 : r1(val(200))
      }
      Goto work 
      
      State work{
      }
      Transition t0 whenReply   a1 -> handleReply
      
      State handleReply{
        printCurrentMessage
      }
      Goto work	
    } 

Il componente (attore) che riceve le richieste, invia una risposta 'eco' preceduta dal nome del mittente:

.. code::

    QActor qacalled context ctxddemorequest_a {
    
      State init initial {
      }
      Transition t0  whenRequest r1 -> handleRequest
    
      State handleRequest{  
        printCurrentMessage		 
        onMsg( r1 : r1(X) ){  
          [# val Answer = "${currentMsg.msgSender()}_${payloadArg(0)}" 
            ut.outMsg( "${getName()} | answer=$Answer" )
          #]
          replyTo r1 with a1 : a1( $Answer )  
        } 
      } 
      Goto init   
    }    


++++++++++++++++++++++++++++++++++
demorequest_a distribuito
++++++++++++++++++++++++++++++++++

Una volta verificato che il sistema *funziona in locale* come ci si aspetta, si possono allocare i tre componenti su  nodi
computazionali diversi. Ad esempio, per la configurazione di figura:

.. image::  ./_static/img/Qak/demorequest_aDistr1.png
    :align: center 
    :width: 40% 

la nuova specifica (del modello) del sistema diventa:

.. code::

    System  /* -trace */ -msglog demorequest_a
    Request r1 : r1(X) 
    Reply   a1 : a1(X)    //answer 

    Context ctxcallers ip [host="localhost" port=8072]     (1)
    Context ctxcalled ip [host="127.0.0.1" port=8074]    (2)

    QActor callerqa1 context ctxcallers { ... }
    QActor callerqa2 context ctxcallers { ... }
    QActor qacalled context ctxcalled { ... }

Una volta salavto il modello, viene generata la seguente descrizione del sistema:

.. code::

  context(ctxcallers, "localhost",  "TCP", "8072").
  context(ctxcalled, "127.0.0.1",  "TCP", "8074").
  qactor( callerqa1, ctxcallers, "it.unibo.callerqa1.Callerqa1").
    qactor( callerqa2, ctxcallers, "it.unibo.callerqa2.Callerqa2").
    qactor( qacalled, ctxcalled, "it.unibo.qacalled.Qacalled").
  msglogging.



Per eseguire il sistema è ora necessario invocare due diversi Main:

- **it.unibo.ctxcalled.MainCtxcalledKt** che attiva gli attori allocati sul contesto ctxcalled di ``host=127.0.0.1`` 
  (l'attore *qacalled*)
- **it.unibo.ctxcallers.MainCtxcallersKt** che attiva gli attori allocati sul contesto ctxcalled di 
  ``host=localhost`` (*callerqa1* e *callerqa2*)

Si deve ovviamente verificare che le risposte del sistema **non devono cambiare dal punto di vista logico**.

:remark:`Start-up di un sistema distribuito` 

- Si noti che nessun attore viene attivato prima che siano stati eseguiti **tutti i Main**. Ciò in quanto un sistema 
  vine concepito come è un 'organismo' che funziona solo quando tutte le sue parti sono state create, anche se si trovano su nodi 
  computazionali diversi

:worktodo:`WORKTODO: Altre configurazioni`

- Si provi ad attivare il sistema in altre configurazioni distribuite, come ad esempio:

.. list-table:: 
  :widths: 50,50
  :width: 100%
  
  * - 
       .. image::  ./_static/img/Qak/demorequest_aDistr2.png
           :align: center 
           :width: 80% 
    -        
         .. image::  ./_static/img/Qak/demorequest_aDistr3.png
           :align: center 
           :width: 80% 

------------------------------------
ExternalQActor 
------------------------------------

Nell'esempio :ref:`demorequest_a distribuito` abbiamo concepito un sistema software come un organismo che comincia ad
operare solo quando tutte le sue parti sono state costruite ed attivate.

Vi sono però situazioni in cui un sistema si configura e si costruisce 'incrementalmente', partendo da un nucleo iniziale 
e poi aggiungendo parti (che interagiscono con il nucleo e tra loro sempre mediante scambio di messaggi).

**Progetto**: *it.unibo.resourcecore* 

+++++++++++++++++++++++++++++
resourcecore
+++++++++++++++++++++++++++++

Supponiamo ad esempio di introdurre come 'nucleo di base' una risorsa modellata come segue.

.. code::

    System resourcecore
    Request cmd 		: cmd(X) // X =  w | s | a | d | h
    Reply   replytocmd  : replytocmd(X)
    Event    alarm      : alarm(V)

    Context ctxresourcecore ip [host="localhost" port=8045]
    
    QActor resourcecore context ctxresourcecore{
      State s0 initial { 	  
          println("resourcecore READY")     
        }   
      Transition t0  
        whenRequest cmd   -> handleRequestCmd
        whenEvent   alarm -> handleAlarm

      State handleAlarm{
          printCurrentMessage
      }
      Transition t0  
          whenRequest cmd -> handleRequestCmd
          whenEvent   alarm -> handleAlarm
        
        
      State handleRequestCmd{
        printCurrentMessage
        onMsg( cmd : cmd(X) ){
          [# val ANSW = "answerFor_${payloadArg(0)}" #]
          replyTo cmd with replytocmd : replytocmd( $ANSW ) 
        }		 		
      }
      Goto s0	
    }   


.. image::  ./_static/img/Qak/resourcecore.png
    :align: center 
    :width: 30% 

Il sistema formato dalla sola :ref:`resourcecore` è descritto come segue:

.. code::

  //file resourcecore.pl
  context(ctxresourcecore, "localhost",  "TCP", "8045").
  qactor( resourcecore, ctxresourcecore, "it.unibo.resourcecore.Resourcecore").

+++++++++++++++++++++++++++++
External caller1
+++++++++++++++++++++++++++++

**Progetto**: *it.unibo.corecallers* 

Supponiamo ora che un ulteriore componente QakActor software voglia 'fare sistema' con :ref:`resourcecore`, inviando una richiesta
alla risorsa.


.. image::  ./_static/img/Qak/corecaller.png
    :align: center 
    :width: 60% 



:remark:`I messaggi sono il 'collante' del sistema`

- Il componente che vuole interagire con  :ref:`resourcecore` deve conoscerne il linguaggio di interazione. Dunque, oltre 
  al protocollo usato dalla risorsa per ricevere i messaggi, occorre anche fare riferimento agli stessi tipi di messaaggio
  dichiarati in  :ref:`resourcecore`


.. code::

  System corecaller1

  Request cmd 		: cmd(X) // X =  w | s | a | d | h
  Reply   replytocmd  : replytocmd(X)
  Event    alarm      : alarm(V)

Agli occhi del nuovoco attore, la  :ref:`resourcecore` è un componente 'esterno' che deve essere solo 
dichiarato come tale, dando informazioni sul suo contesto.

.. code::

  Context ctxcorecaller1   ip [host= "localhost"   port= 8038 ]
  Context ctxresourcecore  ip [host= "127.0.0.1"   port= 8045 ]  
  
  ExternalQActor resourcecore context ctxresourcecore  

  QActor corecaller1 context ctxcorecaller1{ ... }

La descrizione del sistema 'visto' da ``corecaller1`` diventa:

.. code::
  
  //file corecaller1.pl
  context(ctxresourcecore, "localhost",  "TCP", "8045").
  qactor( resourcecore, ctxresourcecore, "it.unibo.resourcecore.Resourcecore").

A questo punto possiamo definire un comportamento che prevede lo scambio di messaggi.

.. code::

  QActor corecaller1 context ctxcorecaller1{
     State s0 initial { 	  
    	printCurrentMessage       
    	request resourcecore -m cmd : cmd(caller1) 
    }   
	Transition t0 
		whenReply replytocmd -> handleReply 
		whenEvent alarm      -> handleAlarm
  	
	State handleReply{
		printCurrentMessage
		delay 5000
		println("       --- caller1 handleReply: emit fire") 
 		emit alarm : alarm(fire)	 
 	}
	Transition t0  
 		whenEvent   alarm -> handleAlarm

	State handleAlarm{
		println("       --- caller1 handleAlarm   ") 
		printCurrentMessage
    //emit alarm : alarm(fire)	//possible LOOP!!
 	}
 	Transition t0 
		whenReply replytocmd -> handleReply 
 		whenEvent   alarm -> handleAlarm
  }  







.. sonarSimulator emette eventi usando emitLocalStreamEvent 

.. sentinel.qak

.. demoAskfor.qak

.. demoStreams.qak

.. Coded Qak


--------------------------------------
Actors as streams
--------------------------------------

`Reactive programming`_ is a combination of the best ideas from the `Observer`_ pattern, the `Iterator`_ pattern, 
and `Functional programming`_.

In `Reactive programming`_, un consumatore reagisce ai dati non appena arrivano, con la capacità
anche di *propagare le modifiche come eventi* agli osservatori registrati.

Un QAkActor può lavorare come un produttore osservabile di dati; può essere *osservato da altri attori* che si
'iscrivono' presso di lui.
Ciascun sottoscrittore elaborerà i dati 'in parallelo' con gli altri e potrà a sua volta funzionare come osservabile.


.. code:: Java

    abstract class  ActorBasic( ... ) {
    protected val subscribers = mutableListOf()

        fun subscribe( a : ActorBasic) : ActorBasic {
            subscribers.add(a)
            return a
        }
        fun subscribeLocalActor( actorName : String) : ActorBasic {
            val a = sysUtil.getActor(actorName)
            if( a != null  ){ subscribers.add(a); return a}
        }
        fun unsubscribe( a : ActorBasic) {
            subscribers.remove(a)
        }

        suspend fun emitLocalStreamEvent(v: ApplMessage ){
            subscribers.forEach { it.actor.send(v) }
        }

++++++++++++++++++++++++++++++++
emitLocalStreamEvent
++++++++++++++++++++++++++++++++

L'operazione ``emitLocalStreamEvent`` è una versione specializzata/ottimizzata del meccanismo di emissione di 
un evento che opera inserendo l'informazione direttamente nella coda associata agli attori sottoscrittori.


++++++++++++++++++++++++++++++++
Creazione di una pipe
++++++++++++++++++++++++++++++++

Nel progetto :ref:`basicrobot22<BasicRobot22: requisiti>` si introduce un sistema che configura
la pipe di figura


.. image::  ./_static/img/Robot22/sonarpipenano.png 
  :align: center 
  :width: 75%

.. code::

  firstActorInPipe = sysUtil.getActor("sonarsimulator")!!
  firstActorInPipe.
    subscribeLocalActor("datalogger").
    subscribeLocalActor("datacleaner").
    subscribeLocalActor("distancefilter").
    subscribeLocalActor("qasink") 
		  
I dati generati da *firstActorInPipe* (un ``sonarsimulator`` come  :ref:`sonarDataGen.kt`) sono 
memorizzati dal  ``datalogger``, filtrati dal ``datacleaner`` e gestiti dal ``distancefilter``, che
emette l'evento 

  ``obstacle:obstacle(V)`` 

nel caso che il valore corrente della distanza 'pulita' misurata risulti inferiore a un limite prefissato.


 