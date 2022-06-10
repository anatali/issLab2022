.. role:: red 
.. role:: blue 
.. role:: remark
  
.. _tuProlog: https://apice.unibo.it/xwiki/bin/view/Tuprolog/
.. _californium: https://www.eclipse.org/californium/
.. _paho: https://www.eclipse.org/paho/
 
.. _CoAP: https://it.frwiki.wiki/wiki/CoAP
.. _REST: https://it.frwiki.wiki/wiki/Representational_state_transfer

===============================================
Attori come risorse CoAP
===============================================

Ogni attore :ref:`ActorQak e QakActor22`  è stato definito in modo da essere anche una :blue:`risorsa CoAP osservabile`.
In particolare:

- un QAkactor di nome ``qa`` che opera in un contesto  ``ctxqa`` è una risorsa `CoAP`_ osservabile cui è possibile accedere 
  con URI path= :blue:`ctxqa/qa` nella versione ``ActorQak`` e, nella 
  :remark:`con URI path = actors/qa, nella versione QakActor22`
- lo stato corrente della risorsa può essere memorizzato come una stringa nella variabile :blue:`actorResourceRep`, 
  utilizzando l'operazione ``updateResource``.

Prima di approfondire questa nuova caratteristica, conviene richimare alcuni concetti e supporti di base per `CoAP`_.

---------------------------------------
Introduzione a CoAP
---------------------------------------
Il protocollo `CoAP`_ considera le interazioni (client/server) tra componenti come uno scambio di rappresentazioni 
di **risorse** e si pone l'obiettivo di realizzare una infrastruttura di gestione delle risorse  tramite alcune semplici
funzioni di accesso e interazione come quelle di ``HTTP``: ``PUT, POST, GET, DELETE``.


- Per interagire con una risorsa CoAP si può usare un oggetto di classe ``org.eclipse.californium.core.CoapClient`` 
  che invia richieste all'``URI`` speficato come argomento del costruttore, come ad esempio:

  .. code:: 

    "coap://"+hostaddress + ":5683/"+ resourcePath

- le risorse allocate su un nodo sono istanze della classe ``org.eclipse.californium.core.CoapResource`` 
  e sono gestite da un server di classe ``org.eclipse.californium.core.CoapServer``. Questo server realizza già
  funzioni analoghe a quelle da :ref:`IContext<Analisi del concetto di Contesto>`.

- La classe :ref:`CoapConnection`   implementa :ref:`Interaction2021<Interaction2021>` 
  e quindi realizza il nostro concetto genrale di connessione, tenendo conto delle caratteristiche del protocollo
  CoAP e della libreria `californium`_.

 
- CoAP fornisce un modello di interazione ancora punto-a-punto ma, essendo di tipo `REST`_, il suo utilizzo
  implica schemi di comunicazione simili a quelli di applicazioni Web basate su ``HTTP``, ma schemi organizzativi
  basati su **gerarchie di risorse**;
- l'uso di CoAP **modifica il modello concettuale** di riferimento per le interazioni, in quanto propone
  l'idea di accesso in lettura (``GET``) o modifica (``PUT``) a :blue:`risorse` identificate da ``URI`` attraverso un 
  unico server (che `californium`_ offre nella classe :blue:`org.eclipse.californium.core.CoapServer`).
   
  .. image:: ./_static/img/Architectures/CoapResources.png 
      :align: center
      :width: 40%

- le risorse CoAP sono organizzate in una gerarchia ad albero, come nell'esempio della figura che segue:

   .. image:: ./_static/img/Radar/CoapRadarResources.PNG
    :align: center  
    :width: 70%

  La definizione di una risorsa applicativa può essere definita come specializzazione della classe 
  :blue:`org.eclipse.californium.core.CoapResource` di `californium`_.
 

Siamo dunque di fronte a un  modello simile a quanto introdotto in  :ref:`schemaFramework`, ma con
una forte forma di :blue:`standardizzazione` sia a livello di 'verbi' di interazione (``GET/PUT/...``) sia a livello di 
organizzazione del codice applicativo (come gerarchia di risorse).

Per utilizzare il :ref:`framework iniziale<schemaFramework>` con protocollo CoAP non dovremo quindi scrivere molto 
altro codice: si veda :ref:`CoapContextServer`.

---------------------------------------
Actor22-CoAP
---------------------------------------

- Un attore ``QakActor22 extends it.unibo.kactor.ActorBasic`` ma non vive in ``it.unibo.kactor.QakContext``; vive in
  ``Qak22Context``.
- CoapApplServer: Viene creato da ``Qak22Context.InitCoap``
- Il metodo ``setActorAsLocal`` introduce un attore di nome **a** nella gerarchia delle risorse, invocando:
   
     ``CoapApplServer.getTheServer().addCoapResource(a, "actors");``

---------------------------------------
Esempio di Attore-CoAP
---------------------------------------

 Progetto: **unibo.actor22** code: *unibo.actor22.coap.TestCoap*. 


++++++++++++++++++++++++++++++++++++++++
ActorCoapDemo
++++++++++++++++++++++++++++++++++++++++

.. code:: Java

  public class ActorCoapDemo extends QakActor22FsmAnnot{
  private int n = 0;
    public ActorCoapDemo(String name) {
      super(name);
    }	
    @State( name = "init", initial=true)
    @Transition( state = "s0"   )	//empty move
    protected void init( IApplMessage msg ) {
      outInfo(""+msg );
    }
    @State( name = "s0" )
    @Transition( state = "s1" ,  msgId = SystemData.demoSysId  )
    protected void s0( IApplMessage msg ) {
      outInfo(""+msg );
      n++;   //Uncomment to go in s1
      this.updateResourceRep("n="+n);
      this.autoMsg( SystemData.demoSysCmd( getName(),getName() ) );
    }
    @State( name = "s1" )
    protected void s1( IApplMessage msg ) {
      outInfo(""+msg );
      //System.exit(0);
    }
  }

++++++++++++++++++++++++++++++++++++++++
ActorObserver for ActorCoapDemo
++++++++++++++++++++++++++++++++++++++++

.. code:: Java

  public class ActorObserver {
    private CoapObserveRelation relation = null;
    private CoapClient client = null;
    
    public ActorObserver(){
      client = new CoapClient("coap://localhost:8073/actors/a1");
    }
    
    public void  observe( ) {
      relation = client.observe(
          new CoapHandler() {
            @Override public void onLoad(CoapResponse response) {
              String content = response.getResponseText();
              ColorsOut.outappl("ActorObserver | value=" + content, ColorsOut.GREEN);
            }					
            @Override public void onError() {
              ColorsOut.outerr("OBSERVING FAILED (press enter to exit)");
            }
          });		
    }
  }


++++++++++++++++++++++++++++++++++++++++
MainDemoCoap
++++++++++++++++++++++++++++++++++++++++

.. code:: Java

  @Context22(name = "ctx", host = "localhost", port = "8073")
  @Actor22(name = "a1",contextName="ctx",implement = CoapDemo.class)
  public class MainDemoCoap {
    protected void configure() throws Exception {
      CommSystemConfig.tracing = true;
      sysUtil.INSTANCE.setTrace(true);
      Qak22Context.configureTheSystem(this);
      CommUtils.delay(1000);  //Give time to start ...
      Qak22Context.showActorNames();
      
      new ActorObserver().observe();
    }
    
    public static void main(String[] args) throws Exception   {
      CommUtils.aboutThreads("Before start - ");
      new MainDemoCoap().configure();
        CommUtils.aboutThreads("At end - ");
    }
  }