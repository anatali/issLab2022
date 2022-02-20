.. role:: red 
.. role:: blue 
.. role:: remark

.. _Applicazione web: https://it.wikipedia.org/wiki/Applicazione_web    

.. _WebappFrameworks: https://www.geeksforgeeks.org/top-10-frameworks-for-web-applications/

.. _Springio: https://spring.io/

.. _WebSocket: https://it.wikipedia.org/wiki/WebSocket

.. _Node.js: https://nodejs.org/it/

.. _Express: https://expressjs.com/it/

.. _CleanArchitecture: https://clevercoder.net/2018/09/08/clean-architecture-summary-review

.. _Buster: https://www.raspberrypi.com/news/buster-the-new-version-of-raspbian/

.. _Bullseye: https://www.raspberrypi.com/news/raspberry-pi-os-debian-bullseye/


==================================
RadarSystem WebGui
==================================  
.. Il progetto *it.unibo.msenabler*  è sviluppato in ``Java11`` e utilizza SpringBoot per fornire 

Vogliamo costruire una `Applicazione web`_ che soddisfi due requisiti funzionali principali:

#. (requisito :blue:`master`): permettere a un amministratore di sistema (master) di impostare attraverso una pagina web 
   (porta  ``8081``) la configurazione del'applicazione RadarSystem e di inviare comandi al Led e al Sonar;
#. (requisito :blue:`observe`): permettere a un utente di osservare lo stato dell'applicazione e dei dispositivi, 
   senza avare la possibilità di inviare comandi.
 
--------------------------------------------
Analisi del problema WebGui
--------------------------------------------

Lo sviluppo di applicazioni Web non può presceindere dall'uso di uno dei numerosi framework disponibili (si veda ad
esempio `WebappFrameworks`_). 

Per il nostro legame con Java, può essere opportuno, per ora, fare 
riferimento a `Springio`_. Tuttavia, una adeguata alternativa potrebbe essere l'uso di framework basati su 
`Node.js`_ ed `Express`_.

Sappiamo che l'uso di un framework 'risolve' problemi ricorrenti in un dominio e 
impone precise regole per l'introduzione di componenti applicativi 
nel contesto dello schema architetturale che il framework utilizza per supportare le sue funzionalità.

Nel caso dei framework nel dominio delle applicazioni Web, trovaimo un insieme di concetti e modi di operare comuni:

- l'uso di un WebServer (spesso Apache Tomcat) che rimane nascosto al livello applicativo;
- l'uso di una infrastruttura che abilita le comunicazioni Client-Server mediante il protocollo HTTP 
  e gli schemi REST di interazione;
- la possibilità di definire componenti applicati 'innestabili' nel framework seocndo precisi meccanismi. 
  SpringBoot si basa principalmente sul meccanismo delle annotazioni Java;
- il concetto di Controller come elemento-base per la gestione dei messaggi;
- l'uso di tools che agevolano la creazione dinamica di pagine HTML a partire da template con 'parametri'
  che possono essere fissati dal Controller;
- l'abilitazione all'uso delle WebSocket per interazioni asincrone con i Client  

Lo schema di funzionamento può essere rissaunto come segue:

#. un operatire umano usa un Browser per collegarsi via HTTP a una certa porta di un nodo remoto, usata come porta
   di ingresso dal WebServer
#. l'infrastruttura del framework effettua una prima gestione del messaggio in arrivo in modo da confezionare
   oggetti computazionali (richieste e/o risposte) da trasferire ad opportuni metodi del Controller applicativo 
   per agevolare la stesura del codice di gestione da parte dell'Application Designer. Spesso la perte infrastrutturale
   è organizzata secondo una pipeline che permette all'Application Designer di introdurre parti di elaborazione
   a questo livello
#. i metodi del Controller realizzano la gestione dei messaggi in funzione dei i 'verbi' HTTP (GET,PUT,POST,DELETE) 
   con cui sono stati inviati e prepara una pagina HTML di risposta, sfruttando opportuni template predefiniti di pagine.
   I parametri dei template vengono fissati utilizzando un oggetto Modello della pagina secondo un classico schema MVC.
#. il Controller restituisce la pagina alla parte infrastruttrale che l'aveva chiamato, la quale provvede a inviare
   la pagina al Client che aveva effettuato la richiesta HTTP;
#. se l'operatore umano è sostituito da una macchina (in questo caso si parla di Machine-To-Machine itneraction)
   i messaggi vengono girati a un Controller specializato per inviare ripsoste in forma di dati, molto spesso 
   in formato XML o JSon.



++++++++++++++++++++++++++++++++++++
Una possibile pagina-utente
++++++++++++++++++++++++++++++++++++

