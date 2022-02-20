.. role:: red 
.. role:: blue 
.. role:: remark

.. _pattern-proxy: https://it.wikipedia.org/wiki/Proxy_pattern

.. _port-adapter: https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)

.. _CoAP: https://coap.technology/

.. _grammatica regolare: https://it.wikipedia.org/wiki/Grammatica_regolare

.. _BackusNaur Form : https://it.wikipedia.org/wiki/Backus-Naur_Form

.. _pattern interpreter : https://it.wikipedia.org/wiki/Interpreter_pattern

.. _test di Turing : https://it.wikipedia.org/wiki/Test_di_Turing

.. _coroutine Kotlin : https://kotlinlang.org/docs/coroutines-overview.html

=====================================================
Abilitatori di comunicazione
=====================================================

L'analisi del problema ha posto in evidenza (si veda :ref:`concettodienabler`) 
la opportunità/necessità,
di introdurre nel sistema degli :blue:`enabler`, che hanno lo scopo di fornire funzionalità
di ricezione/trasmissione di informazione su rete a un nucleo di 
*core-code* incapsulato al proprio interno.

Nell'ambito di un processo di sviluppo bottom-up in cui il procollo TCP è
la tecnologia di riferimento per le comunicazioni, risulta naturale pensare subito a 
un enabler *tipo-server* capace di ricevere richieste  da parte di client remoti (normalmente
dei Proxy).

.. due tipi di enabler: uno per ricevere (diciamo un enabler *tipo-server*) e uno per trasmettere (diciamo un enabler *tipo-client*).
 
Come suggerito nell'analisi, ponendo il ``Controller`` su PC, 
possiamo  (senza modificare il codice introdotto in :ref:`Controller<controller>`)
impostare una architettura come quella rappresentata in figura:

.. image:: ./_static/img/Radar/ArchLogicaOOPEnablersBetter.PNG 
   :align: center
   :width: 50%

Ricordando la proposta delle architetture  `port-adapter`_,  decidiamo, come progettisti,
di proseguire lo sviluppo del software del sistema con riferimento ad una architettura a livelli
rappresentata come segue:


.. image:: ./_static/img/Architectures/cleanArchCone.jpg 
   :align: center
   :width: 50%

.. _EnablerAsServer:

------------------------------------------------
Enabler tipo-server
------------------------------------------------

Iniziamo con il definire un enabler *tipo-server* che demanda la gestione dei messaggi ricevuti 
ad oggetti di una classe che implementa :ref:`IApplMsgHandler`.

.. image:: ./_static/img/Radar/EnablerAsServer.PNG
   :align: center 
   :width: 60%
 
.. code:: java

  public class EnablerAsServer{
    private static int count=1;
    protected String name;
    protected ProtocolType protocol; 
    protected TcpServer serverTcp;

    public EnablerAsServer(String name, int port,  
                       ProtocolType protocol, IApplMsgHandler handler ) {
    try {
      this.name     			= name;
      this.protocol 			= protocol;
      if( protocol != null ) setServerSupport( port, protocol, handler );
      }catch (Exception e) { ... }
    }	
    protected void setServerSupport(int port,ProtocolType protocol,
                      IApplMsgHandler handler) throws Exception{
      if( protocol == ProtocolType.tcp ) {
          serverTcp = new TcpServer( "EnabSrvTcp_"+count++, port, handler );        
      }else if( protocol == ... ) { ...  }
      ...
    }	 
    public void activate() {
      if( protocol == ProtocolType.tcp ) {
        serverTcp.activate();
      }else  ...	
    }   
  public void deactivate() {
      if( protocol == ProtocolType.tcp ) {
        serverTcp.deactivate();
      }else ...
    }   
  }

Notiamo che:

- nel caso ``protocol==null``, non viene creato alcun supporto. 
  Questo caso sarà applicato più avanti, nella sezione  :doc:`ContestiContenitori`.
- si prevede anche la possibilità di utilizzare altri protocolli.

 
.. _IApplIntepreterNoCtx:

------------------------------------------
Interpreti
------------------------------------------

Ogni enabler deve ricevere in ingresso un gestore  applicativo (handler) che implementa :ref:`IApplMsgHandler` estendendo 
la classe :ref:`ApplMsgHandler<ApplMsgHandler>`. L'handler deve definire il metodo ``elaborate`` che gestisce
i comandi o le richieste ricevute dal sever in forma di messaggi.

L'handler deve quindi fare fronte a due compiti:

