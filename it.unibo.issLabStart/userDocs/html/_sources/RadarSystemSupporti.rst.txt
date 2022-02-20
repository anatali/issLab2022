.. role:: red 
.. role:: blue 
.. role:: remark

.. _pattern-proxy: https://it.wikipedia.org/wiki/Proxy_pattern

.. _tcpsupport:

===============================================
Supporti per comunicazioni 
===============================================
  
Il secondo punto del piano di lavoro (si veda :ref:`PianoLavoro`) prevede:

#. definizione di alcuni supporti TCP per componenti lato client e lato server, con l'obiettivo di
   formare un insieme riusabile anche in applicazioni future; 

Abbiamo detto che la creazione di questi supporti non è indispensabile, ma può costituire un 
elemento strategico a livello aziendale, per evitare di rifare ogni volta il codice
che permette di scambiare informazioni via rete.

Inizieremo focalizzando l'attenzione sul protocollo TCP, per verificare poi, al termine
del lavoro, la possibilità di estendere anche ad altri protocolli i supporti creati.

:remark:`il software relativo ai supporti sarà scritto in un package dedicato xxx.supports`
 

.. _tcpsupportClient:

-------------------------------------
TcpClientSupport
-------------------------------------
Introduciamo la classe ``TcpClientSupport`` con cui istanziare oggetti che stabilisccono una connessione 
su un data coppia ``IP,Port``. Il metodo  static ``connect`` restiruisce un oggetto 
che implementa l'interfaccia  :ref:`Interaction2021<conn2021>`  
e che potrà essere usato per inviare-ricevere messaggi sulla connessione.

.. code:: Java

  public class TcpClientSupport {

    public static Interaction2021 connect(
              String host,int port,int nattempts) throws Exception{
      for( int i=1; i<=nattempts; i++ ) {
        try {
          Socket socket        =  new Socket( host, port );
          Interaction2021 conn =  new TcpConnection( socket );
          return conn;
        }catch(Exception e) {
          Colors.out("Attempt to connect:" + host + " port=" + port);
          Thread.sleep(500);
        }
      }//for
      throw new Exception("Unable to connect to host:" + host);
    }
  }

Si noti che il client fa un certo numero di tentativi prima di segnalare la impossibilità di connessione.

.. _TcpConnection:

----------------------------------------------------------------------
``TcpConnection`` implementa ``Interaction2021``
----------------------------------------------------------------------

La classe ``TcpConnection`` costituisce una implementazione della interfaccia 
:ref:`Interaction2021<conn2021>`
e quindi realizza i metodi di supporto per la ricezione e la trasmissione di
messaggi applicativi sulla connessione fornita da una ``Socket``.