La WebGUI si potrebbe presentare, sia al master, sia a un utente observer, come segue:

.. image:: ./_static/img/Radar/msenablerGuiNoWebcam.PNG
   :align: center
   :width: 60%

La SetUp Area sarebbe utlizzabile solo dal master, così come i pulsanti di comando per il Led e per il Sonar;  
l'utente potrebbe però vedere tutto ciò che vede il master; in particolare, il campo ``action`` riporta 
l'ultime azione invocata dal master.

.. , ed eventualmente chiedere quale sia la distanza corrente misurata dal sonar.

Naturalmente, la struttura finale della pagina sarà stabilita in accordo con il committente.

++++++++++++++++++++++++++++++++++++
Architettura del sistema WebGui
++++++++++++++++++++++++++++++++++++

Dal punto di vista architetturale, il WebServer dell'applicazione Spring potrebbe eseere organizzato in due modi:

#. **caso locale**: essere attivato sul Raspberry (basato su `Buster`_ o ??`Bullseye`_ e su ``Java11``) ed interagire 
   con una applicazione Java locale;
      
#. **caso remoto**: essere attivato su un PC ed interagire con una applicazione Java remota operante sul Raspberry,
   come ad esempio :ref:`RadarSystemMainDevsOnRasp`.

In ogni caso, l'organizzazione interna del codice del WebServer dovrà essere ispirata ai principi della
`CleanArchitecture`_.

.. csv-table::  
    :align: center
    :widths: 50,50
    :width: 100% 
    
    .. image:: ./_static/img/Architectures/cleanArchCone.jpg,.. image:: ./_static/img/Architectures/cleanArch.jpg

++++++++++++++++++++++++++++++++++++
Architettura interna al server
++++++++++++++++++++++++++++++++++++

Nello sviluppo del RadarSystem, abbiamo già organizzato il software secondo il principio che  il 
flusso di controllo si origina dal **Controller** per passare poi allo **UseCase** e al **Presenter**
in accordo al principio della `inversione delle dipendenze <https://en.wikipedia.org/wiki/Dependency_inversion_principle>`_:

- :remark:`I componenti di alto livello non devono dipendere da componenti di livello più basso.`

Con queste premesse, il compito che ci attende è quello di realizzare la parte 
**Presenter** in modo da continuare a tenere separati i casi d'uso dall'interfaccia utente.

Come ogni applicazione SpringBoot, gli elementi salienti sono:

- Un WebServer Controller che si occupa della Human-Interaction  (qui denominato **HIController**) 
  che presenta all'end user una pagina HTML come quella indicata in :ref:`Una possibile pagina-utente`
- Una pagina HTML (RadarSystemUserGui.hyml) che include campi il cui valore può essere definito attraverso
  un oggetto ``org.springframework.ui.Model`` che viene trasferito a  ``HIController`` dalla infrastruttura
  Spring e gestito mediante la Java template engine ``Theamleaf``.
- Un file JavaScript (``wsNoStomp.js``) che include funzioni utili per la gestione della pagina lato client.
- Un file per l'uso delle `WebSocket`_  (``WebSocketConfiguration.java``)  che implementa l'interfaccia ``WebSocketConfigurer`` 
  di  *org.springframework.web.socket.config.annotation*.

.. - La pagina che utilillza Bootstrap è ``RadarSystemUserConsole.html``

Il requisito :blue:`observe` può essere ottenuto in due modi diversi:

  - ``HIController`` può inviare **richieste** di informazione al Raspberry (ad esempio ``getDistance`` o ``getState``)
    e presentare all'utente un nuova pagina con le risposte ottenute;
  - utilizzando il meccanismo delle `WebSocket`_, che permette l'aggiornamento automatico della pagina attraverso
    la introduzione di un observer. In questo caso, l'uso di CoAP o MQTT può rendere il compito più agevole rispetto
    a TCP, in quanto nella versione TCP abiamo introdotto solo observer locali. Con CoAP o MQTT invece non è complicato
    introdurre presso il WebServer Spring un observer che riceve dati emessi dal Raspberry.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Caso locale
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nel **caso locale**, si può pensare che il Presenter possa avvalersi di una applicazione ad-hoc
(:ref:`RadarSystemMainEntryOnRasp`) che accede direttamente ai dispositivi reali connessi al Raspberry.
 
   .. image:: ./_static/img/Radar/hexagonalOnRaspOnly.PNG
      :align: center
      :width: 60%

.. Sembra molto lento, in particolare quando si attiva la webcam.

Prima di realizzare questa nuova applicazione, conviene però fare in modo che sia ``HIController`` a indicare
l'insieme di operazioni di cui necessita.