#. interpretare un messagio e tradurlo in un comando o richiesta al dispositivo Sonar;
#. inviare al mittente la risposta, in caso il messaggio sia una richiesta.

Facendo riferimento al *single responsibility principle* (SRP, si veda 
`SOLID <https://it.wikipedia.org/wiki/SOLID>`_), conviene delegare il primo compito ad
un componente che non 'sappia nulla' della *dimensione interazione* e che si occupi solo della
interpretazione del messaggio. Introduciamo una interfaccia per componenti di questo tipo: 



.. code:: java

  public interface IApplIntepreter {
    public String elaborate( String message );
  }

.. _LinguaggioComando:

++++++++++++++++++++++++++++++++++++++
Linguaggio di comando
++++++++++++++++++++++++++++++++++++++

In questo nostro semplice sistema, la String message rappresenta un comando o una richiesta e segue la sintassi di
una  `grammatica regolare`_ che può essere definita mediante le seguenti regole in `BackusNaur Form`_:

.. code::

  MSG          ::=  LEDMSG   | SONARMSG 
  LEDMSG       ::=  LEDCMD   | LEDREQUEST 
  SONARMSG     ::=  SONARCMD | SONARREQUEST
  LEDCMD       ::= "on" | "off" 
  LEDREQUEST   ::=  "getState"
  SONARCMD     ::= "activate" | "deactivate"
  SONARREQUEST ::= "getDistance"   |  "isActive"   


Concettualmente, dobbiamo fare ora riferimento al `pattern interpreter`_ .
Tuttavia, la semplicità di questo linguaggio non richiede al momento approfondimenti di tecniche
per il riconoscimento e la esecuzione di frasi: basteranno dei semplici test su stringhe,
come vederemo nelle sezioni successive.

Occorre però segnalare un punto importante: stiamo introducendo l'idea che si possa interagire 
con un componente software (nel nostro caso con un dispositivo di I/O) 'parlando' con tale componente,
invece che invocarne un metodo.

Il linguaggio con cui comunicare con il componente potrebbe essere, in applicazioni future, molto
più articolato dell'attuale, tanto da porci di fronte a questioni come il famoso `test di Turing`_.

 

.. _LedApplIntepreterNoCtx:

+++++++++++++++++++++++++++++++++++++
Un interpreter per il Led
+++++++++++++++++++++++++++++++++++++

L'intepreter per il Led riconosce frasi generate da ``LEDMSG`` (si veda `LinguaggioComando`_)  e le esegue 
invocando il dispositivo rappresentato da un POJO di interfaccia :ref:`ILed<ILed>`.

