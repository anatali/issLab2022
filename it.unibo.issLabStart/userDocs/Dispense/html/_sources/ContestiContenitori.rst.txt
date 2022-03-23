.. role:: red 
.. role:: blue 
.. role:: remark
  
.. _tuProlog: https://apice.unibo.it/xwiki/bin/view/Tuprolog/
.. _Prolog: https://it.wikipedia.org/wiki/Prolog

==================================================
Contesti-contenitori
==================================================

Nella versione attuale, ogni :ref:`enable<EnablerAsServer>` attiva un ``TCPServer`` su una propria porta.

.. image::  ./_static/img/Radar/EnablersLedSonar.PNG
  :align: center 
  :width: 70%



Una ottimizzazione delle risorse può essere ottenuta introducendo :blue:`un solo TCPServer` per ogni nodo
computazionale. 

----------------------------------------------
Analisi del concetto di Contesto
----------------------------------------------

Introdurre un solo server per nodo computazionale significa introdurre un componente con due responsabilità
principali:

- permettere che 'client' allocati su altri nodi computazionali possano inviare messaggi al nodo e gestire 
  tali messaggi a 'livello di sistema';
- fungere da contenitore di componenti  capaci di gestire i messaggi a livello applicativo.

D'ora in poi denomineremo col termine generico ``Contesto`` un componente di questo tipo, lasciando
indeterminata la sua natura.

In questa fase dello sviluppo, è tuttavia opportuno realizzare la nuova idea riusando il software sviluppato 
negli SPRINT precedenti. In questa prospettiva:

- il contesto potrà essere implementato da una classe (denominata :ref:`TcpContextServer<TcpContextServer>`) che specializza
  la classe  :ref:`TcpServer<TcpServer>`;
- componenti applicativi gestori dei messaggi potrano essere definiti da classi che specializzano la classe 
  :ref:`ApplMsgHandler<ApplMsgHandler>`  e implementano :ref:`IApplMsgHandler`;
- il contesto deve reindirizzare un messaggio ad uno specifico
  componente applicativo (come :ref:`LedApplHandler` e :ref:`SonarApplHandler`). Questa 
  gestione  a 'livello di sistema' dei messaggi può essere delegato
  a un oggetto :ref:`ContextMsgHandler<ContextMsgHandler>` di tipo :ref:`IApplMsgHandler<IApplMsgHandler>` 
  che funge da anche da **contenitore di componenti**.


.. image::  ./_static/img/Radar/TcpContextServerSonarLed.PNG
  :align: center 
  :width: 50%


In questo quadro:

:remark:`il ContextMsgHandler deve sapere a quale componente è destinato un messaggio`

Occorre quindi superare l'idea che un messaggio sia una String interpretabile dal
solo dal livello applicativo (si veda :ref:`Interpreti`).

Più specificatamente, occorre definire una estensione sulla struttura dei messaggi, 
che ci darà  anche uno 
:blue:`standard interno` sulla struttura delle informazioni scambiate via rete.

Parti di questa estensione dovranno essere interpretate dal :ref:`ContextMsgHandler<ContextMsgHandler>`,
che svolge il ruolo di un 'interprete di sistema'.  La sua operazione
``elaborate(String message)`` effettua il voluto reindirizzamento del messaggio
a uno dei componenti applicativi memorizzati.


.. _msgApplicativi:

++++++++++++++++++++++++++++++++++++++++++++++
Struttura dei messaggi applicativi
++++++++++++++++++++++++++++++++++++++++++++++

Introduciamo dunque una  estensione sulla struttura dei messaggi, che ci darà d'ora in poi anche uno 
:blue:`standard interno` sulla struttura delle informazioni scambiate via rete:

 .. code:: java

    msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )

  - MSGID:    identificativo del messaggio
  - MSGTYPE:  tipo del msg (Dispatch, Invitation,Request,Reply,Event)  
  - SENDER:   nome del componente che invia il messaggio
  - CONTENT:  contenuto applicativo (payload) del messaggio 
  - RECEIVER: nome del componente chi riceve il messaggio 
  - SEQNUM:   numero di sequenza del messaggio