++++++++++++++++++++++++++++++++++++
IApplicationFacade
++++++++++++++++++++++++++++++++++++

In quanto componente applicativo primario, 
il Controller-Spring relativo alla Human-interaction (``HIController``) e anche quello
relativo a una probabile futura Machine-interaction (``MIController``) impone 
che il componente di cui farà uso per realizzare i suoi use-cases obbedisca ad una
precisa interfaccia, che viene al monento definita come segue:

.. code:: java 

   public interface IApplicationFacade {  
      public void setUp( String configFile );
      public void ledActivate( boolean v );	
      public String ledState(   );
      public void sonarActivate(   );
      public boolean sonarIsactive(   );
      public void sonarDectivate(   );
      public String sonarDistance(   );	
      public void doLedBlink();
      public void stopLedBlink();
      void activateObserver(IObserver h);  
   }

Notiamo:

- la introduzione di una nuova funzionalità per il blinking del Led;
- la possibilità di attivare un observer che riceve in ingresso un oggetto (``IObserver h``) mediante il 
  quale il Controller Spring può ricevere dati da propagare usando la WebSocket.

++++++++++++++++++++++++++++++++++++++
RadarSystemMainEntryOnRasp
++++++++++++++++++++++++++++++++++++++
La realizzazione della nuova risorsa necessaria per il :ref:`Caso locale` è facilmente definibile:

.. code:: java 

    public class RadarSystemMainEntryOnRasp  implements IApplicationFacade{
    protected ISonar sonar;
    protected ILed  led ;
    protected boolean ledblinking   = false;

        @Override
        public void setUp(String configFile) {
            RadarSystemConfig.setTheConfiguration(configFile);
            if( RadarSystemConfig.sonarObservable ) 
                sonar = DeviceFactory.createSonarObservable();		
            else sonar = DeviceFactory.createSonar();
            led  = DeviceFactory.createLed();
        }
        @Override
        public void ledActivate(boolean v) {
            if( v ) led.turnOn();else led.turnOff();} 	
        @Override
        public String ledState() { return ""+led.getState(); }
        @Override
        public void doLedBlink() { ... }
        @Override
        public void stopLedBlink() {ledblinking = false;}	
        @Override
        public void sonarActivate() { sonar.activate();	}
        @Override
        public boolean sonarIsactive() { return sonar.isActive(); }
        @Override
        public void sonarDectivate() { sonar.deactivate(); }
        @Override
        public String sonarDistance() {
             return ""+sonar.getDistance().getVal(); }
    }//RadarSystemMainEntryOnRasp


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Caso remoto
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Abbiamo già osservato come l'applicazione :ref:`RadarSystemMainDevsOnRasp` possa essere
riusata come referente remoto di un WebServer allocato su PC. Il WebServer potrebbe dunque avvalersi
della applicazione :ref:`RadarSystemMainEntryOnPc` per inviare ed ottenere dal Raspberry 
(usando TCP, MQTT o CoAP) le informazioni necessarie alla attività del **Presenter**.

Il compito del Controller-Spring che si occupa della Human-Interaction (``HIController``) sarà quello di
utilizzare :ref:`RadarSystemMainEntryOnPc` per inviare i comandi immessi dal master mediante la GUI-HTML
e per introdurre nella pagina HTML le informazioni ricevute.

.. image:: ./_static/img/Radar/hexagonal.PNG
    :align: center
    :width: 70% 
 



.. il master può inviare comandi al Led e ricevere i dati del Sonar 
  
.. Per ricevere dati senza polling può essere conveniente utilizzare il meccanismo delle `WebSocket`_.

.. (si veda come lettura preliminare :ref:`WebSockets<wsintro>` in ``iss2Technologies``)  



.. una WebGui alla porta ``8081`` che permette di comandare il Led e il Sonar. 



++++++++++++++++++++++++++++++++++++++
RadarSystemMainEntryOnPc
++++++++++++++++++++++++++++++++++++++



