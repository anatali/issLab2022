.. role:: red 
.. role:: blue 
.. role:: remark

================================================
Un approccio top-down al processo
================================================

Nell'impostare l'analisi del problema posto dai requisiti, partiamo ora considerando il sistema nel suo
complesso e non dai singoli dispositivi (di input/output).

Questo 'ribaltamento' di impostazione ci induce a focalizzare l'attenzione su tre dimensioni fondamentali:

- la :blue:`struttura` del sistema, cioè di quali parti è composto;
- la :blue:`connessione/interazione` tra le parti del sistema in modo da formare un 'tutto' con precise proprietà
  non (completamente) riducibili a quelle delle singole parti;
- il :blue:`comportamento` (autonomo o indotto) di ogni singola parte in modo che siano assicurate le interazioni
  volute.

Un modo per considerare in modo unitario queste tre dimensioni è quello di impostare l':blue:`architettura`
del sistema, cerando di dare risposta a un insieme di domande fondamentali:

------------------------------------------
Quali componenti?
------------------------------------------

Quali componenti fanno sicuramente parte del sistema, considerando i requisiti? 

.. list-table::
   :width: 100%

   * - Il sistema deve possedere parti software capaci di gestire il :blue:`Sonar`, il :blue:`RadarDisplay` e il :blue:`Led`.
       Questi componenti rappresentano dispositivi di input/ouput ovvero sensori ed attuatori. 
       Ma un dispostivo di I/O non dovrebbe mai includere codice relativo alla logica applicativa.
       
       Dunque la nostra analisi ci induce a introdurre un altro componente, che denominiamo  :blue:`Controller`, 
       con l'idea i dispositivi di I/O possano  essere riusati, senza varuazioni, per fomare molti sistemi diversi 
       modificando in modo opportuno solo il ``Controller``.

------------------------------------------
Oggetti o enti attivi?
------------------------------------------

Considerando (il software relativo a) ciascun componente, questo può/deve essere visto come un :blue:`oggetto` 
che definisce operazioni attivabili con chiamate di procedura o come un 
:blue:`ente attivo` capace di comportamento autonomo?

.. list-table::
   :width: 100%

   * - Analizzando il software disponibile, possiamo dire che:
     
       -  il ``Sonar`` è un ente attivo che scrive dati su un dispositivo standard di output
       -  il ``Led`` è un oggetto  che implementa l'interfaccia
          
          .. code::  java

             interface ILed {
                  void turnOn()
                  void turnOff()
                  boolean isOn()
             }
       -  il ``radarSupport`` è un oggetto singleton che può essere usato invocando il metodo ``update``
 
Se anche il ``RadarDisplay`` fosse sul RaspberryPi, il ``Controller`` potrebbe essere definito come segue:

.. code:: java

  while True :
    d = Sonar.getVal()
    radarSupport.update( d,90 )       
    if( d <  DLIMIT )  then Led.turnOn() else Led.TurnOff()

Da un punto di vista logico, il ``Controller`` è un ente attivo 
che può operare sul PC o sul RaspberryPi (un terzo nodo è escluso).

- Nel caso il ``Controller`` operi sul PC, lo schema precedente non va più bene, 
  perchè il ``Controller`` deve poter interagire via rete con il ``Sonar``e con il ``Led``.
  Inoltre, il ``Sonar``e il ``Led`` devono essere :blue:`embedded` in qualche altro componente
  capace di ricevere/trasmettere messaggi.

- Nel caso il ``Controller`` operi sul RaspberryPi, lo schema precedente non va più bene, 
  perchè il ``Controller``  deve poter interagire via rete con il ``RadarDisplay``. 
  In questo caso il  ``RadarDisplay`` si presenta logicamente come un ente attivo capace di ricevere/trasmetter messaggi 
  utilizzando poi ``radarSupport`` per visualizzare l'informazione ricevuta dal ``Controller``.
  


------------------------------------------
Quali interazioni?
------------------------------------------
Come punto saliente della analisi condotta fino a questo punto possiamo affermare che:

:remark:`Il problema ci induce a parlare di interazioni basate su messaggi.`

.. list-table::
   :width: 100%

   * - Di fronte alla necessità di progettare e realizzare *sistemi software distribuiti*, 
       la programmazione ad oggetti comincia a mostrare i suoi limiti 
       e si richiede un :blue:`ampliamento dello spazio concettuale di riferimento`.

       A questo riguardo, può essere opportuno affrontare il passaggio :blue:`dagli oggetti agli attori` come
       passaggio preliminare per il passaggio *da sistemi concentrati a sistemi distribuiti*. 

       Affronteremo più avanti questo passaggio, dopo avere cercato di realizzare il sistema impostando
       ancora un sistema ad oggetti che utilizzano opportuni protocolli di comunicazione.



