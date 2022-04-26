.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _visione olistica: https://it.wikipedia.org/wiki/Olismo
.. _state diagram: https://en.wikipedia.org/wiki/State_diagram#:~:text=A%20state%20diagram%20is%20a,this%20is%20a%20reasonable%20abstraction.
.. _Automa a stati finiti: https://it.wikipedia.org/wiki/Automa_a_stati_finiti
.. _Macchina d Moore: https://it.wikipedia.org/wiki/Macchina_di_Moore
.. _opinionated: https://govdevsecopshub.com/2021/02/26/opinionated-software-what-it-is-and-how-it-enables-devops/


======================================
Actors22
======================================

Il lavoro svolto fino ad ora ha coperto buona parte di quanto prospettato nella :ref:`FASE1` e nella
:ref:`FASE2`.

----------------------------------------
Uno sguardo alla Fase1
----------------------------------------

La :ref:`FASE1` ci ha progressivamente portato a costruire supporti per comunicazioni 
via rete basati su diversi protocolli: dapprima TCP, UDP e successivamente anche HTTP e WS.

Per agevolare lo sviluppo di applicazioni abbiamo 'nascosto' i dettagli tecnologici di ciascun protocollo,
introducendo classi che implementano l'astrazione :blue:`connessione`, 
espressa dall'interfaccia :ref:`Interaction2021<Interaction2021>`.

La implementazione del concetto di connessione per le WebSocket (:ref:`WsConnection`) ha introdotto l'idea 
di **connessione osservabile**, per la gestione dei :ref:`messaggi di stato`. 

Abbiamo rimandato alcune voci:

- Refactoring del sistema a fronte dell’uso di altri protocolli: MQTT e CoAP.
- Come dotare l’applicazione di una WebGui usando SpringBoot.

Le riprenderemo dopo avere completato il lavoro della :ref:`FASE2`.

----------------------------------------
Uno sguardo alla Fase2
----------------------------------------

La :ref:`FASE2` ci ha condotto a sviluppare supporti quali i
:ref:`Contesti-contenitori` e l':ref:`EnablerContext` che si avvale di un gestore di sistema di messaggi
:ref:`ContextMsgHandler<Il gestore di sistema dei messaggi>` per reindirizzare un messaggio a un 
handler applicativo (un POJO) di tipo :ref:`IApplMsgHandler<IApplMsgHandler>`.

Abbiamo poi pensato di sostituire i POJO :ref:`IApplMsgHandler<IApplMsgHandler>` con 
enti attivi, capaci di gestire in modo 'nativo' (FIFO) messaggi inseriti nella coda di ingresso loro associata.
Abbiamo denotato questi nuovi componenti con il nome di :ref:`Attori`  e ne abbiamo fornito una prima implementazione
:ref:`QakActor22<ActorQak e QakActor22>` che ha creato una corrispondeza tra 
il ‘macro-mondo’ rappresentato dai servizi distribuiti sulla rete 
e il ‘micro-mondo’ rappresentato dai componenti interni a questi servizi. 

Il problema della possibile prolificazione di Thread dovuta al concetto di Attore è stato superato
sfruttando una implementazione in Kotlin (libreria ``it.unibo.qakactor-2.7.jar``) 
realizzata in anni passati.
Abbiamo cioè costruito ed iniziato ad usare:

- una infrastruttura per attori **message-driven** come supporto alla costruzione di software distribuiti ed eterogeni

Al momento abbiamo lassciato in sospeso due temi: 

- Introduzione al linguaggio Kotlin.
- Dalle coroutine Kotlin agli attori Kotlin.


Li riprederemo dopo avere migliorato la nostra attuale infrastruttura, al fine di anticipare gli aspetti
più importanti che avevamo riportato come :ref:`FASE3`:

- da bottom-up a **top-down**: il ruolo dei modelli
- uso di **modelli eseguibili** nelle fasi di analisi dei requisiti e del problema,
  come premessa per l’abbattimento dei costi (e degli imprevisti) di produzione

Il lavoro che ci accingiamo a svolgere comprende anche un altro punto menzionato nella :ref:`FASE2`

- da attori *message-driven* ad attori che operano come un `Automa a stati finiti`_.

-----------------------------------------
Preludio alla Fase3
-----------------------------------------

