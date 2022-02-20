.. role:: red 
.. role:: blue 
.. role:: remark
  
.. _tuProlog: https://apice.unibo.it/xwiki/bin/view/Tuprolog/

======================================================================
Il RadarSystem con :ref:`TcpContextServer<TcpContextServer>`
======================================================================

-------------------------------------------------------
Ridefinizione degli handler
-------------------------------------------------------

All'interno di ogni handler applicativo, occorre ora definire il codice del metodo `elaborate` 
previsto da :ref:`ApplMsgHandler<ApplMsgHandler>`, quando il messaggio di input è di tipo :ref:`ApplMessage<ApplMessage>`.

Prima di procedere in questo senso, introduciamo un metodo utile quando occorra inviare risposte.

++++++++++++++++++++++++++++++++++++++++++
Il metodo ``prepareReply``
++++++++++++++++++++++++++++++++++++++++++

Il metodo ``prepareReply`` viene introdotto in :ref:`ApplMsgHandler<ApplMsgHandler>` in modo da generare un 
messaggio strutturato tale che:

- l'identificativo del messaggio di risposta coincida con l'identificativo della richiesta;
- l'identificativo del destinatario sia il l'identificativo del mittente della richiesta.

.. code:: java

    protected ApplMessage prepareReply(ApplMessage message, String answer) {
    String payload = message.msgContent();
    String sender  = message.msgSender();
    String receiver= message.msgReceiver();
    String reqId   = message.msgId();
    ApplMessage reply = null;
      if( message.isRequest() ) {
         reply = Utils.buildReply(receiver,reqId,answer,message.msgSender());
      }else { //DEFENSIVE
        ColorsOut.outerr(name + " | ApplMsgHandler prepareReply ERROR...");
      }
      return reply;
    }

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
    public String elaborate( ApplMessage message ) {
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
  public void elaborate( ApplMessage message, Interaction2021 conn ) {
    if( message.isRequest() ) {
      String answer = ledInterpr.elaborate(message);
      sendMsgToClient( answer, conn );
    }else {
      ledInterpr.elaborate( message.msgContent() ); 
    }	
  }


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
  public String elaborate(ApplMessage message) {
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
  public void elaborate( ApplMessage message, Interaction2021 conn ) {
    String payload = message.msgContent();
      if( message.isRequest() ) {
        String answer = sonarIntepr.elaborate(message);
        sendMsgToClient( answer, conn );
      }else sonarIntepr.elaborate( message.msgContent() ); 
  }


-------------------------------------------------------
Ridefinizione dei client Proxy
-------------------------------------------------------

Introduciamo un nuovo parametro di configurazione per indicare l'uso del  :ref:`TcpContextServer<TcpContextServer>`:

.. code:: java
  
  RadarSystemConfig.withContext = true;

Ridefiniamo i client definiti in precedenza (come ad esempio :ref:`SonarProxyAsClient<SonarProxyAsClientNoContext>`)
in modo  da inviare messaggi di tipo :ref:`ApplMessage<ApplMessage>`, quando la configurazione 
*RadarSystemConfig.withContext* specifica che usiamo :ref:`TcpContextServer`:

Ad esempio, ridefiniamo il Proxy del Led (il caso del Sonar è analogo) tenendo anche conto 
della possibilità di usare altri protocolli (in particolare CoAP e MQTT) :

.. _LedProxyAsClient:

.. code::   java

  public class LedProxyAsClient extends ProxyAsClient implements ILed {
    public LedProxyAsClient( String name, String host, String entry,
                       ProtocolType protocol  ) {
      super(name,host,entry, protocol);
    }

    @Override
    public void turnOn() { 
        if( Utils.isTcp() && RadarSystemConfig.withContext ) {
        sendCommandOnConnection(Utils.turnOnLed.toString());
      }
      else if( Utils.isMqtt() ) {
        sendCommandOnConnection(Utils.turnOnLed.toString());
      }
      else if( Utils.isCoap() ) {
        sendCommandOnConnection( "on" );
      }else //CASO DI DEFAULT
        sendCommandOnConnection( "on" );
    }

    @Override
    public void turnOff() {   
      if( Utils.isTcp() && RadarSystemConfig.withContext){
        sendCommandOnConnection(Utils.turnOffLed.toString());
      }
      else if( Utils.isMqtt() ) {
        sendCommandOnConnection(Utils.turnOffLed.toString());
      }
      else if( Utils.isCoap() ) {
        sendCommandOnConnection( "off" );
      } else  //CASO DI DEFAULT
        sendCommandOnConnection( "off" );
    }

    @Override
    public boolean getState() {   
      String answer="";
      if( Utils.isTcp() && RadarSystemConfig.withContext){
        answer = sendRequestOnConnection(
          Utils.buildRequest(name, "query", "getState", "led").toString()) ;
      }
        else if( Utils.isMqtt() )  
          answer = sendRequestOnConnection(
            Utils.buildRequest(name, "query", "getState", "led").toString());
      else { //CASO DI DEFAULT
        answer = sendRequestOnConnection( "getState" );
      }
      return answer.equals("true");
    }
  }

I metodi ``sendCommandOnConnection`` e ``sendRequestOnConnection`` sono definiti in :ref:`ProxyAsClient`.


.. _messaggiAppl:

+++++++++++++++++++++++++++++++++++++++++++++
Definizione dei messaggi come ``ApplMessage``
+++++++++++++++++++++++++++++++++++++++++++++
 

La classe ``Utils`` fornisce metodi per la creazione dei messaggi usati dagli handler del Led e dal Sonar
usando un ``dispatch`` per i comandi e un  ``request`` per le richieste
di informazione.

 .. code:: java

  //Definizione dei Messaggi
  ApplMessage turnOnLed    = 
    new ApplMessage("msg( turn, dispatch, system, led, on, 0)");
  ApplMessage turnOffLed   = 
    new ApplMessage("msg( turn, dispatch, system, led, off, 0)");
  ApplMessage sonarActivate =  
    new ApplMessage("msg( sonarcmd, dispatch,system,sonar, activate,0)");
  ApplMessage getDistance  = 
    new ApplMessage("msg(sonarcmd,request,system,sonar, getDistance,0)");
  ApplMessage getLedState  = 
    new ApplMessage("msg(ledcmd,request,system,led,getState, 0)");
  //For simulation:
  ApplMessage fardistance  =
    new ApplMessage("msg( distance, dispatch, system, sonar, 36, 0)");
  ApplMessage neardistance =
    new ApplMessage("msg( distance, dispatch, system, sonar, 10, 0)");


.. _primoPrototipo: 

------------------------------------------
Architettura del primo prototipo
------------------------------------------


Avvaledoci dei componenti introdotti in precedenza, costruiamo un sistema che abbia il Controller (e il radar) su PC
e i dispositivi sul Raspberry, secondo l'architettura mostrata in figura:


.. image:: ./_static/img/Radar/sysDistr1.PNG
   :align: center 
   :width: 70%

I dispositivi sul Raspberry sono incapsulati in  handler che gestiscono i :ref:`Messaggi applicativi<messaggiAppl>` inviati 
loro dal :ref:`TcpContextServer<TcpContextServer>`.

Si veda:

- ``RadarSystemMainDevsCtxOnRasp`` : da attivare sul Raspberry 
- ``RadarSystemMainWithCtxOnPc`` : da attivare sul PC
 
--------------------------------------------
Deployoment del primo prototipo
--------------------------------------------

.. code:: 

  gradle build jar -x test

Crea il file `build\distributions\it.unibo.enablerCleanArch-1.0.zip` che contiene la directory bin.  


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
  
.. image:: ./_static/img/Radar/CounterWithDelay.PNG
   :align: center  
   :width: 80%




.. code:: java

  public class CounterApplHandler extends ApplMsgHandler {
  private CounterWithDelay c = new CounterWithDelay();

  public CounterApplHandler( String name ) { super(name); }

  @Override
	public void elaborate(ApplMessage message, Interaction2021 conn) {
    String answer = elaborate( msg.msgContent() );
    if( msg.isRequest() ) {
      ApplMessage  reply = Utils.prepareReply(msg, answer);
      sendAnswerToClient(reply.toString());			
    }
  }
  @Override
  public void elaborate(String message, Interaction2021 conn) {
    try {
      Struct cmdT     = (Struct) Term.createTerm(cmd);
      String cmdName  = cmdT.getName();
      if( cmdName.equals("dec")) {
        int delay = Integer.parseInt(cmdT.getArg(0).toString());
        counter.dec(delay);	
        answer = ""+counter.getVal();
			}
    }catch( Exception e) {... }	
  } 
 
  }


La chiamata al contatore può essere effettuata da un Proxy che invia un messaggio ``msg( cmd, dispatch, main, counter, dec(DELAY), 1)``
con ``DELAY`` fissato a un certo valore.
Ad esempio:

.. code:: java

  String delay = "50"; 
  ApplMessage msgDec = new ApplMessage(
      "msg( cmd, dispatch, main, counter, dec(DELAY), 1 )"
      .replace("DELAY", delay));

  ProxyAsClient client1 = 
    new ProxyAsClient("1","localhost",""+ctxServerPort,ProtocolType.tcp).
  client1.sendCommandOnConnection(msgDec.toString());

Il programma ``SharedCounterExampleMain`` crea due chiamate di questo tipo una di seguito all'altra. 
Con delay basso (ad esempio ``delay="0";``) il comportamento è corretto (e il contatore va a 0), 
ma con ``delay="50";`` si vede che il decremento non avviene (il contatore si fissa a 1).
 