Come già discusso in :ref:`Terminologia di riferimento`, 
i messaggi scambiati sono logicamente suddivisi in diverse categorie:

.. list-table:: 
  :widths: 70,30
  :width: 100%

  * - - :blue:`dispatch`: un messaggio inviato a un preciso destinatario senza attesa  di una risposta 
        (in modo detto anche  `fire-and-forget`);
      - :blue:`invitation`: un messaggio inviato a un preciso destinatario aspettandosi un 'ack' da parte di questi;
      - :blue:`request`: un messaggio inviato a un preciso destinatario aspettandosi da parte di questi una 
        :blue:`response/reply` logicamente correlata alla richiesta;
      - :blue:`event`: un messaggio inviato a chiunque sia in grado di elaborarlo.

    - .. image:: ./_static/img/Architectures/legendMessages.PNG
        :align: center
        :width: 80%

.. code:: java

  enum ApplMessageType{
      event, dispatch, request, reply, invitation
  }   
   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Rappresentazione dei messaggi
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La rappresentazione in forma di String dei messaggi seguirà le regole della sitassi `Prolog`_. In particolare:

- gli identificatori ``MSGID,SENDER,RECEIVER`` sono espressi da **atomi** Prolog formati da lettere minuscole;
- ``MSGTYPE`` è un atomo prefissato (al momento) tra i seguenti: ``dispatch, request, reply``;
- ``CONTENT`` è un **termine** Prolog;
- ``SEQNUM`` è un intero.

Esempi di messaggi:

.. code:: java

   msg(sonarcmd,dispatch,controller,sonar,deactivate,0)
   msg(cmd,dispatch,controller,led,eecute(turnOn),1)
   msg(req,request,main,sonar,getDistance,2)
   msg(req,request,main,led,info(getState),3)


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Il problema delle risposte
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nella versione strutturata dei messaggi, il tipo di messaggio  ``reply`` 
denota che il messaggio è una **risposta**, relativa a una precedente *richiesta* (``request``).

L'attuale implementazione dell'invio di una richiesta è fatta in modo **sincrono** dal metodo ``request`` di 
:ref:`TcpConnection<TcpConnection>` (che implementa :ref:`Interaction2021<Interaction2021>`).
Ciò significa che  il componente che effettua una richiesta usando il metodo ``request``
rimane attualmente in attesa della risposta qualificata come ``reply``.

Ma in futuro si può pensare di inviare richieste in modo **asincrono**, cioè senza aspettare 
subito la risposta.

In questo scenario, un componente potrebbe inviare più richieste a uno o più destinatari 
e gestire poi le risposte man mano arriveranno, in un ordine qualsiasi.
Occorre quindi che tale componente possa sapere, quando riceve una ``reply``, a quale richiesta
corrisponde.

Osserviamo che un problema del genere si pone anche nel sistema attuale, in quanto un componente
potrebbe inviare più richieste a uno stesso destinatario invocando
il metodo ``request`` di :ref:`TcpConnection<TcpConnection>` all'interno di un Thread.
In generale infatti, non si può assumere che il destinatario risponda in modo FIFO a due richieste
che provengono dallo stesso componente.


+++++++++++++++++++++++++++++++
Lo SPRINT4
+++++++++++++++++++++++++++++++

La realizzazione di sistemi in cui ogni nodo computazionale 
può ospitare un Contesto sarà l'obiettivo dello SPRINT4 e del progetto ``it.unibo.radarSystem22_4``.

In questo progetto inseriremo sia il software di competenza dei **System Designer** 
(nel package ``it.unibo.radarSystem22_4.comm``) sia il software di competenza degli **Application Designer**.
(nel package ``it.unibo.radarSystem22_4.appl``).