In questa parte che precede la :ref:`FASE3` del nostro piano di lavoro,
introdurremo alcuni miglioramenti alla implementazione degli attori con lo 
scopo di agevolare quanto più possibile il lavoro dell'Application Designer.

A questo fine, faremo ampio ricorso allo strumento delle :ref:`Annotazioni` che 
permettono  di dare semantica aggiuntiva a classi e metodi Java attraverso **frasi dichiarative** che 
aiutano a meglio comprenderne il codice e a colmare in modo automatico 
l':ref:`abstraction gap<Abstraction GAP e topDown>` tra la nuova semantica e il livello tecnologico
sottostante.

La conseguenza più importante  sarà la possibilità di agevolare processi 
di produzione  :ref:`topDown<Abstraction GAP e topDown>` del software, ponendo **in primo 
piano i requisiti e il problema**, in modo da introdurre le tecnologie come risposta ad esigenze
esplicitamente espresse e motivate.

Faremmo anche passi sostanziali nel concretizzare il lavoro delle fasi di analisi (dei requisiti e del problema)
introducendo :ref:`Modelli` **eseguibili** del sistema da sviluppare, coorredati da opportuni
:ref:`piani di testing<Passi operativi 'a regime'>`, da cui i progettisti potranno
partire per le evoluzioni incrementali che, con diversi :ref:`SPRINT<SCRUM>`, 
porteranno alla versione finale del sistema. 


-----------------------------------------
Actor22 annotated
-----------------------------------------

In una `visione olistica`_ di un sistema software, cercheremo di superare la visione 'tecnicistica' introdotta in
:ref:`Configurare con Annotation`, cercando di creare una corrispondenza sistematica tra
i concetti-base del nostro :ref:`Modello ad Attori<Il paradigma ad Attori>` e le nostre nuove frasi dichiarative
in forma di :ref:`Annotazioni` Java.

++++++++++++++++++++++++++++++++++++++++
Un esempio di sistema a due nodi
++++++++++++++++++++++++++++++++++++++++
Progetto: **unibo.wenvUsage22** package: *unibo.wenvUsage22.actors.demofirst*.

Riportiamo subito un esempio di come si presentereranno le dichiarazioni per un sistema distribuito formato da due nodi:

- un PC, su cui attiviamo il programma ``MainAnnotationDemo22Pc``
- un RaspberryPi, su su cui attiviamo il programma ``MainAnnotationDemo22Rasp``

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Parte del sistema su PC
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code::

    @Context22(name="pcCtx",host="localhost",
               port="8080", protocol=ProtocolType.tcp)
    @Context22(name="raspCtx",host ="192.168.1.12",port="8082") //TCP default
    @Actor22(name="a1",contextName="pcCtx",implement=A1Actor22OnPc.class)
    @Actor22(name="a2",contextName="raspCtx" )

    public class MainAnnotationDemo22Pc {
    ...
    }

Questo programma dichiara il sistema composto da due attori: 

- l'attore  ``a1``, che opera nel contesto di nome ``pcCtx`` **locale** al PC in quanto specifica che il suo host è  
  :blue:`localhost`.  Ne viene quindi fornita anche la classe che lo implementa
- l'attore  ``a2``, che opera nel contesto di nome ``raspCtx`` con **host diverso da  localhost**.
  L'attore viene dunque visto (in questa prospettiva del sistema) come  **remoto**  e NON se ne specifica la classe 
  di implementazione.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Parte del sistema su RaspberryPi
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code::

    @Context22(name = "pcCtx",  host = "192.168.1.12", port = "8080") 
    @Context22(name = "raspCtx",host = "localhost",    port = "8082") //TCP default
    @Actor22(name = "a1",  contextName = "pcCtx" )
    @Actor22(name = "a2",  contextName = "raspCtx", implement=A2Actor22OnRasp.class )

    public class MainAnnotationDemo22Rasp {
    ...
    }

Questo programma dichiara il sistema nello stesso modo, ma con una **prospettiva diversa**: 

- l'attore di nome ``a1`` su ``pcCtx`` viene visto come **remoto**
- l'attore di nome  ``a2`` su ``raspCtx`` viene visto come **locale** al RaspberryPi e se ne fornisce dunque 
  la classe di implementazione.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Configurazione del sistema  
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Le annotazioni  sono gestite da :ref:`Qak22Context`. Il programma di ciascun nodo avrà unaa stessa, semplice
fase di configurazione; ad esempio:

