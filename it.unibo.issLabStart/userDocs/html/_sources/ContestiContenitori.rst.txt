.. role:: red 
.. role:: blue 
.. role:: remark
  
.. _tuProlog: https://apice.unibo.it/xwiki/bin/view/Tuprolog/

==================================================
Contesti-contenitori
==================================================

Nella versione attuale, ogni enabler *tipo server* attiva un ``TCPServer`` su una propria porta.

.. image::  ./_static/img/Radar/EnablersLedSonar.PNG
  :align: center 
  :width: 70%


Una ottimizzazione delle risorse può essere ottenuta introducendo :blue:`un solo TCPServer` per ogni nodo
computazionale. Questo server (che denominiamo ``TcpContextServer``) 
verrebbe a costituire una sorta di ``Facade`` comune a tutti gli :ref:`ApplMsgHandler<IApplMsgHandler>` 
disponibili su quel nodo.

.. *enabler-server* attivati nello stesso :blue:`contesto` rappresentato da quel  nodo.

.. image::  ./_static/img/Radar/TcpContextServerSonarLed.PNG
  :align: center 
  :width: 50%

 
Per realizzare questa ottimizzazione, il ``TcpContextServer`` deve essere capace di sapere per quale
componente è destinato un messaggio, per poi invocarne l'appropriato :ref:`IApplMsgHandler<IApplMsgHandler>`
(come :ref:`LedApplHandler` e :ref:`SonarApplHandler`).

.. _msgApplicativi:

-------------------------------------------------------
Struttura dei messaggi applicativi
-------------------------------------------------------

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

I messaggi scambiati sono logicamente suddivisi in diverse categorie:

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
   

.. _ApplMessage:

++++++++++++++++++++++++++++++++++++++++++++++++
La classe ApplMessage
++++++++++++++++++++++++++++++++++++++++++++++++

La classe ``ApplMessage`` fornisce metodi per la costruzione e la gestione di messaggi organizzati
nel modo descritto. La classe si avvale del supporto del tuProlog_.

 .. code:: java

   public class ApplMessage {
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



.. _IApplMsgHandlerEsteso:

-----------------------------------------------------------------------
Estensione della interfaccia :ref:`IApplMsgHandler<IApplMsgHandler>`
-----------------------------------------------------------------------

In relazione alla nuova esigenza, provvediamo ad estendere il contratto relativo ai gestori dei messaggi di
livello applicativo.

.. code:: Java

  public interface IApplMsgHandler {
    ...
    public void elaborate( ApplMessage message, Interaction2021 conn ); 
  }

Di conseguenza, introduciamo nella classe astratta :ref:`ApplMsgHandler<ApplMsgHandler>`  
un nuovo metodo ``abstract  elaborate( ApplMessage message, Interaction2021 conn )`` 
che dovrà essere definto dalle classi specializzate.


.. _IApplIntepreterEsteso:

-------------------------------------------------------------------------------
Estensione della interfaccia :ref:`IApplIntepreter<IApplIntepreterNoCtx>`
-------------------------------------------------------------------------------

In modo analogo estendiamo il contratto relativo alla interpretazione dei messaggi:

.. code:: java

  public interface IApplIntepreter {
    ...
    public String elaborate( ApplMessage message );    
  }


.. _TcpContextServer:

-------------------------------------------------------
Il TcpContextServer
-------------------------------------------------------

Il ``TcpContextServer`` è una specializzazione del :ref:`TcpServer<TcpServer>` che lega il campo ``userDefHandler`` 
a un gestore di messaggi (il `ContextMsgHandler`_ ) che ha il compito
di reindirizzare il messaggio ricevuto di forma ``msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )``
al gestore applicativo, sulla base dell'attributo  ``RECEIVER``.

.. image:: ./_static/img/Architectures/MessageHandlers.PNG
   :align: center 
   :width: 50%

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

-------------------------------------------------------
Il gestore di sistema dei messaggi
-------------------------------------------------------

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
   :width: 80%


:remark:`I componenti IApplMsgHandler sono semplici gestori di messaggi`

:remark:`I componenti IApplMsgHandler acquisiscono dal contesto la capacità di interazione`

 
 

  