.. code:: java
  
  public class LedApplIntepreter implements IApplIntepreter  {
  ILed led;
    public LedApplIntepreter(  ILed led) { this.led = led; }

    public String elaborate( String message ) {
      //Analizza message e invoca il led,
      //restituendo un risultato o una risposta
      if( message.equals("getState") ) return ""+led.getState() ;
      else if( message.equals("on")) led.turnOn();
      else if( message.equals("off") ) led.turnOff();	
      return message+"_done";
    }

.. _SonarApplIntepreterNoCtx:

+++++++++++++++++++++++++++++++++++++
Un interpreter per il Sonar
+++++++++++++++++++++++++++++++++++++

L'intepreter per il Sonar riconosce frasi generate da ``SONARMSG`` (si veda `LinguaggioComando`_) e le esegue invocando 
il dispositivo rappresentato da un POJO di interfaccia :ref:`ISonar<ISonar>`.


.. code:: java

  public class SonarApplIntepreter implements IApplIntepreter{
  private	ISonar sonar;

    public SonarApplIntepreter(ISonar sonar) { this.sonar = sonar; }    
    @Override
      public String elaborate(String message) {
      //Analizza message e invoca il Sonar 
      //restituendo un risultato o una risposta
      ...
      }
  }

 

.. I messaggi possono essere semplici sringhe oppure oggetti di tipo :ref:`ApplMessage<ApplMessage>` che introdurremto in :doc::`ApplMessage<ApplMessage>`.


------------------------------------------
Componenti per il Sonar 
------------------------------------------

.. image::  ./_static/img/Radar/EnablerProxySonar.PNG
         :align: center 
         :width: 60%


++++++++++++++++++++++++++++++++++++++++
Enabler per il Sonar
++++++++++++++++++++++++++++++++++++++++
.. list-table::
  :widths: 30,70
  :width: 100%

  * - .. image::  ./_static/img/Radar/EnablerAsServerSonar.PNG
         :align: center 
         :width: 90%
    - L'*enabler tipo server* per il Sonar è un :ref:`EnablerAsServer<EnablerAsServer>` connesso un gestore 
      applicativo :ref:`SonarApplHandler<SonarApplHandlerNoContext>` che si avvale di :ref:`SonarApplIntepreterNoCtx` per 
      trasformare messaggi in chiamate di metodi:
      
.. di tipo ``IApplMsgHandler`` che estende  la classe :ref:`ApplMsgHandler<ApplMsgHandler>` fornendo un metodo che elabora:

      - i *comandi*: ridirigendoli al sonar locale 
      - le *richieste*:  ridirigendole al sonar locale e inviando la risposta al client 



.. _SonarApplHandlerNoContext:

+++++++++++++++++++++++++++++++++++
SonarApplHandler
+++++++++++++++++++++++++++++++++++

.. code:: java

  public class SonarApplHandler extends ApplMsgHandler  {
  private IApplIntepreter sonarIntepr;

    public SonarApplHandler(String name, ISonar sonar) {
      super(name);
      sonarIntepr = new SonarApplIntepreter(sonar);
    }

    @Override
    public void elaborate(String message, Interaction2021 conn) {
      if( message.equals("getDistance") || message.equals("isActive")) {
        sendMsgToClient( sonarIntepr.elaborate(message), conn );
      }else sonarIntepr.elaborate(message);
    }
  }

.. _SonarProxyAsClientNoContext:

++++++++++++++++++++++++++++++++++++++++
Proxy per il Sonar
++++++++++++++++++++++++++++++++++++++++

.. list-table::
  :widths: 30,70
  :width: 100%

  * - .. image::  ./_static/img/Radar/SonarProxyAsClient.PNG
         :align: center 
         :width: 90%
    - Il '*proxy tipo client* per il Sonar è una specializzazione di  :ref:`ProxyAsClient` che implementa i 
      metodi di :ref:`ISonar<ISonar>` inviando comandi o richieste all'*enabler tipo server* sulla connessione 
      :ref:`Interaction2021<Interaction2021>`:


.. code:: java

  public class SonarProxyAsClient extends ProxyAsClient implements ISonar{
    public SonarProxyAsClient( 
         String name, String host, String entry, ProtocolType protocol ) {
      super( name,  host,  entry, protocol );
    }
    @Override
    public void activate() { sendCommandOnConnection("activate"); }
    @Override
    public void deactivate() { sendCommandOnConnection("deactivate"); }
    @Override
    public IDistance getDistance() {
      String answer = sendRequestOnConnection("getDistance");
      return new Distance( Integer.parseInt(answer) );
    }
    @Override
    public boolean isActive() {
      String answer = sendRequestOnConnection("isActive");
      return answer.equals( "true" );
    }
  }

 

-----------------------------------------
Componenti per il Led
-----------------------------------------
Il caso del Led è simile al caso del Sonar, sia per quanto riguarda l'enabler, sia per quanto riguarda il proxy.

.. image::  ./_static/img/Radar/EnablerProxyLed.PNG
         :align: center 
         :width: 60%

 
Riportiamo qui solo la struttura dell'handler che realizza la logica applicativa.

.. _LedApplHandlerNoContext:

+++++++++++++++++++++++++++++++++++
LedApplHandler
+++++++++++++++++++++++++++++++++++

.. code:: Java

  public class LedApplHandler extends ApplMsgHandler   {
  private IApplIntepreter ledIntepr;

    public LedApplHandler(String name, ILed led) {
      super(name);
      ledIntepr = new LedApplIntepreter(led) ;
    }
    
    @Override
    public void elaborate(String message, Interaction2021 conn) {
      if( message.equals("getState") ) 
        sendMsgToClient( ledIntepr.elaborate(message), conn );
      else ledIntepr.elaborate(message);
    }
  }

.. _testingEnablers:

-----------------------------------------
Testing degli enabler
-----------------------------------------

La procedura si setup (configurazione) del testing crea gli elementi della architettura di figura:

.. image::  ./_static/img/Radar/TestEnablers.PNG
         :align: center 
         :width: 50%


.. code::  java

  public class TestEnablersTcp {
  @Before
  public void setup() {
    RadarSystemConfig.simulation = true;
    RadarSystemConfig.ledGui     = true;
    RadarSystemConfig.ledPort    = 8015;
    RadarSystemConfig.sonarPort  = 8011;
    RadarSystemConfig.sonarDelay = 100;
    RadarSystemConfig.testing    = false;
    RadarSystemConfig.tracing    = false;
 
    //I devices
    sonar 	= DeviceFactory.createSonar();
    led     = DeviceFactory.createLed();
		
    //I server
    sonarServer = new EnablerAsServer("sonarSrv",
                RadarSystemConfig.sonarPort,
                protocol, new SonarApplHandler("sonarH", sonar) );
    ledServer   = new EnablerAsServer("ledSrv",  
                RadarSystemConfig.ledPort, 
                protocol, new LedApplHandler("ledH", led)  );
 
    //I proxy
    sonarPxy = new SonarProxyAsClient( "sonarPxy", "localhost", 
              ""+RadarSystemConfig.sonarPort, protocol );		
    ledPxy   = new LedProxyAsClient( "ledPxy",   "localhost", 
             ""+RadarSystemConfig.ledPort,   protocol );	
  }

  @After
  public void down() {
    ledServer.stop();
    sonarServer.stop();
  }	
	
 	

Il test simula il comportamento del Controller, senza RadarDisplay:

.. code::  java

	@Test 
	public void testEnablers() {
    RadarSystemConfig.DLIMIT=30;

		sonarServer.start();
		ledServer.start();
		System.out.println(" ==================== testEnablers "  );
 		
		//Simulo il Controller
 		Utils.delay(500);		
		
		//Attivo il sonar
		sonarPxy.activate();
		System.out.println("testEnablers " + sonarPxy.isActive());
		
		while( sonarPxy.isActive() ) {
			int v = sonarPxy.getDistance().getVal();
			//Utils.delay(500);
			if( v < RadarSystemConfig.DLIMIT ) {
				ledPxy.turnOn();
				boolean ledState = ledPxy.getState();
				assertTrue( ledState );	
			}
			else {
				ledPxy.turnOff();
				boolean ledState = ledPxy.getState();
				assertTrue( ! ledState );	
			}
		}		
	}

-----------------------------------------
Da POJO a gestori di messaggi
-----------------------------------------

Al termine di questa fase dello sviluppo, poniamo in evidenza alcuni punti, che potrebbero
emergere al termine di una SPRINT-review:

- i nuovi componenti-base di livello applicativo non sono più POJO, ma sono
  gestori di messaggi, come ad esempio :ref:`SonarApplHandlerNoContext`  e :ref:`LedApplHandlerNoContext`;
- i POJO originali (come :ref:`Sonar<Sonar>` e :ref:`Led<Led>`) sono stati incapsulati 
  negli handler che specializzano la  classe :ref:`ApplMsgHandler<ApplMsgHandler>`;
- i gestori di messaggi lavorano all'interno di componenti (:ref:`Enabler<EnablerAsServer>`) 
  che forniscono una infrastruttura per le comunicazioni via rete. Il codice
  che realizza gli enabler e i proxy può essere riutilizzato in altre applicazioni;
- l'attenzione dell':blue:`Application Designer` si concentra sulla definizione del metodo 
  ``elaborate`` di componenti-gestori di tipo :ref:`ApplMsgHandler<ApplMsgHandler>` 
  che ricevono dalla
  infrastruttura-enabler un oggetto (di tipo  :ref:`Interaction2021<Interaction2021>`) 
  che abilita alle interazioni via rete;
- i messaggi gestiti dagli handler sono  ``String`` di struttura non meglio specificata;

.. notiamo però che gli handler sono già predisposti per gestire messaggi più strutturati,  rappresentati  dalla classe  ``ApplMessage`` (si veda :ref:`ApplMessage`).


Il :ref:`testing degli enablers<testingEnablers>`  ha già mostrato come sia possibile affrontare 
il punto 4 del nostro :ref:`piano di lavoro<PianoLavoro>` 

-  assemblaggio dei componenti  per formare il sistema distribuito.

Tuttavia emerge un punto critico:

:remark:`introdurre un serverTCP per ogni componente potrebbe essere troppo costoso`

Un serverTCP richiede infatti la creazione di un nuovo Thread. Anche se il costo di questa
operazione potrebbe essere (notevolmente) ridotto sostituendo il Thread Java con la 
`coroutine Kotlin`_, il team di sviluppo osserva che lo si può evitare con una modifica 
non troppo complessa.


La modifica parte da questa domanda: è possibile che i gestori applicativi di messaggi (gli handler)
possano essere dotati di capacità di comunicazione avvalendosi di un *singolo serverTCP* 
per nodo computazionale?


La prossima sezione sarà dedicata alla realizzazione di questa idea, che ci farà fare
un ulteriore passo in avanti nella transizione dal paradigma ad oggetti al paradigma
a messaggi.