.. code::

    public class MainAnnotationDemo22Pc {
        Qak22Context.configureTheSystem(this);
    }

Vediamo dunque come si è pervenuti a questo modo di specifica, dando anche qualche dettaglio su come opera 
il metodo  ``Qak22Context.configureTheSystem``.

++++++++++++++++++++++++++++++++++++++++
Annotazioni per dichiarare Contesti
++++++++++++++++++++++++++++++++++++++++

Nella sezione  :ref:`Dal locale al distribuito` abbiamo detto che:

:remark:`Un sistema distribuito è di norma formato da due o più contesti` 
      
Inoltre, un contesto:

        - opera su un nodo di elaborazione associato a un indirizzo IP
        - utilizza almeno un protocollo di comunicazione (tra cui sempre TCP) per ricevere messaggi 
          su una data porta di ingresso (che potrebbe assumere la forma di un URI

Ne consegue una annotazione dichiarativa della forma:

  ``@Context22(name=<STRING>,host=<STRING>,port=<STRING>,protocol=ProtocolType.<xxx>)``


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Qak22Context.setContexts22
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La annotazione precedente viene elaborata (da :ref:`Qak22Context` col metodo ``setContexts22``) che tiene traccia
di tutti i contesti dichiarati.

Nel caso di contesto con **host="localhost"**, si crea un oggetto che implementa l'interfaccia :ref:`IContext`
come istanza della classe ``EnablerContextForActors`` definita nel 
:ref:`Package unibo.actor22Comm` che utilizza il :ref:`ContextMsgHandler per attori`.


++++++++++++++++++++++++++++++++++++++++
Annotazioni per dichiarare Attori
++++++++++++++++++++++++++++++++++++++++

:remark:`Un attore nasce, vive e muore in un contesto`

- Nel caso di attore **locale**, ne consegue una annotazione dichiarativa della forma:

    ``@Actor22(name=<STRING>,  contextName=<STRING>, implement=<CLASS>)``
  
  La annotazione precedente viene elaborata (da :ref:`Qak22Context` col metodo ``setActorAsLocal``) che:

  - crea una istanza dell'attore come implementazione della classe specificata

.. - invia all'attore un messaggio di attivazione


- Nel caso di attore **remoto**, ne consegue una annotazione dichiarativa della forma:

    ``@Actor22(name=<STRING>,  contextName=<STRING> )``

  La annotazione precedente viene elaborata (da :ref:`Qak22Context` col metodo ``setActorAsRemote``) che: 

  - crea un proxy (singleton) per il contesto in cui risiede l'attore
  - memorizza il proxy in una mappa utilizzata dalla operazione :ref:`sendMsgToRemoteActor` invocata da 
    :ref:`sendMsg<Invio di messaggi da attore>`)


----------------------------------------
Actor22: esempi con WEnv
----------------------------------------
Progetto: **unibo.wenvUsage22** package: *unibo.wenvUsage22.actors.basic*.


Costruiamo un sistema formato da un attore di classe ``ActorWithObserverUsingWEnv`` che fa percorrere al 
:ref:`VirtualRobot` il boundary della stanza, utilizzando :ref:`WsConnection` e l'osservatore della 
connessione di classe :ref:`WsConnSysObserver`.


.. code::

    @Context22(name = "pcCtx", host = "localhost", 
            protocol=ProtocolType.tcp, port = "8083")
    @Actor22(name = MainActorUsingWEnv.myName, contextName = "pcCtx", 
            implement = ActorWithObserverUsingWEnv.class)
    public class MainActorUsingWEnv {
	  public static final String myName = "wenvUse";
	
    public void doJob() {
      Qak22Context.configureTheSystem(this);
      Qak22Context.showActorNames();
      Qak22Util.sendAMsg( SystemData.startSysCmd("main",myName) );
    };
    public void terminate() { ... }
    public static void main( String[] args) throws Exception {
      CommUtils.aboutThreads("Before start - ");
      MainActorUsingWEnv appl = new MainActorUsingWEnv( );
      appl.doJob();
      appl.terminate();
    }
    }

++++++++++++++++++++++++++++++++++++++++++++
ActorWithObserverUsingWEnv
++++++++++++++++++++++++++++++++++++++++++++

Al momento della creazione, l'attore si connette al :ref:`VirtualRobot` creando una :ref:`WsConnection` 
associata a un osservatore di tipo :ref:`WsConnSysObserver`.