++++++++++++++++++++++++++++++++++++++
Sincrone o asincrone?
++++++++++++++++++++++++++++++++++++++


++++++++++++++++++++++++++++++++++++++
Dirette o mediate?
++++++++++++++++++++++++++++++++++++++


---------------------------------------
Quali comportamenti?
---------------------------------------

Il comportamento di ciascun componente ha ora l'obiettivo principale di :blue:`realizzare le interazioni` che
permettono alle 'parti'  di agire in modo da formare un 'tutto' (il sistema) capace di soddifare i requisiti
funzionali attraverso opportune elaborazioni delle informazioni ricevute e tramesse tra i componenti stessi.

Il ``Controller`` potrebbe essere ora definito come segue:

.. code:: java

  while True :
    invia al Sonar la richiesta di un valore d 
    invia d al RadarDisplay in modo che lo visualizzi
    if( d <  DLIMIT ) invia al Led un comando di accensione 
    else invia al Led un comando di spegnimento

Il comportamento dei disposivi è una conseguenza logica di questo.

+++++++++++++++++++++++++++++++++++++++++
Message-driven o state-based?
+++++++++++++++++++++++++++++++++++++++++


-------------------------------------
Quale architettura?
-------------------------------------



Partiamo dalla architettura logica definita dall'analisi del problema.

- Il ``Controller`` usa i disposiitivi mediante le loro interfacce (``ISonar``, ``ILed``, ``IRadarGui``) indipendentemente dal fatto
  che essi siano locali o remoti.
- Nel caso il Sonar sia remoto, l'oggetto che implementa ``ISonar`` deve essere 'di tipo server', cioè un oggetto attivo 
  che riceve i dati via rete e li rende disponibili al ``Controller`` con il metodo ``getVal()``.
- Nel caso il Led sia remoto, l'oggetto che implementa ``ILed`` deve essere 'di tipo client', cioè un oggetto   
  che trasmette via rete i comandi (``turnOn``, ``turnOff``) del ``Controller`` .


++++++++++++++++++++++++++++++++++++
Tipi di componenti
++++++++++++++++++++++++++++++++++++


.. list-table:: 
  :widths: 70,30
  :width: 100%

  * - - :blue:`Object (POJO)`: un oggetto convenzionale della oop
      - :blue:`Active Object`: oggetto con un Thread interno
      - :blue:`Actor`: un oggetto attivo con coda di messaggi
      - :blue:`Server`: un componente che offre servizi in rete
      - :blue:`RestResource`: una risorsa REST accessibile via HTTP o CoAP
   
    - .. image:: ./_static/img/Architectures/legendComponets.PNG
        :align: center
        :width: 80%

 


------------------------------------------------
La problematica della interazione
------------------------------------------------
++++++++++++++++++++++++++
Tipi di protocollo
++++++++++++++++++++++++++

La interazione tra componenti può avvenire utilizzando diversi tipi di protocollo, che possiamo
diviedere in due categorie:

- protocolli :blue:`punto-a-punto` che stabiliscono un *canale bidirezionale* tra il client e il server. Esempi
  di questo tipo di protocolli sono ``UDP, TCP, HTTP e CoAP``.
- protocolli :blue:`publish-subscribe` che si avvalgono di un mediatore (broker) tra client e server. Esempio
  di questo tipo di protocollo è sono ``MQTT`` che viene supportato da broker come ``Mosquitto e RabbitMQ``. 

.. https://www.eclipse.org/community/eclipse_newsletter/2014/february/article2.php

In questa fase ci concentremo su **protocolli punto-a-punto**, ponendoci anche l'obiettivo di 
costruire una infrastruttura software che permetta di astrarre dai dettagli dei specifici protocolli e
di stabilire una connessione di alto livello per l'invio e la ricezione dei messsaggi.

In questo modo:

- la logica applicativa risulterà indipendente dai dettagli del codice necessari per implementare interazioni
  basaate su uno specifico protocollo;
- si facilita la possibilità di modificare il protocollo di interazione lasciando inalterato il codice che
  esprime la logica applicativa;
- si contribuisce alla definizione di un ``modello`` implementabile con linguaggi di programmazione diversi e quindi utilizzabile
  in diversi contesti applicativi.

++++++++++++++++++++++++++
Tipi di messaggio
++++++++++++++++++++++++++

I messaggi scambiati verranno logicamente suddivisi in diverse categorie:

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
 
 
++++++++++++++++++++++++++++++++++++++
Una interfaccia per l'interazione
++++++++++++++++++++++++++++++++++++++             

Una connessione punto-a-punto sarà rappresentata da un oggetto che implements la seguente interfaccia, che permette di
inviare/ricevere messaggi astraendo dallo specifico protocollo:

.. code:: Java

  interface Interaction2021  {	 
    public void forward(  String msg ) throws Exception;
    public String receiveMsg(  )  throws Exception;
    public void close( )  throws Exception;
  }

Il metodo di invio è denominato ``forward`` per rendere più evidente il fatto che si tatta di una trasmissione 
di tipo  `fire-and-forget`. La stringa restituita dal metodo ``receiveMsg`` può rappresentare un 
*dispatch/invitation/event* oppure un *ack/reply*.

Si noti che l'informazione scambiata è rappresenta da una ``String`` che è un tipo di dato presente in tutti
i linguaggi di programmazione.
Non viene definito un tipo (Java)  ``Message`` perchè si vuole permettere la interazione tra client e server
scritti in linguaggi diversi.

+++++++++++++++++++++++++++++++++++
Messaggi di livello applicativo
+++++++++++++++++++++++++++++++++++

Ovviamente componenti scritti in linguaggi diversi potrenno comprendersi solo condividendo il modo di
interpretazione delle stringhe.

Per agevolare la interoperabilità dei componenti si introduce una precisa struttura delle stringhe 
che rappresentano i messaggi di livello applicativo:

.. code::  Java

  msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )

  - MSGID:identificativo del messaggio
  - MSGTYPE: tipo del message (Dispatch, Invitation,Request,Reply,Event)  
  - SENDER: nome del componente che invia il messaggio
  - CONTENT: contenuto applicativo del messaggio (detto anche payload)
  - RECEIVER:  nome del componente chi riceve il messaggio 
  - SEQNUM: numero di sequenza del messaggio

------------------------------------------------
Architettura adapter-port
------------------------------------------------

.. image:: ./_static/img/Architectures/cleanArch.jpg
  :align: center
  :width: 60%

 

++++++++++++++++++++++++++
Adapter di tipo server  
++++++++++++++++++++++++++
La classe astratta ``EnablerAsServer`` fattorizza le proprietà di tutti gli abilitatori 'di tipo server'. 

.. code:: Java

  public abstract class EnablerAsServer extends ApplMessageHandler{
        public EnablerAsServer(String name, int port) {
          super(name);
          //Invoca il metodo che inizializza il server e il supporto al protocollo da utilizzare
          try {
            setProtocolServer( port  );
          } catch (Exception e) { System.out.println(name+" ERROR " + e.getMessage() ); } 			
        }

        public abstract void setProtocolServer( int port ) throws Exception; 
        @Override //from ApplMessageHandler
        //Questo metodo deve essere definito dall'Application designer per gestire i messaggi ricevuti
        public abstract void elaborate(String message);
  }


La classe ``ApplMessageHandler`` è una  classe astratta che definisce il metodo abstract ``elaborate( String message )``.
Questo metodo dovrà essere definito nelle estensioni ella classe per realizzare la voluta  gestione dei messaggi.

.. code:: Java

  public abstract class ApplMessageHandler implements CoapHandler{  
  protected Interaction2021 conn;		//Injected
  protected String name;
    ... 
    public abstract void elaborate( String message ) ;

    public void setConn( Interaction2021 conn) { this.conn = conn; }
  }

Le istanze di questa classe ricevono per *injection* (col metodo ``setConn``)  
una connessione di tipo ``Interaction2021`` che l'application designer 
potrà utilizzare  nel metodo *elaborate* per l'invio di messaggi *ack/reply*.

++++++++++++++++++++++++++
Adapter  di tipo client
++++++++++++++++++++++++++

La classe astratta ``EnablerAsClient`` fattorizza le proprietà di tutti gli abilitatori 'di tipo client'. 
  
.. code:: Java

  public abstract class EnablerAsClient {
    private Interaction2021 conn; 
    protected String name ;	

      public EnablerAsClient( String name, String host, int port ) {
        try {
          this.name = name;
          conn = setProtocolClient(host,  port);
        } catch (Exception e) {
          System.out.println( name+"  |  ERROR " + e.getMessage());		}
      }
      
      protected abstract Interaction2021 setProtocolClient( String host, int port  ) throws Exception;
      
      protected void sendValueOnConnection( String val ) throws Exception{
        conn.forward(val);
      }
      
      public Interaction2021 getConn() {
        return conn;
      }
  }  

++++++++++++++++++++++++++
Il caso del sonar
++++++++++++++++++++++++++

Ad esempio, nel caso del sonar, definiamo un adapter che estende ``EnablerAsServer`` realizzando al contempo
l'interfaccia ``ISonar``.

Il metodo *setProtocolServer* deve attivare un server passandogli :blue:`this` in modo
che il server possa invocare il metodo *elaborate* per ogni dato ricevuto.
L'elaborazione del dato consiste nel renderlo disponibile al ``Controller`` che ha invocato una *getVal* bloccante.