In una fase successiva, torneremo a scorporare la parte di sistema, ma lo faremo dopo avere introdotto
gli :doc:`Attori`. 

Come in precedenza, il progetto si avvarrà della libreria ``it.unibo.radarSystem22.domain-1.0`` che contiene 
il software relativo al dominio, che ovviamente non cambia.


-------------------------------------------------------
SPRINT4: il progetto del SystemDesigner
-------------------------------------------------------





.. _ApplMessage:
 
++++++++++++++++++++++++++++++++++++++++++++++++
La classe ApplMessage
++++++++++++++++++++++++++++++++++++++++++++++++

La classe ``ApplMessage`` fornisce metodi per la costruzione e la gestione di messaggi organizzati
nel modo descritto. La classe si avvale del supporto del tuProlog_ per il passaggio della rapprresentazione 
in forma di String a una rappresentazione interna manipolabile da programma e viceversa.

 .. code:: java

   public class ApplMessage implements IApplMessage{
    protected String msgId       = "";
    protected String msgType     = null;
    protected String msgSender   = "";
    protected String msgReceiver = "";
    protected String msgContent  = "";
    protected int msgNum         = 0;
    public ApplMessage( String MSGID, String MSGTYPE, String SENDER, 
          String RECEIVER, String CONTENT, String SEQNUM ) {
      ...
    }
    public ApplMessage( String msg ) {
      Struct msgStruct = (Struct) Term.createTerm(msg);
      setFields(msgStruct);
    }  
    public String msgId() {   return msgId; }
    public String msgType() { return msgType; }
    public String msgSender() { return msgSender; }
    public String msgReceiver() { return msgReceiver;  }
    public String msgContent() { return msgContent;  }
    public String msgNum() { return "" + msgNum; }
    public boolean isEvent(){ 
      return msgType == ApplMessageType.event.toString(); }
    ...
    public String toString() { ... }
  }

++++++++++++++++++++++++++++++++++++++++++++++++
IApplMessage
++++++++++++++++++++++++++++++++++++++++++++++++

 .. code:: java

   public interface IApplMessage {
    public String msgId();
    public String msgType();
    public String msgSender();
    public String msgReceiver();
    public String msgContent();
    public String msgNum();
    public boolean isDispatch();
    public boolean isRequest();
    public boolean isReply();
    public boolean isEvent();
  }

.. _IApplMsgHandlerEsteso:

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Estensione della interfaccia :ref:`IApplMsgHandler<IApplMsgHandler>`
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Provvediamo a modificare il contratto relativo ai gestori dei messaggi di
livello applicativo che sono ora di tipo :ref:`ApplMessage<ApplMessage>`.

.. code:: Java

  public interface IApplMsgHandler {
    ...
    public void elaborate( IApplMessage message, Interaction2021 conn ); 
  }

:remark:`messaggi standard di sistema`

- D'ora in poi il metodo ``elaborate`` con argomento ``IApplMessage<IApplMessage>`` diventerà il metodo di riferimento
  per la gestione dei messaggi. In altre parole, tutte le nostre applicazioni distribuite 
  invieranno messaggi della forma:

  .. code:: java

     msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Modifica della classe :ref:`ApplMsgHandler<ApplMsgHandler>`
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Di conseguenza, introduciamo nella classe astratta :ref:`ApplMsgHandler<ApplMsgHandler>`  
la specifica del metodo ``abstract  elaborate( ApplMessage message, Interaction2021 conn )`` 
che dovrà essere definito dalle classi specializzate.


&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
prepareReply
&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

Per associare le risposte alle richieste, viene introdotto il  metodo ``prepareReply`` che
generare un messaggio strutturato ``msg/6`` tale che:

- l'identificativo del messaggio di risposta sia l'identificativo del messaggio di richiesta;
- l'identificativo del destinatario sia l'identificativo del mittente della richiesta.
 