.. code:: Java

  public class TcpConnection implements Interaction2021{
    ...
  public TcpConnection( Socket socket  ) throws Exception { ... }
 
Le implementazione delle operazioni si riduce alla scrittura/lettura di informazione sulla Socket 
e si rimanda quindi direttamente al codice.


.. _tcpsupportServer:

-------------------------------------
TCP Server
-------------------------------------

Alla semplicità del supporto lato client si contrappone una maggior complessità lato server, in quanto
occorre:

- permettere di stabilire connenessioni con più client;
- fare in modo che si stabilisca una diversa connessione con ciascun client;
- fare in modo che i messaggi ricevuti su una specifica connessione siano elaborati da opportuno 
  codice applicativo.




.. _IApplMsgHandler:

+++++++++++++++++++++++++++++++++++++++++++
L'interfaccia ``IApplMsgHandler``
+++++++++++++++++++++++++++++++++++++++++++

Nel seguito, incapsuleremo il codice applicativo  entro oggetti che implementano l'interfaccia
``IApplMsgHandler``.

.. code:: Java

  public interface IApplMsgHandler {
    public String getName(); 
    public  void elaborate( String message, Interaction2021 conn ) ;	 
    public void sendMsgToClient(String message, Interaction2021 conn);
    public void sendAnswerToClient(String message, Interaction2021 conn);
  }


Il costruttore del TCP server avrà quindi la seguente signature:

.. code:: Java

  public TcpServer(String name,int port,IApplMsgHandler userDefHandler) 

cioè riceverà un oggetto di livello applicativo (``userDefHandler``) capace di:

- gestire i messaggi ricevuti sulla connessione :ref:`Interaction2021<conn2021>` che il server avrà stabilito con i clienti 
- inviare risposte (o altri messagi) ai clienti sulla stessa connessione.


.. _ApplMsgHandler:

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
``ApplMsgHandler`` implementa ``IApplMsgHandler``
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Per agevolare il lavoro dell'application designer, viene definita una classe astratta che 
implementa la interfaccia :ref:`IApplMsgHandler<IApplMsgHandler>`.
Questa classe realizza l'invio di messaggi ai clienti, ma
delega alle classi specializzate il compito di definire il metodo  ``elaborate`` per la gestione
dei messaggi in ingresso.

.. _msgh: 

.. code:: Java

  public abstract class ApplMsgHandler implements IApplMsgHandler{  
  protected String name;
    public ApplMsgHandler( String name ) { this.name = name; }
    
    public Interaction2021 getName(  ) {  return name;  }
    @Override
    public void sendMsgToClient( String message, Interaction2021 conn) {
      try {  
        conn.forward( message );
      }catch(Exception e){ ... }
    } 
    @Override
    public void sendAnswerToClient( String reply, Interaction2021 conn) {
        sendMsgToClient(reply, conn);
    }
    
    public abstract void elaborate(String message,Interaction2021 conn) ;
   }

.. image:: ./_static/img/Architectures/ApplMessageHandler.png 
    :align: center
    :width: 60%



.. _TCPserver:

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Il TCPserver come oggetto attivo
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

.. Mediante la classe ``TcpServer`` possiamo istanziare oggetti che realizzano un server TCP che apre una ``ServerSocket`` e gestisce la richiesta di connessione da parte dei clienti.

Il ``TcpServer`` viene definito come un Thread che definisce  metodi per essere attivato e disattivato
e il metodo ``run`` che ne specifica il funzionamento.

.. code:: Java

  public class TcpServer  extends Thread{
  private boolean stopped = true;
  private IApplMsgHandler userDefHandler;
  private int port;
  private ServerSocket serversock;

  public TcpServer(String name,int port,IApplMsgHandler userDefHandler){
    super(name);
    this.port        = port;
    this.applHandler = applHandler;
    try {
      serversock = new ServerSocket( port );
      serversock.setSoTimeout(RadarSystemConfig.serverTimeOut);
    }catch (Exception e) { 
      Colors.outerr(getName() + " | ERROR: " + e.getMessage());
    }
  }
  public void activate() {
    if( stopped ) {
      stopped = false;
      this.start();
    }
  }
  public void deactivate() {
    try {
      stopped = true;
      serversock.close();
    }catch (IOException e) {
      Colors.outerr(getName() + " | ERROR: " + e.getMessage());	
    }
  }

  @Override
  public void run() { ... }
  
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Il funzionamento del TCPserver
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Il metodo ``run`` che specifica il funzionamento del server, opera come segue:

#.  attende una richiesta di connessione;  
#.  all'arrivo della richiesta, creae un oggetto (attivo)
    di classe :ref:`TcpApplMessageHandler<tcpmsgh>` passandondogli l':ref:`ApplMessageHandler<msgh>` 
    ricevuto nel costruttore e la connessione (di tipo :ref:`Interaction2021<conn2021>`) appena stabilita.
    Questo oggetto attende messaggi sulla nuova connessione 
    e ne delega la gestione all':ref:`ApplMessageHandler<msgh>` ricevuto;
#.  torna in fase di attesa di conessione con un altro client.

.. code:: Java

  @Override
  public void run() {
  try {
    while( ! stopped ) {
      //Accept a connection				 
      Socket sock  = serversock.accept();	//1
      Interaction2021 conn = new TcpConnection(sock);
      applHandler.setConn(conn);
      //Create a message handler on the connection
      new TcpApplMessageHandler( userDefHandler, conn ); //2			 		
    }//while
  }catch (Exception e) {...}

La figura che segue mostra l'architettura che si realizza in seguito a chiamate 
da parte di due client diversi

.. image:: ./_static/img/Architectures/ServerAndConnections.png 
    :align: center
    :width: 80%
 
:remark:`Notiamo che vi può essere concorrenza nell'uso di oggetti condivisi.` 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
TcpApplMessageHandler
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La classe ``TcpApplMessageHandler`` definisce oggetti (dotati di un Thread interno) che si occupano
di ricevere messaggi su una data connessione 
:ref:`Interaction2021<conn2021>`, delegandone la gestione all':ref:`ApplMessageHandler<msgh>` ricevuto
nel costruttore.

.. _tcpmsgh: 

.. code:: Java

  public class TcpApplMessageHandler extends Thread{
  public TcpApplMessageHandler(
        IApplMsgHandler handler,Interaction2021 conn){ 
    @Override
    public void run() {
      ...
      while( true ) {
        String msg = conn.receiveMsg();
        if( msg == null ) {
          conn.close();
          break;
        } else{ handler.elaborate( msg, conn ); }
      }
    }
  }



----------------------------------------------------------------------
Una TestUnit
----------------------------------------------------------------------
Una TestUnit può essere utile sia come esempio d'uso dei suppporti, sia per chiarire le
interazioni client-server.

Per impostare la TestUnit, seguiamo le seguente :blue:`user-story`:

.. epigraph:: 

  :blue:`User-story TCP`: come TCP-client mi aspetto di poter inviare una richiesta di connessione al TCP-server
  e di usare la connessione per inviare un messaggio e per ricevere una risposta.
  Mi aspetto anche che altri TCP-client possano agire allo stesso modo senza che le
  loro informazioni interferiscano con le mie.

++++++++++++++++++++++++++++++++++++++++
Metodi before/after
++++++++++++++++++++++++++++++++++++++++

I metodi che la JUnit esegue prima e dopo ogni test attivano e disattivano il TCPServer: 

.. code:: Java

  public class TestTcpSupports {
  private TcpServer server;
  public static final int testPort = 8111; 

  @Before
  public void up() {
    server = new TcpServer(
        "tcpServer",testPort, new NaiveHandler("naiveH") );
    server.activate();		
  }

  @After
  public void down() {
    if( server != null ) server.deactivate();
  }	

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
L'handler dei messaggi applicativi ``NaiveHandler``
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

L' `ApplMsgHandler`_ associato al server è molto semplice: visualizza il messaggio ricevuto
sulla connessione e invia una risposta avvalendosi  
della connessione ereditata da ':ref:`ApplMessageHandler<msgh>`.

.. code:: Java

  class NaiveHandler extends ApplMsgHandler {
    public NaiveHandler(String name) { super(name); }
    @Override
    public void elaborate(String message, Interaction2021 conn) {
      System.out.println(name+" | elaborates: "+message);
      sendMsgToClient("answerTo_"+message, conn);	
    }
    @Override
    public void elaborate(ApplMessage message, Interaction2021 conn) {}
  }

 

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Un semplice client per i test
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Un semplice client di testing viene definito in modo che (metodo ``doWorkWithServerOn``) il client :

#. si connette al server
#. invia un messaggio
#. attende la risposta del server
#. controlla che la risposta sia quella attesa 

.. code:: Java

  class ClientForTest{
    public void doWorkWithServerOn(String name, int ntimes ) {
      try {
        Interaction2021 conn  = 
          TcpClientSupport.connect("localhost",TestTcpSupports.testPort,ntimes);//1
        String request = "hello from" + name;
        conn.forward(request);              //2
        String answer = conn.receiveMsg();  //3
        System.out.println(name + " | receives the answer: " +answer );	
        assertTrue( answer.equals("answerTo_"+ request)); //4
      } catch (Exception e) {
        fail();
      }
    }
  }

Il metodo  ``doWorkWithServerOn`` controlla che un client esegua un certo numero di tentativi ogni volta
che tenta di connettersi a un server:

.. code:: Java

  public void doWorkWithServerOff( String name, int ntimes  ) {
    try {
      connect(ntimes);
      fail(); //non deve connttersi ...
    } catch (Exception e) {
      ColorsOut.outerr(name + " | ERROR (expected)" + e.getMessage());	
    }
  }


+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Test per l'interazione senza server
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

.. code:: Java

  @Test 
  public void testClientNoServer() {
    server.deactivate(); //il server deve essere down
    new ClientForTest().doWorkWithServerOff( "clientNoServer", 3  );	
  }

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Test per l'interazione client-server
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

.. code:: Java

  @Test 
  public void testSingleClient() {
    new ClientForTest().doWorkWithServerOn( "client1",10  );		
  }
 
	
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Test con più clienti
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

.. code:: Java

  @Test 
  public void testManyClients() {
    new ClientForTest().doWorkWithServerOn("client1",10  );
    new ClientForTest().doWorkWithServerOn("client2",1 );
    new ClientForTest().doWorkWithServerOn("client3",1 );
  }	


.. L'errore da indagare:
.. .. code:: Java
.. oneClientServer | ERROR: Socket operation on nonsocket: configureBlocking



---------------------------------
RadarSystem distribuito
---------------------------------

Una prima versione distribuita del ``RadarSystem`` consiste nell'attivare tutto il sistema
sul Raspberry, lasciando sul PC solo il ``RadarDisplay``.

Per ottenere lo scopo, si può ricorrere al  pattern-proxy_ e fare in modo che
l'oggetto che realizza il caso d'uso :ref:`RadarGuiUsecase` (nella versione
:ref:`RadarSystemMainLocal` ) riceva come argomento ``radar`` un Proxy per 
il *RadarDisplay* realizzato da un TCP client che interagisce con 
un TCP-Server posto sul PC e che gestisce il  *RadarDisplay*.


.. image:: ./_static/img/radar/RadarOnPc.PNG 
    :align: center
    :width: 60%


 


+++++++++++++++++++++++++++++++++++++++++
ProxyAsClient
+++++++++++++++++++++++++++++++++++++++++


Introduciamo la classe ``ProxyAsClient`` che riceve nel costruttore:

- l'host a cui connettersi 
- la porta espressa da una *String* denominata lo``entry``
- il tipo di protocollo (:ref:`ProtocolType`) da usare

.. image:: ./_static/img/Radar/ProxyAsClient.PNG
    :align: center
    :width: 40%


.. code:: java

  public class ProxyAsClient {
    private Interaction2021 conn; 
    protected String name ;		//could be a uri
    protected ProtocolType protocol ;

    public ProxyAsClient( 
          String name, String host, String entry, ProtocolType protocol ) {
      try {
        this.name     = name;
        this.protocol = protocol;        
        setConnection(host, entry, protocol);
      } catch (Exception e) {...}
    }

    public Interaction2021 getConn() { return conn; }

Il fatto di denotare la porta del server con una *String* invece che con un *int* ci darà
la possibilità di gestire anche comunicazioni basate su altri protocolli; ad esempio per CoAP 
il parametro ``entry`` denoterà un :blue:`Uniform Resource Identifier (URI)` 
(si veda :ref:`ProxyAsClientEsteso`).

``ProxyAsClient`` definisce le seguenti operazioni:

- **setConnection**: stabilire una connessione con un server remoto dato un protocollo;
- **sendCommandOnConnection**: inviare un comando al server;
- **sendRequestOnConnection**: inviare una richiesta al server e attendere la risposta;

Il metodo ``setConnection`` effettua la connessione al server remoto in funzione del tipo di
protocollo specificato:

.. code:: java

    protected void setConnection(
          String host,String entry,ProtocolType protocol) throws Exception{
      if( protocol == ProtocolType.tcp) {
        conn = TcpClientSupport.connect(host,  Integer.parseInt(entry), 10);
      }else if( protocol == ... ) {
        conn = ...	
      }
    }

.. Nel caso di CoAP, il metodo ``setConnection`` si avvale di un supporto   ``CoapSupport``
.. che definiremo più avanti e che restituisce un oggetto di tipo ``Interaction2021`` 
.. come nel caso di TCP/UDP.

Il caso di Proxy per protocolli diversi da TCP sarà affrontato in :doc:`VersoUnFramework`.

Con riferimento ai :ref:`TipiInterazione` introdotti nella fase di analisi,
il *proxy tipo-client* definisce anche un metodo per inviare *dispatch* e un metodo per inviare
*request* con attesa di response/ack:

.. code:: java    

  protected void sendCommandOnConnection( String cmd ) {
    try {
      conn.forward(cmd);
    } catch (Exception e) {...}
  }  
  public String sendRequestOnConnection( String request )  {
    try {
      String answer = conn.request(request);
      return answer;
    }catch (Exception e) { ...; return null;}
  }

:remark:`Il ProxyAsClient così definito realizza request-response sincrone (bloccanti)`

+++++++++++++++++++++++++++++++++++++++++
Refactoring del codice su Raspberry
+++++++++++++++++++++++++++++++++++++++++

La fase di configurazione della versione :ref:`RadarSystemMainLocal` su Raspberry 
può ora essere modificata in modo da associare alla variabile *radar* un ProxyClient:

.. code:: java  

  protected void configure() {
    ...
    radar = RadarSystemConfig.RadarGuiRemote ?
              new  ProxyAsClient("radarPxy", RadarSystemConfig.pcHostAddr, ProtocolType.tcp)
              : DeviceFactory.createRadarGui();
  ...
  }

Si veda:

- *it.unibo.enablerCleanArch.main.remotedisplay.RadarSystemMainRaspWithoutRadar*  (main che implementa :ref:`IApplication`)

Per completare il sistema non rimane che definire il TCPServer da attivare sul PC


+++++++++++++++++++++++++++++++++++++++++
Un server per il RadarDisplay
+++++++++++++++++++++++++++++++++++++++++

Si veda:

- *it.unibo.enablerCleanArch.supports.TcpServer*
- *it.unibo.enablerCleanArch.supports.TcpApplMessageHandler*
- *it.unibo.enablerCleanArchapplHandler.RadarApplHandler*
- *it.unibo.enablerCleanArch.main.remotedisplay.RadarSystemMainDisplayOnPc*  (main che implementa :ref:`IApplication`)