.. code:: java

  public class SonarAdapterServer extends EnablerAsServer implements ISonar{
    public SonarAdapterServer( String name, int port ) { ... }
      @Override	//from EnablerAsServer
      public void setProtocolServer( int port ) throws Exception{
        //Attiva il server sulla port usando un certo protocollo (ad es. TCP)
        //Alla ricezione dei dati del sonar, il server chiama il metodo elaborate
      }	 

      @Override  //from ApplMessageHandler
      public void elaborate(String message) {
        //Elabora il valore corrente del sonar ricevuto dal server
        //rendendolo disponibile a chi invoca il metodo ISonar.getVal 
      }

      //METODI DI ISonar 
      @Override
      public void activate(){ ... }
      public void deactivate(){ ... }
      public int getVal(){ ... }
      public boolean isActive(){ ... }
  }

 

++++++++++++++++++++++++++
Il caso del led
++++++++++++++++++++++++++

Ad esempio, nel caso del Led, definiamo un adapter che estende ``EnablerAsClient`` realizzando al contempo
l'interfaccia ``ILed``.

------------------------------------------------
Dagli oggetti alle risorse
------------------------------------------------

- Gli oggetti passivi non hanno proprietà utili per la progettazione e costruzione di sistemi distributi.
- L'uso dei protocolli di comunicazione e di oggetti 'attivi' con Thread permette di colmare la lacuna
  (l'abstraction gap) ma richiede tempo e sposta l'attenzione del progettista su aspetti infrastrutturali,
  distraendolo dalle problematiche applicative.
- Lo sforzo di costruire infrastrutture di supporto alla comuncazione può essere ridotto
  cercando di costruire elementi riusabili in più applicazioni o veri e propri :blue:`framework`.
- Una volta comprese le problematiche ricorrenti, si può introdurre una nuova astrazione come elemento 
  di riferimento per la organizzazione di sistemi distribuiti. Un primo esempio è il concetto di :blue:`risorsa RESTful`
  (*REpresentational State Transfer*).

.. http://personale.unimore.it/rubrica/contenutiad/mmamei/2020/55811/N0/N0/9999

++++++++++++++++++++++++++
Risorse REST
++++++++++++++++++++++++++
Un concetto importante in REST è l'esistenza di :blue:`risorse`, intese come fonti di informazione  
a cui si può accedere tramite un identificatore detto :blue:`URI`.

Per utilizzare le risorse, i componenti software comunicano attraverso un'interfaccia standard
(di norma HTTP) per scambiare rappresentazioni di queste risorse, ovvero il documento che trasmette le informazioni.

I  'verbi' HTTP sono tipicamente usati in una API RESTful come segue:

- :blue:`GET` : restituisce una rappresentazione di una risorsa o di una parte di essa, in un formato di dato (**media type**) appropriato.
- :blue:`PUT` : modifica una risorsa o un parte di essa o, se non esiste, lo crea
- :blue:`POST` : crea una risorsa
- :blue:`DELETE` :  elimina una risorsa o una parte di essa


++++++++++++++++++++++++++
Risorse CoAP
++++++++++++++++++++++++++

In questa sezione faremo riferimento al concetto di :blue:`CoapResource` che rappresenta una risorsa REST
cui è possibile inviare (utilizzando il protocollo  :blue:`CoAP`) i diversi tipi di richieste REST, 
cui corrispondono i seguenti metodi:

- handleGET( ... )
- handlePOST( ... )
- handlePUT( ... )
- handleDELETE( ... )

Nella inplementazione *org.eclipse.californium.core* che useremo,
ciascun metodo ha una implementazione di default che risponde con il codice :blue:`4.05 (Method Not Allowed)`.
Inoltre ciascun metodo si presenta in due forme: 

- con parametro :blue:`Exchange`: usato internamente da *californium*;
- con parametro  :blue:`CoAPExchange`: usato dagli sviluppatori
  perchè "*provides a save and user-friendlier API that can be used to respond to a request*".



------------------------------------------------
Architettura del RadarSystem
------------------------------------------------

- Il RaspberryPi ospita le risorse LedResource (come dispositivo di ouput) e SonarResource (come dispositivo di input).
- Il Controller su PC accedede ai dispositivi attraverso adapter che realizzano le interfacce ILed e ISonar.
- L'adapter per accedere al Led include un CoapClient, mentre l'adapter per il Sonar è un CoAP observer.

++++++++++++++++++++++++++++++
LedResource
++++++++++++++++++++++++++++++

++++++++++++++++++++++++++++++
SonarResource
++++++++++++++++++++++++++++++

See ``CoapSonarResource`` for testing.