.. code:: java

    protected ApplMessage prepareReply(ApplMessage message, String answer) {
    String payload = message.msgContent();
    String sender  = message.msgSender();
    String receiver= message.msgReceiver();
    String reqId   = message.msgId();
    ApplMessage reply = null;
      if( message.isRequest() ) {
         reply = CommUtils.buildReply(receiver,reqId,answer,message.msgSender());
      }else { //DEFENSIVE
        ColorsOut.outerr(name + " | ApplMsgHandler prepareReply ERROR...");
      }
      return reply;
    }

.. _IApplIntepreterEsteso:

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Estensione della interfaccia :ref:`IApplIntepreter<IApplIntepreterNoCtx>`
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

In modo analogo, modifichiamo il contratto relativo alla interpretazione dei messaggi:

.. code:: java

  public interface IApplIntepreter {
     public String elaborate( IApplMessage message );    
  }


.. _TcpContextServer:

+++++++++++++++++++++++++++++++++++++++++
Il TcpContextServer
+++++++++++++++++++++++++++++++++++++++++

Il ``TcpContextServer`` è una specializzazione del :ref:`TcpServer<TcpServer>` che lega il campo ``userDefHandler`` 
a un gestore di messaggi (il `ContextMsgHandler`_ ) che ha il compito
di reindirizzare il messaggio ricevuto di forma ``msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )``
al gestore applicativo, sulla base dell'attributo  ``RECEIVER``.

.. image:: ./_static/img/Architectures/MessageHandlers.PNG
   :align: center 
   :width: 70%

Per ottenere questo scopo, il ``TcpContextServer``
definisce metodi per aggiungere al  (ed eliminare dal)  `ContextMsgHandler`_  oggetti di tipo :ref:`IApplMsgHandler<IApplMsgHandler>` 
che realizzano la gestione di livello applicativo dei messaggi di tipo `ApplMessage`_.

 
.. code:: java

  public class TcpContextServer extends TcpServer{
  private static boolean activated = false;
  private ContextMsgHandler ctxMsgHandler;
    public TcpContextServer(String name, int port ) {
      super(name, port, new ContextMsgHandler("ctxH"));
      this.ctxMsgHandler = (ContextMsgHandler) userDefHandler;
    } 
    public void addComponent( String name, IApplMsgHandler h) {
      ctxMsgHandler.addComponent(name,h);
    }
    public void removeComponent( String name ) {
      ctxMsgHandler.removeComponent(name );
    }
  }
 
.. _ContextMsgHandler:

+++++++++++++++++++++++++++++++++++++++++
Il gestore di sistema dei messaggi
+++++++++++++++++++++++++++++++++++++++++

Il gestore dei sistema dei messaggi attua il reindirizzamento (dispatching) consultando una mappa
interna che associa un :blue:`identificativo univoco` (il nome del destinatario) a un handler.


 .. code:: java

  public class ContextMsgHandler extends IApplMsgHandler{
  private HashMap<String,IApplMsgHandler> handlerMap = 
                           new HashMap<String,IApplMsgHandler>();
    public ContextMsgHandler(String name) { super(name); }
    @Override
    public void elaborate(String message,Interaction2021 conn) {
      //msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )
      try {
        ApplMessage msg  = new ApplMessage(message);
        elaborate( msg, conn );
      }catch(Exception e) { ...	}
    }
    @Override
    public void elaborate(ApplMessage msg,Interaction2021 conn) {
      String dest       = msg.msgReceiver();
      IApplMsgHandler h = handlerMap.get( dest );
      if( dest!=null && (! msg.isReply()) ) 
        h.elaborate(msg.msgContent(), conn);
    }
    public void addComponent( String name, IApplMsgHandler h) {
      handlerMap.put(name, h);
    }
    public void removeComponent( String name ) {
      handlerMap.remove( name );
    }
  }