.. code:: 
  
  public class ActorWithObserverUsingWEnv extends QakActor22  {
    private Interaction2021 conn;
    private int n = 0;
    
    public ActorWithObserverUsingWEnv(String name) {
      super(name);
      init();
    }

    protected void init() {
      conn = WsConnection.create("localhost:8091" );
      ((WsConnection) conn).addObserver( new WsConnSysObserver(getName()) );
      ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
    }
  
    
    @Override
    protected void handleMsg(IApplMessage msg) {
       interpret(msg);
    }

La gestione dei messaggi è delegata al metodo  ``interpret``, che gestisce:

- il mesaggio di attivazione dell'attore (con ``id=ApplData.activateId``), inviando un comando di movimento 
  in avanti di durata tale da provocare la collisione del robot con ``wallDown``
- il messaggio ``SystemData.wsEventId`` generato da  :ref:`WsConnSysObserver` al momento della collisione
  del robot con ``wallDown``
- messaggi di movimento (con ``id= ApplData.moveCmdId``)

.. code:: 

    protected void interpret( IApplMessage m ) {
      if( m.msgId().equals( ApplData.activateId )) {
        autoMsg(ApplData.moveCmd(getName(),getName(),"w"));
        return;
      }
      if( m.isEvent() || m.msgId().equals( SystemData.wsEventId ) ) {
        handleWsInfo(m);
        return;
      }
      if( ! m.msgId().equals( ApplData.moveCmdId )) {
        ColorsOut.outappl(getName() + " | sorry, I don't handle :" + m,  ColorsOut.YELLOW);
        return;
      }
      switch( m.msgContent() ) {
        case "w" : VRobotMoves.moveForward(getName(),conn,2300);break;
        case "a" : VRobotMoves.turnLeft(getName(),conn);break;
        default: break;
      }
    }

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Gestione dei messaggi di stato
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La gestione dei :ref:`messaggi di stato` distingue tra completamenti di mosse e collsioni,
realizzando parte della 'business logic'.

.. code:: 

      protected void handleWsInfo(IApplMessage m) {
      String msg = m.msgContent().replace("'", "");
      JSONObject d = new JSONObject(""+msg);
      if( d.has("collision")) {
        n++;
        sendMsg(ApplData.moveCmd(getName(),getName(),"a"));
      }
      if( d.has("endmove") && d.getBoolean("endmove") && n < 4) 
         sendMsg(ApplData.moveCmd(getName(),getName(),"w"));      
      }
  }

:remark:`la realizzazione 'spezzata' della business logic va rivista`

++++++++++++++++++++++++++++++++++++++++++++
Un primo automa a stati finiti
++++++++++++++++++++++++++++++++++++++++++++
Progetto: **unibo.wenvUsage22** code: *unibo.wenvUsage22.actors.fsm.basic.ActorWithFsmBasic*.

Il comportamento logico del BoundaryWalker può essere descritto da un semplice FSM (`Automa a stati finiti`_):

.. code:: 

  private enum State {start, goingAhead, turnedLeft, end };


.. image::  ./_static/img/VirtualRobot/FsmBoundary.PNG
    :align: center 
    :width: 60% 

Lo `state diagram`_ di figura può essere realizzato da una funzione come quella che segue:

.. code:: 
 
  protected void fsm(String move, boolean endmove){
    switch( curState ) {
    case start: {
      VRobotMoves.step(getName(), conn ); //forward per 300
      curState = State.goingAhead;
      numIter++;
      break;
    }
    case goingAhead: {
      if (endmove ) {  //lo stato non cambia	            	
        VRobotMoves.step(getName(), conn );
      } else {
        VRobotMoves.turnLeft(getName(), conn);
        curState = State.turnedLeft;	           
      }	        	
      }
      break;
    }
    case turnedLeft:{
      numIter++;
      if( numIter < 5 ) {
        VRobotMoves.step(getName(), conn );
        curState = State.goingAhead;
      }
      else curState = State.end;
      break;
    }
    case end: {
      ColorsOut.outappl("fsm DONE "  , ColorsOut.MAGENTA);
      break;
    }       
    }
  }

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Nuova gestione dei messaggi di stato
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La ricezione di un messaggio di stato induce un nuovo passo computazionale (una transizione) nell'automa.

:remark:`tutta la business logic è ora definita dalla funzione fsm`