.. code:: java 

    public class RadarSystemMainEntryOnPc  implements IApplicationFacade{
    public static final String mqttAnswerTopic  = "pctopic";
    public static final String mqttCurClient    = "pc4";

    protected ISonar sonar;
    protected ILed  led ;
    protected boolean ledblinking   = false;
    protected String serverHost = "";
	
    public RadarSystemMainEntryOnPc( String addr){
        RadarSystemConfig.raspHostAddr = addr;		
    }
    public void doJob(String configFileName) {
        setUp( configFileName );
        configure();
    }
    @Override
    public void setUp(String configFile) {
        if(configFile!=null) RadarSystemConfig.setTheConfiguration(configFile);
        else {
            RadarSystemConfig.protcolType       = ProtocolType.tcp;
            RadarSystemConfig.raspHostAddr      = "192.168.1.9";  
            RadarSystemConfig.ctxServerPort     = 8018;
            RadarSystemConfig.sonarDelay        = 1500;
            RadarSystemConfig.withContext       = true; //MANDATORY 
            RadarSystemConfig.DLIMIT            = 40;
            RadarSystemConfig.testing           = false;
            RadarSystemConfig.tracing           = true;
            RadarSystemConfig.mqttBrokerAddr    = "tcp://broker.hivemq.com";
                                    //: 1883  or  "tcp://localhost:1883" 	
        }
    }	

	@Override
	public void activateObserver(IObserver h) {
        if( Utils.isCoap()) {
            CoapClient  cli  = new CoapClient( 
                "coap://"+RadarSystemConfig.raspHostAddr+":5683/"+
                CoapApplServer.inputDeviceUri+"/sonar" );
            //CoapObserveRelation obsrelation = 
                cli.observe( new SonarObserverCoap("sonarObsCoap", h) );
        }else if( Utils.isMqtt() ) { ... }
	}

    protected void configure() {
        if(Utils.isCoap() ) { 
            serverHost       = RadarSystemConfig.raspHostAddr;
            String ledPath   = CoapApplServer.lightsDeviceUri+"/led"; 
            String sonarPath = CoapApplServer.inputDeviceUri+"/sonar"; 
            led     = new LedProxyAsClient("ledPxy", serverHost, ledPath );
            sonar   = new SonarProxyAsClient("sonarPxy",serverHost,sonarPath);
        }else {
            String serverEntry = "";
            if(Utils.isTcp() ) { 
                serverHost  = RadarSystemConfig.raspHostAddr;
                serverEntry = "" +RadarSystemConfig.ctxServerPort; 
            }
            if(Utils.isMqtt() ) { 
                MqttConnection conn = MqttConnection.createSupport(mqttCurClient);
                conn.subscribe( mqttCurClient, mqttAnswerTopic );
                serverHost  = RadarSystemConfig.mqttBrokerAddr;  //dont'care
                serverEntry = mqttAnswerTopic; 
            }				
            led   = new LedProxyAsClient("ledPxy", serverHost, serverEntry);
            sonar = new SonarProxyAsClient("sonarPxy",  serverHost, serverEntry);
        }
    }//configure

    @Override
    public void doLedBlink() {
    new Thread() {
        public void run() {
            ledblinking = true;
            while( ledblinking ) {
                ledActivate(true);
                Utils.delay(500);
                ledActivate(false);
                Utils.delay(500);
             }
        }
    }.start();			
    }
    @Override
    public void ledActivate(boolean v) {
            if( v ) led.turnOn();else led.turnOff();}
    @Override
    public String ledState() { return ""+led.getState(); }
    @Override
    public void stopLedBlink() {ledblinking = false;}	
    @Override
    public void sonarActivate() { sonar.activate();	}
    @Override
    public boolean sonarIsactive() { return sonar.isActive(); }
    @Override
    public void sonarDectivate() { sonar.deactivate(); }
    @Override
    public String sonarDistance() {return ""+sonar.getDistance().getVal();}
  	
    }//RadarSystemMainEntryOnPc 


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Testing svincolato
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Notiamo che questa parte applicativa di cui il Controller-Spring si avvale, può essere testata
senza dover attivare l'applicazione Spring.

Per ottenere questo scopo possiamo ad esempio aggiungere il codice che segue ed attivare il main
da PC:

.. code:: java 

    public void entryLocalTest(   ) {		
        ledActivate(true);
        Utils.delay(1000);
        ledActivate(false);

        sonar.activate();
        Utils.delay(2000);
		
        int d = sonar.getDistance().getVal();
        ColorsOut.out("RadarSystemMainEntryOnPc | d="+d);
		
        terminate();
	}	
    public void terminate() {
        sonar.deactivate();
        System.exit(0);
    }

    public static void main( String[] args) throws Exception {
        new RadarSystemMainEntryOnPc("192.168.1.9", null).entryLocalTest( );
    }



---------------------------------------
IApplicationFacadeWithWebcam
---------------------------------------

.. code:: java 

   public interface IApplicationFacadeWithWebcam extends IApplicationFacade{  
      public void takePhoto( String fName  );	
      public void sendCurrentPhoto();
      public void startWebCamStream(   );	
      public void stopWebCamStream(   );	
      public String getImage(String fName);
      public void storeImage(String encodedString, String fName);
    }