.. image:: ./_static/img/Architectures/ContextServer.PNG
   :align: center 
   :width: 70%

I componenti di tipo :ref:`IApplMsgHandler<IApplMsgHandler>`:

- :remark:`sono gestori di messaggi`
- :remark:`acquisiscono dal contesto la capacità di comunicazione`
 
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Verso gli attori 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

I componenti di tipo :ref:`IApplMsgHandler<IApplMsgHandler>` potrebbero essere realizzati come 
enti attivi dotati di una coda di messaggi, denominati **attori**:

.. image:: ./_static/img/Architectures/ContestiEComponenti.PNG
   :align: center 
   :width: 80%


La trasformazione di un componente-gestore in attore sarà introdotta nella sezione :doc:`Attori`.


-------------------------------------------------------
SPRINT4: il progetto dell' ApplicationDesigner
-------------------------------------------------------

 
Ridefinizione degli handler

All'interno di ogni handler applicativo, occorre ora definire il codice del metodo `elaborate` 
di :ref:`ApplMsgHandler<ApplMsgHandler>`, quando il messaggio di input è di tipo :ref:`ApplMessage<ApplMessage>`.

Prima di procedere in questo senso, introduciamo un metodo utile quando occorra inviare risposte.


++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Refactoring per il Led
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

.. _LedApplIntepreterWithCtx:

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
elaborate di :ref:`LedApplInterpreter<LedApplIntepreterNoCtx>` 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code:: java

  public class LedApplInterpreter implements IApplInterpreter  {
    ...
    @Override
    public String elaborate( String message ) { ... }

    @Override
    public String elaborate( IApplMessage message ) {
    String payload = message.msgContent();
      if(  message.isRequest() ) {
        if(payload.equals("getState") ) {
          String ledstate = ""+led.getState();
          ApplMessage reply = Utils.prepareReply( message, ledstate);
          return (reply.toString() ); //msg(...)
        }else return "request_unknown";
      }else return elaborate( payload );  
    }
  }