.. code:: 

      protected void handleWsInfo(IApplMessage m) {
      String msg = m.msgContent().replace("'", "");
      JSONObject d = new JSONObject(""+msg);
      if( d.has("collision")) {
        fsm( d.getString("collision"), false);
        return;
      }
      if( d.has("endmove") && d.getBoolean("endmove") && n < 4) 
        fsm( d.getString("move"), true);
        return;
      }
  }


--------------------------------------------------
QakActor22Fsm
--------------------------------------------------

La classe astratta ``QakActor22Fsm`` estende :ref:`QakActor22<ActorQak e QakActor22>` impostando il funzionamento di un attore
come un FSM concepito come una `Macchina d Moore`_ i cui stati sono definiti da azioni, implementazioni della interfaccia 
:ref:`StateActionFun`.

Ogni stato è uan coppia ``<StateName, StateActionFun>`` che viene inserita durante la fase di costruzione 
nella  *tabella degli stati* (``stateMap``).

+++++++++++++++++++++++++++++++++++++
QakActor22Fsm: costruttore
+++++++++++++++++++++++++++++++++++++

Il costruttore dell'automa opera come segue:

#. invoca un metodo (dichiarato abstract) :ref:`declareTheStates` in cui l'Application designer definisce
   gli stati dell'automa. Questi stati sono inseriti nella *tabella degli stati*  ``stateMap``,  usando come 
   chiave il nome dello stato
#. fissa il valore della variabile ``curState`` che denota il nome dello stato corrente al valore restituito dal metodo 
   ``setTheInitialState`` (dichiarato abstract) 
#. attiva l'automa, che si posiziona sullo stato iniziale (unico)

.. code:: 

  public QakActor22Fsm(String name) {
    super(name);
    declareTheStates( );
    setTheInitialState( );
    addExpectedMsg(curState, ApplData.startSysCmdId );
    //Si auto-invia il messaggio di inizio che porta nello stato iniziale
    autoMsg(ApplData.startSysCmd("system",name));
  }

+++++++++++++++++++++++++++++++++++++
addExpectedMsg
+++++++++++++++++++++++++++++++++++++

In un `Automa a stati finiti`_, ogni stato risulta 'essere interessato' a ricevere un preciso insieme di messaggi,
effettuando una transizione di stato quando uno di questi si manifesta. 

Il metodo ``addExpectedMsg`` inserisce l'identificatore di un messaggio tra quelli attesi, usando una 
tabella  ``nextMsgMap`` che ha come chiave il nome dello stato.
Questa tabella viene consultata dal metodo ``checkIfExpected`` e aggiornata dal metodo :ref:`nextState`, 
che effettua le transizioni di stato.


+++++++++++++++++++++++++++++++++++++
QakActor22Fsm: handleMsg
+++++++++++++++++++++++++++++++++++++

La classe ``QakActor22Fsm`` funziona secondo il solito meccanismo message-driven, ma realizza una gestione dei messaggi 
volta a tenere conto delle specifiche dell'automa. 

:remark:`un messaggio è gestito solo se atteso nello stato corrente, se no è memorizzato`

In particolare, il metodo ``handleMsg``:

#. controlla che il messaggio sia atteso nello stato corrente
#. se il messaggio è atteso, esegue :ref:`stateTransition`, che effettua una transizione dallo stato corrente 
   allo stato indicato nella *tabella delle transizioni correnti* (:ref:`transTab<addTransition>`)
#. se il messaggio non è atteso, lo inserisce in una coda locale interna (``OldMsgQueue``), che verrà consultata al termine 
   della esecuzione del nuovo stato (si veda :ref:`nextState`)

.. code:: 

    @Override
    protected void handleMsg(IApplMessage msg) {
        String state = checkIfExpected(msg);
        if ( state != null ) stateTransition(state,msg);
        else memoTheMessage(msg);
    }

 

+++++++++++++++++++++++++++++++++++++
StateActionFun
+++++++++++++++++++++++++++++++++++++

Un oggetto di tipo ``StateActionFun`` definisce il comportamento dell'automa in relazione alla ricezione di un messaggio
di tipo :ref:`IApplMessage`.

.. code:: 

    public interface StateActionFun {
        void run(IApplMessage msg);
    }

La classe ``QakActor22Fsm`` (alquanto  `opinionated`_ ...) impone un precisa struttura logica al 
comportamento di uno stato:

.. code:: 

    StateActionFun ... = new StateActionFun() {
        @Override
        public void run(IApplMessage msg) {
            //Body dello stato (Behavior)
            addTransition( <nextState>, <msgId> ); 
            addTransition ...
            nextState();
        }			
    };

Ad esempio:

.. code:: 

    StateActionFun s0State = new StateActionFun() {
        @Override
        public void run(IApplMessage msg) {
            outInfo( ""+msg ); //outInfo Inherited
            addTransition( "s1", ApplData.moveCmdId );
            nextState();
        }		
    };


+++++++++++++++++++++++++++++++++++++
declareTheStates
+++++++++++++++++++++++++++++++++++++

Un esempio del metodo declareTheStates:

.. code:: 

    @Override
    protected void declareTheStates( ) {  
		
      StateActionFun s0State = ...
      declareState( "s0", s0State);

      declareState("s1", new StateActionFun() {
      @Override
      public void run(IApplMessage msg) {
        outInfo(""+msg); 	//outInfo Inherited
        addTransition( "s1", ApplData.moveCmdId );
        addTransition( "s2", ApplData.haltSysCmdId );
        nextState();
      }	
       ...		
    });

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
declareState
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Il metodo declareState inserisce lo stato nella *tabella degli stati*  ``stateMap``.


+++++++++++++++++++++++++++++++++++++
addTransition
+++++++++++++++++++++++++++++++++++++

Il metodo ``addTransition`` aggiunge una transizione alla *tabella delle transizioni correnti* (``transTab``)
aggiungendo una coppia :blue:`(nextstate, msgId)` col seguente significato:

- se il prossimo messaggio ha identificatore :blue:`msgId`, transita allo stato :blue:`nextstate`

 
+++++++++++++++++++++++++++++++++++++
stateTransition
+++++++++++++++++++++++++++++++++++++

La transizione di stato opera come segue:

#. aggiorna il valore dello stato corrente (variabile ``curState``)
#. pulisce la  *tabella delle transizioni correnti* ``transTab``
#. recupera dalla *tabella degli stati* ``stateMap`` il riferimento al codice dello stato
#. esegue il codice dello stato

.. code:: 

	protected void stateTransition(String stateName, IApplMessage msg ) {
		curState   = stateName;
		currentMsg = msg;
		transTab.removeAllElements();
		StateActionFun a = stateMap.get(stateName);
		if( a != null ) a.run( msg );
		else ColorsOut.outerr(getName() + " | QakActor22Fsm TERMINATED");
	}	



+++++++++++++++++++++++++++++++++++++
nextState
+++++++++++++++++++++++++++++++++++++

La operazione ``nextState`` definita in ``QakActor22Fsm`` effettua una transione di stato sulla base del 
prossimo messaggio (o meglio del prossimo ``msgId``)
ricevuto dall'automa. Per ogni elemento della tabella ``transTab``:

#.  cerca se il ``msgId`` si trova nella ``oldMsgQueue``. In caso positivo, invoca :ref:`stateTransition` per effettuare la
    transizione di stato relativa a questo vecchio messaggio
#. in caso negativo, invoca `addExpectedMsg`, per inserire l'id del messaggio tra quelli attesi.
   Ricordando il funzionamento di :ref:`QakActor22Fsm: handleMsg`, l'automa al momento di ferma.

.. code:: 

	protected void nextState() {
		clearExpectedMsgs();
		Iterator< Pair<String, String> > iter = transTab.iterator();
		while( iter.hasNext() ) {
			Pair<String, String> v = iter.next();
			String state = v.getFirst();
			String msgId = v.getSecond();
			IApplMessage oldMsg = searchInOldMsgQueue( msgId );
			if( oldMsg != null ) {
				stateTransition(state,oldMsg);
				break;
			}
			else  addExpectedMsg(state, msgId);
		}
	}


--------------------------------------------
Automa refactored
--------------------------------------------

Progetto: **unibo.wenvUsage22** code: *unibo.wenvUsage22.actors.robot.RobotBoundaryWalkerFsm*.

:worktodo:`WORKTODO: Refactoring usando QakActor22Fsm`

- Fare un refactoring di :ref:`ActorWithFsmBasic<Un primo automa a stati finiti>` impostando l'attore come
  un istanza di :ref:`QakActor22Fsm`.