.. _LedApplHandlerWithCtx:

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
elaborate di :ref:`LedApplHandler` 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code:: java

  public class LedApplHandler extends ApplMsgHandler {
  ...
  @Override
  public void elaborate( IApplMessage message, Interaction2021 conn ) {
    if( message.isRequest() ) {
      String answer = ledInterpr.elaborate(message);
      sendMsgToClient( answer, conn );
    }else {
      ledInterpr.elaborate( message.msgContent() ); 
    }	
  }


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
LedProxyAsClient
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. _LedProxyAsClient:

.. code::   java

  public class LedProxyAsClient extends ProxyAsClient implements ILed {
	public static  IApplMessage turnOnLed   ;
	public static  IApplMessage turnOffLed  ;
 	public static  IApplMessage getLedState ;

    public LedProxyAsClient( String name, String host, String entry,
                       ProtocolType protocol  ) {
      super(name,host,entry, protocol);
      turnOnLed   = CommUtils.buildDispatch(name,"cmd", "on",      "led");
      turnOffLed  = CommUtils.buildDispatch(name,"cmd", "off",     "led");
      getLedState = CommUtils.buildRequest(name, "req", "getState","led");
    }

    @Override
    public void turnOn() { 
	     if( protocol == ProtocolType.tcp   ) {
        sendCommandOnConnection(Utils.turnOnLed.toString());
      }
      //ALTRI PROTOCOLLI ...	
     }

    @Override
    public void turnOff() {   
      if( protocol == ProtocolType.tcp  ){
        sendCommandOnConnection(Utils.turnOffLed.toString());
      }
      //ALTRI PROTOCOLLI ...	
    }

    @Override
    public boolean getState() {   
      String answer="";
      if( protocol == ProtocolType.tcp ){
        answer = sendRequestOnConnection(getLedState.toString()) ;
      }
      //ALTRI PROTOCOLLI ...	
      return answer.equals("true");
    }
  }

I metodi ``sendCommandOnConnection`` e ``sendRequestOnConnection`` sono definiti in :ref:`ProxyAsClient`.


++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Refactoring per il Sonar
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

.. _SonarApplIntepreterWithCtx:

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
elaborate di :ref:`SonarApplInterpreter<SonarApplIntepreterNoCtx>` 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code:: java

  public class SonarApplInterpreter implements IApplInterpreter{
  ...
  @Override
  public String elaborate(String message) { ... }

  @Override
  public String elaborate(IApplMessage message) {
    String payload = message.msgContent();
    if( message.isRequest() ) {
      if(payload.equals("getDistance") ) {
        String vs = ""+sonar.getDistance().getVal();
        ApplMessage reply = Utils.prepareReply( message, vs);  
        return reply.toString();
      }else if(payload.equals("isActive") ) {
        String sonarState = ""+sonar.isActive();
        ApplMessage reply = Utils.prepareReply( message, sonarState);  
        return reply.toString();
      }else return "request_unknown";
    }else return elaborate( payload );			
  }


.. _SonarApplHandlerWithCtx:

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Metodo elaborate di :ref:`SonarApplHandler` 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code:: java

  public class SonarApplHandler extends ApplMsgHandler {    
  ...
  @Override
  public void elaborate( IApplMessage message, Interaction2021 conn ) {
    String payload = message.msgContent();
      if( message.isRequest() ) {
        String answer = sonarIntepr.elaborate(message);
        sendMsgToClient( answer, conn );
      }else sonarIntepr.elaborate( message.msgContent() ); 
  }

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
SonarProxyAsClient
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Simile al caso del Led.

.. code:: java

  public class SonarProxyAsClient extends ProxyAsClient implements ISonar{
    public static    IApplMessage sonarActivate   ; 
    public static    IApplMessage sonarDeactivate ;  
    public static    IApplMessage getDistance  ; 
    public static    IApplMessage isActive     ; 

    public SonarProxyAsClient( 
        String name, String host, String entry, ProtocolType protocol ) {
      super( name,  host,  entry, protocol );
      sonarActivate   = CommUtils.buildDispatch(name,"cmd", "activate",   "sonar");
      sonarDeactivate = CommUtils.buildDispatch(name,"cmd", "deactivate", "sonar");
      getDistance     = CommUtils.buildRequest(name, "req", "getDistance","sonar");
      isActive        = CommUtils.buildRequest(name, "req", "isActive",   "sonar");
    }
    @Override
    public void activate() {
      if( protocol == ProtocolType.tcp  ) {
        sendCommandOnConnection(sonarActivate.toString());	
      }
      //ALTRI PROTOCOLLI ...	
    }
    ...
    @Override
    public boolean isActive() {
      String answer = "false";
      if( protocol == ProtocolType.tcp  ) {
        answer = sendRequestOnConnection(isActive.toString());
      }
      return answer.equals( "true" );
    }

  }

 

+++++++++++++++++++++++++++++++++++++++++++++++
Architettura con contesto
+++++++++++++++++++++++++++++++++++++++++++++++


Avvaledoci dei componenti introdotti in precedenza, costruiamo un sistema che abbia il Controller (e il radar) su PC
e i dispositivi sul Raspberry, secondo l'architettura mostrata in figura:


.. image:: ./_static/img/Radar/sysDistr1.PNG
   :align: center 
   :width: 80%

Si veda (nel package ``it.unibo.radarSystem22_4.appl.main``):

- ``RadarSystemMainDevsCtxOnRasp`` : da attivare sul Raspberry 
- ``RadarSystemMainWithCtxOnPc`` : da attivare sul PC
 
+++++++++++++++++++++++++++++++++++++++++++++++
Deployoment del prototipo con contesto
+++++++++++++++++++++++++++++++++++++++++++++++

Simile a quanto fatto in :ref:`SPRINT1: Deployment su RaspberryPi`. Il comando:

.. code:: 

  gradle distZip jar -x test

crea il file `build\distributions\it.unibo.radarSystem22_4-1.0.zip`.  


-----------------------------------------------
Problemi ancora aperti  
-----------------------------------------------

- Un handler lento o che si blocca, rallenta o blocca la gestione dei messaggi da parte del
  ``ContextMsgHandler`` e quindi del :ref:`TcpContextServer<TcpContextServer>`.
- Nel caso di componenti con stato utlizzabili da più clienti, vi possono essere problemi di concorrenza.
  
Per un esempio, si consideri un contatore (POJO) che effettua una operazione di decremento rilasciando il controllo 
prima del completamento della operazione. 
  
.. code:: java

  public class CounterWithDelay {
    private int n = 2;
    public void inc() { n = n + 1; }
    public void dec(int dt) {	
      int v = n;
      v = v - 1;
      ColorsOut.delay(dt);  //the control is given to another client
      ColorsOut.out("Counter resumes v= " + v);
      n = v;
      ColorsOut.out("Counter new value after dec= " + n);
    }
  }
  
.. image:: ./_static/img/Radar/CounterDelayHandler.PNG
   :align: center  
   :width: 70%




.. code:: java

  public class CounterApplHandler extends ApplMsgHandler {
  private CounterWithDelay counter;

  public CounterApplHandler( String name ) { 
    super(name);
    this.counter = counter;
  }

  @Override
  public void elaborate(String message, Interaction2021 conn) {
    ColorsOut.out(name + " | (not used) elaborate cmd: "+cmd);  
  }
  @Override
  public void elaborate(ApplMessage message, Interaction2021 conn) {
    elaborateForObject( msg );
  }

  protected void elaborateForObject( IApplMessage msg  ) {
  String answer=null;
  try {
    String cmd =  msg.msgContent();
    int delay   = getDecDelayArg(cmd);
    counter.dec(delay);	
    answer = ""+counter.getVal();
    if( msg.isRequest() ) {
      IApplMessage  reply = CommUtils.prepareReply(msg, answer);
      sendAnswerToClient(reply.toString());			
    }
  }catch( Exception e) {}	
  }

  /*
  Il messaggio completo è
    msg( dec, dispatch, main, counter, dec(DELAY), 1)
  Quindi il payload è una String che denita un termine Prolog
    dec(DELAY)
  */
  protected int getDecDelayArg(String cmd) throws Exception{
    Struct cmdT     = (Struct) Term.createTerm(cmd);
    String cmdName  = cmdT.getName();
    if( cmdName.equals("dec")) {
      int delay = Integer.parseInt(cmdT.getArg(0).toString());
      return delay;
    }else return 0;		
  }


La chiamata al contatore può essere effettuata da un Proxy che invia un messaggio
   
   ``msg( cmd, dispatch, main, counter, dec(DELAY), 1)``

con ``DELAY`` fissato a un certo valore.
Ad esempio:

.. code:: java

  String delay = "50"; 
  ApplMessage msgDec = new ApplMessage(
      "msg( dec, dispatch, main, counter, dec(DELAY), 1)"
      .replace("DELAY", delay));

  ProxyAsClient client1 = 
    new ProxyAsClient("client1","localhost",""+ctxServerPort,ProtocolType.tcp).
  client1.sendCommandOnConnection(msgDec.toString());

Il programma ``SharedCounterExampleMain`` crea due chiamate di questo tipo una di seguito all'altra. 
Con delay basso (ad esempio ``delay="0";``) il comportamento è corretto (e il contatore va a 0), 
ma con ``delay="50";`` si vede che il decremento non avviene (il contatore si fissa a 1).
 
Questi problemi possono essere evitati sostituendo il POJO ``CounterWithDelay`` con un 
attore:

.. image:: ./_static/img/Radar/CounterWithDelayActor.PNG
   :align: center  
   :width: 70%