.. role:: red 
.. role:: brown
.. role:: blue 
.. role:: green
.. role:: remark
.. role:: worktodo  

.. _SpringBoot: https://spring.io/projects/spring-boot
.. _User experience design: https://it.wikipedia.org/wiki/User_experience_design

.. _IPWebcam:  https://play.google.com/store/apps/details?id=nfo.webcam&hl=it&gl=US
.. _Thymeleaf: https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html
.. _ThymeleafSyntax: https://www.thymeleaf.org/doc/articles/standardurlsyntax.html

.. _FormHTML: https://www.w3schools.com/html/html_forms.asp
.. _InputHTML: https://www.w3schools.com/tags/tag_input.asp
.. _ButtonHTML: https://www.w3schools.com/tags/tag_button.asp

.. _bannerOnline: https://manytools.org/hacker-tools/ascii-banner/
.. _Bootstrap4: https://www.w3schools.com/bootstrap4/bootstrap_get_started.asp
.. _Bootstrap5: https://www.w3schools.com/bootstrap5/
.. _Containers: https://getbootstrap.com/docs/5.0/layout/containers/
 
.. _Grids: https://www.w3schools.com/bootstrap5/bootstrap_grid_basic.php
.. _Cards: https://www.w3schools.com/bootstrap5/bootstrap_cards.php
.. _Colors: https://getbootstrap.com/docs/4.0/utilities/colors/
.. _Spacing: https://getbootstrap.com/docs/5.0/utilities/spacing/
.. _Toasts: https://www.w3schools.com/bootstrap5/bootstrap_toast.php

.. _jsdelivr: https://www.jsdelivr.com/
.. _WebJars: https://mvnrepository.com/artifact/org.webjars
.. _WebJarsExplained: https://www.baeldung.com/maven-webjars 
.. _WebJarsDocs: https://getbootstrap.com/docs/5.1/getting-started/introduction/
.. _WebJarsExamples: https://getbootstrap.com/docs/5.1/examples/
.. _WebJarsContainer: https://getbootstrap.com/docs/5.1/layout/containers/
.. _Heart-beating: https://stomp.github.io/stomp-specification-1.2.html#Heart-beating


.. _basicrobot22Gui.html: ../../../../../webRobot22/src/main/resources/templates/basicrobot22Gui.html
.. _issSpec.css: ../../../../../webRobot22/src/main/resources/static/css/issSpec.css
.. _application.properties: ../../../../../webRobot22/src/main/resources/application.properties
 

========================================
webrobot22
========================================

Lo scopo di questo lavoro è usare `SpringBoot`_  per costruire una applicazione Web che fornisca una console di comando
per  il  :ref:`BasicRobot22<Una prima architettura>`.

Procederemo in due passi:

#. Come primo passo, costruiremo la *'parte statica'* dell'applicazione che riguarda la impostazione della pagina HTML.
#. Come secondo passo, costruiremo la *'parte dinamica'* che permette all'applicazione Web di interagire da un lato 
   con utente umano (attraverso un Browser) e da un altro lato con l':ref:`attore Qak<QActor (meta)model>` 
   che realizza il  :ref:`bascirobot22<Impostazione del modello>`.

  .. image::  ./_static/img/Robot22/webRobot22ComeSistDistr.PNG
    :align: center 
    :width: 40%

 

:brown:`Creazione del progetto`

#. Iniziamo il **progetto webrobot22** :ref:`webrobot22: startup`

:brown:`Parte statica`

#. :ref:`Usiamo Bootstrap5` per impostare la pagina usando `Bootstrap5`_
#. :ref:`Visualizziamo la pagina statica`, come primo passo di un processo di `User experience design`_.

--------------------------------------
Interazioni tra i componenti
--------------------------------------

:brown:`Parte dinamica` 

#. Impostiamo l'organizzazione di un :ref:`RobotController` lato server.
#. :green:`PgToRc`: Realizziamo l'interazione sincrona :blue:`Pagina-RobotController`  via HTTP
#. :green:`RcToBr`: Realizziamo l'interazione :blue:`RobotController-basicrobot22` usando un protocollo specificato dall'utente.  
#. :green:`BrToRc`: Realizziamo l'interazione :blue:`basicrobot22-RobotController` per far giungere 
   alla applicazione Web informazioni  sullo stato del sistema.
#. :green:`RcToPg`: Realizziamo l'interazione asincrona :blue:`RobotController-Pagina` per visualizzare sulla pagina HTML 
   le informazioni sullo stato del sistema, con la mediazione del :ref:`RobotController`.

   .. image::  ./_static/img/Robot22/webRobot22Interactions.PNG
     :align: center 
     :width: 60%

In linea di princpio, una pagina HTML potrebbe anche agire come osservatore diretto (via CoAP) del :ref:`basicrobot22`.
Tuttavia, notiamo che: 

:remark:`I Browser non supportano API JavaScript per CoAP per motivi di sicurezza (legate a UDP)`

Questo rende necessario che il :ref:`RobotController` funga da mediatore tra le informazioni emesse via CoAP da 
:ref:`basicrobot22` e la pagina, attraverso le interazioni ``BrToRc`` e ``RcToPg``.

-----------------------------------------------------------
webrobot22: startup
-----------------------------------------------------------

#. Costruiamo il file ``webRobot22.zip`` in accordo a :ref:`Primi passi con SpringBoot`.
  
 
  .. image::  ./_static/img/Robot22/webRobot22Springio.PNG
    :align: center 
    :width: 70%

#. Scompattiamo il file ``webRobot22.zip``  nella nostra cartella di lavoro.
#. Modifichiamo   ``7.4.1`` in ``7.4.2`` nel file ``webRobot22\gradle\wrapper\gradle-wrapper.properties``
#. Aggiungiamo il file ``gradle.properties`` con il contenuto:

   .. code::

       kotlinVersion = 1.6.0

#. Aggiungiamo il file ``banner.txt`` in ``src\main\resources\`` usando `bannerOnline`_ (*small* font)
#. Nel file  ``application.properties`` di ``src\main\resources\`` inseriamo:

   .. code::

       spring.application.name = webRobot22
       spring.banner.location  = classpath:banner.txt
       server.port             = 8085      


+++++++++++++++++++++++++++++++++++++++++
Enable SpringBoot live DevTools
+++++++++++++++++++++++++++++++++++++++++

La feature di auto-restart mediante *Spring Developer Tools* non sembra abilitata di default
in Intellij   (come avviene invece in Eclipse).
Per provare ad attivarla manualmente, si consulti la rete, ad esempio :
https://medium.com/javarevisited/spring-boot-developer-tools-and-intellij-b16c7e5f39e4

 


++++++++++++++++++++++++++++++++++++++
build.gradle di webRobot22 
++++++++++++++++++++++++++++++++++++++

- Aggiorniamo ``build.gradle``:
 
  .. code::

    plugins {
        ...
        id 'application'
    }     

    version = '1.0'
    sourceCompatibility = '11'

    repositories {
        mavenCentral()
        flatDir {   dirs '../unibolibs'	 }
    }
    dependencies {
        ...
        //Libreria Kotlin-runtime
        implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'

        //Per comunicazioni WebSocket NOSTOMP della pagina HTML
        implementation("org.springframework:spring-websocket:5.3.14")

        //webjars
        implementation 'org.webjars:webjars-locator-core'
        implementation 'org.webjars:bootstrap:5.1.3'
        implementation 'org.webjars:jquery:3.6.0'

        /* UNIBO ********************************** */
        implementation name: 'uniboInterfaces'
        implementation name: '2p301'
	      implementation name: 'unibo.comm22-1.1'
	      implementation name: 'unibo.qakactor22-2.8'  //per ApplMessage
    }
    mainClassName = 'unibo.webRobot22.WebRobot22Application'
    jar {
        println("executing jar")
        from sourceSets.main.allSource
        manifest {
            attributes 'Main-Class': "$mainClassName"
        }
    }
 

- I `WebJars`_ sono stati introdotti in :ref:`Bootstrap e webJars`.
- La libreria ``unibo.comm22-1.1.jar`` è costruita nel progetto  :blue:`it.unibo.comm22`
- La libreria ``unibo.qakactor22-2.8`` è costruita nel progetto :ref:`QActor (meta)model`  
  e utilizza la libreria precedente per le comunicazioni.

-----------------------------------------------------------
basicrobot22Gui.html
-----------------------------------------------------------

Avvalendoci di `Bootstrap5`_, impostiamo una pagina HTML (nel file `basicrobot22Gui.html`_ in ``src/main/resources/templates``) 
in modo che presenti le aree mostrate in figura:

.. image::  ./_static/img/Robot22/webRobot22GuiStructure.PNG
  :align: center 
  :width: 70%


- :ref:`ConfigurationArea and Data`: area che include campi di input per la configurazione del sistema 
  e campi di output che mostrano i valori dei dati di configurazione fissati dall'utente.
- :ref:`RobotCmdArea`: area di input con pulsanti per inviare comandi di movimento al robot.
- :ref:`infoDisplay`: area di output  che visualizza informazioni di sistema.
- :ref:`robotDisplay`: area di output  che visualizza informazioni relative al robot o al suo ambiente.
- :ref:`Ip Webcam Android<WebcamArea>`: area di output  che visualizza lo stream prodotto da un telecamera posta su Android (ad esempio `IpWebcam`_) o su PC.
  Viene introdotta per chi non abbia un robot fisico dotato di telecamera.
- :ref:`WebCam robot<WebcamArea>`: area di output che visualizza lo stream prodotto da un telecamera posta sul robot fisico.

Avvalendoci di `Thymeleaf`_,  impostiamo la pagina come un template che presenta alcuni campi 
(*protocol, robotip, webcamip*) 
che corrispondo a quanto definito nella :ref:`Specifica dei dati applicativi`, i cui valori verranno fissati 
dal :ref:`RobotController` nella fase di costruzione della pagina (si veda :ref:`buildThePage`).

+++++++++++++++++++++++++++++++
Usiamo Bootstrap5
+++++++++++++++++++++++++++++++

Abilitiamo l'uso di `Bootstrap5`_, nella sezione ``head`` del file `basicrobot22Gui.html`_ e poi impostiamo la struttura 
del contenuto della pagina:

.. code::

   <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"> 
    <title>basicrobot22Gui</title>
    <link href=
    "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src=
    "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="css/issSpec.css">     <!-- stili custom -->
    <link rel=
    "shortcut icon" href="images/mbotIot.png" type="image/x-icon"> <!-- ICONA su browser -->
    </head>



+++++++++++++++++++++++++++++++
Contenuto della pagina
+++++++++++++++++++++++++++++++

La pagina viene suddivisa in due `Containers`_ di tipo *fluid*, uno per il titolo e uno per il contenuto vero e proprio.

.. code::

    <body>
        <div class="container-fluid pt-1 bg-primary text-white text-center">  
            <h1>basicrobot22 console</h1>
        </div>
    
        <div class="container-fluid">
            <!-- Contenuto della pagina -->
        </div>  

        <footer>
            <!-- FOOTER -->
        </footer>
    </body>

Il contenuto della pagina viene organizzato entro una riga (di ``12`` colonne, come indicato in `Grids`_ ) 
che contiene due colonne: la colonna di sinistra (di ampiezza ``7``) 
è riservata alla area di I/O, mentre la  la colonna di destra (di ampiezza ``5``)  è dedicata
alla visualizzazione degli stream di dati delle telecamere.

.. code::

    <!-- Contenuto della pagina -->
    <div class="row"> <!-- Page main row -->
        <div class="col-7">  <!-- I/O area col  -->
             <!-- CONFIGURATION Area and Data   -->
             <!-- ROBOTCmdArea                  -->
             <!-- INFO display                  -->
             <!-- ROBOT display                 -->
        </div>
        <div class="col-5">  <!-- Webcam area col  -->
            <!-- IPWebcam Android  -->
            <!-- Webcam robot      -->
        </div>
    </div> <!-- Page main row -->


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Schema delle aree di I/O
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Le aree entro le colonne sono organizzate usando le  `Cards`_ secondo lo schema:

.. code::

      <div class="card BGSTYLE TEXTCOLOR">
          <div class="card-header px-1"> ... </div>  
          <div class="card-content px-1">
               <!-- CARDCONTENT -->
          </div>
      </div>

Per le specifiche del tipo ``px-N``, si veda `Spacing`_.

Per i colori del testo (``TEXTCOLOR``) faremo riferimento agli standard `Colors`_, mentre 
per lo stile di background (``BGSTYLE``) faremo riferimento a definizioni custom.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Stili custom: issSpec.css
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La specifica degli stili custom si trova nel file `issSpec.css`_.
Tutte le definizioni iniziano con il prefisso :brown:`iss-`.




+++++++++++++++++++++++++++++++
WebcamArea
+++++++++++++++++++++++++++++++

.. image::  ./_static/img/Robot22/webRobot22GuiWebCams.PNG
  :align: center 
  :width: 30%


Riportiamo la specifica della colonna relativa all'area di output che visualizza 
gli stream (``Ip Webcam Android`` e ``WebCam robot``) prodotti dalle telecamere.


Per la visualizzazione, sfrutteremo la specifica *Protocol-relative-URL* (``th:src``) di `ThymeleafSyntax`_.


.. code::

  <div class="col-5">  <!-- webcam col -->
    <div class="card iss-bg-webcamarea px-1 border">
     <div class="card-body">
      <div class="row">
         <img class="img-fluid" 
            th:src="@{${ 'http://'+webcamip+':8080/video'} }"
            alt="androidcam" style="border-spacing: 0; border: 1px solid black;">
      </div>
       <div class="row">
         <img class="img-fluid" 
            th:src="@{${ 'http://'+robotip+':8080/?action=stream'}}"
            alt="raspicam" style="border-spacing: 0; border: 1px solid black;">
      </div>
     </div> <!-- card body -->
     </div> <!-- card -->
   </div><!-- webcam col -->

-  Il simbolo :brown:`webcamip` denota un campo del Model che viene fissato dal :ref:`RobotController` al valore immesso 
   dall'utente nella :ref:`AREA WEBCAM Android`   della sezione :ref:`ConfigurationArea and Data`.
-  Il simbolo :brown:`robotip` denota un campo del Model che viene fissato dal :ref:`RobotController` al valore immesso 
   dall'utente nella :ref:`AREA ROBOT ADDRESS`   della sezione :ref:`ConfigurationArea and Data`.


+++++++++++++++++++++++++++++++
ConfigurationArea and Data
+++++++++++++++++++++++++++++++

.. image::  ./_static/img/Robot22/webRobot22GuiConfigurationArea.PNG
  :align: center 
  :width: 30%

La parte :blue:`CONFIGURATION Area and Data` del :ref:`Contenuto della pagina`  viene organizzata come una *card* suddivisa 
in aree:

.. code::

  <!-- CONFIGURATION Area and Data   -->
   <div class="card iss-bg-inputarea">   
     <div class="card-body">
        <!-- AREA PROTOCOL       --> 
        <!-- AREA WEBCAM Android -->
        <!-- AREA ROBOT ADDRESS  -->
    </div>
   </div>

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Struttura generale delle aree di I/O
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code::

     <!-- AREA ...         --> 
     <div class="row"> 
      <div class="col-7">
        <!--   InputArea   -->
         ...
      </div>      
      <div class="col-5 text-primary">
        <!--   DataArea      -->
        ...
      </div>
     </div> <!-- row -->

- Le aree di input sono espresse mediante   `FormHTML`_ con campi `InputHTML`_.
- Quando l'utente immette un dato in una Form di input e lo invia al server, il :ref:`RobotController`
  memorizza il dato e lo ritrasmetta alla pagina aggiornando il modello con ``setConfigParams``, come
  indicato in :ref:`Interazione PgToRc (Pagina-RobotController)`.

&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
Specifica dei dati applicativi
&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

- Il file `application.properties`_ definisce i valori iniziali dei campi di input che vengono visualizzati nella
  pagina.

  .. code::

    robot22.protocol   = coap
    robot22.robotip    = not connected
    robot22.webcamip   = unknown

- I dati sono visualizzati in campi con identificatori referenziabili nel :ref:`RobotController` mediante  
  **Model**, come indicato in   :ref:`Interazione PgToRc (Pagina-RobotController)`.


Vediamo nel dettaglio le parti di I/O per la configurazione del sistema.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
AREA PROTOCOL
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code::
 
      <!--   PROTOCOL InputArea   -->
      <form action="setprotocol" method="post">
        <input type="text" size="10" id="protocolspec" name="protocol" value="coap">
        <input type="submit" value="protocol">
       </form>
 
      <!--   PROTOCOL DataArea      -->
        <b><span th:text="${protocol}">tcp</span></b>
 
Il valore immesso dall'utente viene inviato via ``HTTP-POST`` al :ref:`RobotController` che lo 
gestisce col metodo :ref:`setprotocol` memorizzando nel Model (si veda :ref:`setConfigParams`) e di qui, via `Thymeleaf`_,  
nel parametro ``protocol``  del template della pagina (si veda :ref:`buildThePage`).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
AREA WEBCAM Android
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

.. code::
  
    <!--WEBCAM Android InputArea  --> 
      <form action="setwebcamip" method="post">
         <input type="text" size="10" id="webcamspec" name="ipaddr" value="">
         <input type="submit" value="camip">
      </form>

    <!--WEBCAM Android DataArea  --> 
      <b><span th:text="${webcamip}" id="webcamipaddr">unknown</span></b>

Il valore immesso dall'utente viene inviato via ``HTTP-POST`` al :ref:`RobotController` che lo 
gestisce col metodo :ref:`setwebcamip` memorizzando nel Model (si veda :ref:`setConfigParams`) e di qui, via `Thymeleaf`_,  
nel parametro ``webcamip``  del template della pagina (si veda :ref:`buildThePage`).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
AREA ROBOT ADDRESS
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



.. code::
  
    <!-- ROBOT ADDRESS InputArea --> 
      <form action="setrobotip" method="post">
        <input type="text" size="10" id="configurespec" name="ipaddr" value="localhost">
        <input type="submit" value="robotip">
      </form>    

    <!-- ROBOT ADDRESS DataArea  --> 
       <b><span th:text="${robotip}">not connected</span></b>

Il valore immesso dall'utente viene inviato via ``HTTP-POST`` al :ref:`RobotController` che lo 
gestisce col metodo :ref:`setrobotip` memorizzando nel Model (si veda :ref:`setConfigParams`) e di qui, via `Thymeleaf`_,  
nel parametro ``robotip``  del template della pagina (si veda :ref:`buildThePage`).

+++++++++++++++++++++++++++++++
RobotCmdArea
+++++++++++++++++++++++++++++++

.. image::  ./_static/img/Robot22/webRobot22GuiRobotCmdArea.PNG
  :align: center 
  :width: 30%

Queta area presenta `ButtonHTML`_  per inviare a :ref:`RobotController` comandi per muovere il robot.


.. code::

      <div class="card  iss-bg-cmdarea text-primary"> <!--  command card -->
         <div class="card-header">
          <h6>Commands</h6>
         </div>
        <div class="card-content"> <!--  pb-4 -->
         <!--  See https://getbootstrap.com/docs/4.1/components/buttons/ -->
         <div class="row">  <!-- w,s,h commands row -->
           <div class="col"><button class="btn btn-block iss-btn-ligthblue border" id='w'>w <i>(ahead)</i></button></div> <!--class='btn btn-block btn-light-primary font-bold border' -->
           <div class="col"><button class='btn btn-block iss-btn-ligthblue  border' id='s'>s (back) </button></div>
           <div class="col"><button class='btn btn-danger  btn-block border' id='h'>h (halt) </button></div>
          </div> <!-- w,s,h commands row -->

         <div class="row"> <!-- p,l,r commands row -->
             <div class="col"><button class='btn btn-block iss-btn-ligthgreen border' id='l'>l (left)  </button></div>
             <div class="col"><button class='btn btn-block iss-btn-ligthgreen border' id='r'>r (rigth) </button></div>
             <div class="col"><button class='btn btn-warning btn-block border' id='p'>p (step) </button></div>
         </div> <!-- p,l,r commands row -->
        </div> <!-- command card-content -->
      </div> <!--  command card -->

Il conando immesso dall'utente con un *button* viene inviato via ``HTTP-POST`` al :ref:`RobotController` che lo 
gestisce col metodo :ref:`doMove`.




+++++++++++++++++++++++++++++++
infoDisplay
+++++++++++++++++++++++++++++++

.. image::  ./_static/img/Robot22/webRobot22GuiInfoDisplay.PNG
  :align: center 
  :width: 30%

.. code::

  <div class="card iss-bg-infoarea text-primary">
    <div class="card-header px-1">Info:</div>
    <div class="card-content px-1">
        <span id="display">...</span>
    </div>
  </div>

- Il contenuto del campo denotato dall'dentificatore :blue:`display` è dinamicamente modificato da :ref:`ioutils.js`
  e da :ref:`wsminimal.js<wsminimal.js in webrobo22>`.

+++++++++++++++++++++++++++++++
robotDisplay
+++++++++++++++++++++++++++++++

.. image::  ./_static/img/Robot22/webRobot22GuiRobotDisplay.PNG
  :align: center 
  :width: 30%

.. code::

  <div class="card iss-bg-robotarea text-dark">
    <div class="card-header px-1">Robot:</div>
      <div class="card-content px-1">
        <span id="robotDisplay" >...</span>
    </div>
  </div>

- Il contenuto del campo denotato dall'dentificatore ``robotDisplay`` è dinamicamente definito da da :ref:`wsminimal.js<wsminimal.js in webrobo22>`.

+++++++++++++++++++++++++++++++
Pagina finale
+++++++++++++++++++++++++++++++

.. image::  ./_static/img/Robot22/webRobot22GuiAnnot.PNG
  :align: center 
  :width: 100%


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Visualizziamo la pagina statica 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Eseguo ``gradlew bootRun`` e apro un browser su ``localhost:8085``


-------------------------------------------
Parte dinamica
-------------------------------------------

La realizzazione della parte dinamica si avvale di supporti sia nella pagina Web sia nel server:

  .. image::  ./_static/img/Robot22/webRobot22Arch.PNG
    :align: center 
    :width: 80%


- Pagina HTML: :ref:`wsminimal.js<wsminimal.js in webrobo22>`, :ref:`ioutils.js` 
  (e anche :ref:`Stili custom: issSpec.css`).
- Server: :ref:`RobotUtils.java`  

-----------------------------------------------------------
RobotController
-----------------------------------------------------------

Il Controller definisce i valori di alcune variabili locali, che comprendono gli attributi usati nel *Model*. 

Il **Model** utilizzato dal :ref:`RobotController` opera come un contenitore per dati applicativi, che vengono 
aggiornati, prima dell'invio della pagina di risposta, utilizzando il metodo :ref:`setConfigParams`.


.. code::

  @Controller 
  public class RobotController {
    protected String robotName  = "basicrobot";  
    protected String mainPage   = "basicrobot22Gui";

    //Settaggio di variabili relative ad attributi del modello
    @Value("${robot22.protocol}")
    String protocol;
    @Value("${robot22.webcamip}")
    String webcamip;
    @Value("${robot22.robotip}")
    String robotip;

    //Metodi di INTERAZIONE ...
 
    @ExceptionHandler
    public ResponseEntity handle(Exception ex) { ... }
  }

- Il *Settaggio degli attributi del modello* avviene con riferimento alla :ref:`Specifica dei dati applicativi`.


++++++++++++++++++++++++++++++++++++++++++++++++++
Interazione PgToRc (Pagina-RobotController)
++++++++++++++++++++++++++++++++++++++++++++++++++

La pagina :ref:`basicrobot22Gui.html` viene dotata di supporti utili per la interazione con il server attraverso 
codice JavaScript, contenuto nei files :ref:`ioutils.js` e :ref:`wsminimal.js<wsminimal.js in webrobo22>`.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
JavaScript di supporto nella pagina
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

&&&&&&&&&&&&&&&&&&&&&&&&&&&&
ioutils.js
&&&&&&&&&&&&&&&&&&&&&&&&&&&&

Il file  ``ioutils.js`` definisce operazioni (``setMessageToWindow`` e ``addMessageToWindow``) utili 
a inserire messaggi in aree denotate dall'argomento ``outfield``.

.. code::

  function setMessageToWindow(outfield, message) {
      var output = message.replace("\n","<br/>")
      outfield.innerHTML = `<tt>${output}</tt>`
  }

Aree in cui inserire messaggi sono ad esempio :ref:`infoDisplay` e :ref:`robotDisplay` 

.. code::

    const infoDisplay   = document.getElementById("display");
    const robotDisplay  = document.getElementById("robotDisplay");
 

Si defnisce una funzione che usa Ajax per inviare via HTTP comandi POST al server e per visualizzare 
nell'area :ref:`infoDisplay` un messaggio di successo/falimento dell'invio .

.. code::

  function callServerUsingAjax(message) {
      $.ajax({
        type: "POST",        
        url: "robotmove",        //Dove  inviare i dati        
        data: "move=" + message, //Dati da inviare
        dataType: "html",         
        success: function(msg){   
          setMessageToWindow(infoDisplay,message+" done")
        },
        error: function(){
          alert("Chiamata fallita, si prega di riprovare..."); 
        }
      });
  }

&&&&&&&&&&&&&&&&&&&&&&&&&&&&
wsminimal.js in webrobo22
&&&&&&&&&&&&&&&&&&&&&&&&&&&&

Il file `wsminimal.js`, è già stato introdotto in :ref:`RobotCleanerWeb<wsminimal.js>` e 
in :ref:`WebApplication con SpringBoot<Lo script *wsminimal.js*>`.
Esso definisce funzioni che realizzano la connessione via socket con il server e funzioni di I/O che permettono di inviare 
un messaggio al server e di visualizzare la risposta.


Si riporta qui il codice della funzione ``connect`` che crea una WebSocket usando l'URL 
``ws://SERVERHOSTIP/socket``.

.. code::

    function connect(){
      var host       =  document.location.host;
        var pathname =  "/"                    
        var addr     = "ws://" +host  + pathname + "socket"  ;
        // Assicura che sia aperta un unica connessione
        if(socket !== undefined && socket.readyState !== WebSocket.CLOSED){
             alert("WARNING: Connessione WebSocket già stabilita");
        }
        socket = new WebSocket(addr);

        socket.onopen = function (event) {
            setMessageToWindow(infoDisplay,"Connected to " + addr);
        };

        socket.onmessage = function (event) {
            console.log("ws-status:" + `${event.data}`);
            console.log(""+`${event.data}`);
            setMessageToWindow(robotDisplay,""+`${event.data}`);
        };
    }//connect

- La proprietà ``socket.onopen`` definisce l'handler invocato alla apertura della socket. Questo handler
  invoca   ``setMessageToWindow`` di :ref:`ioutils.js` per visualizzare nell'area :ref:`infoDisplay` 
  l'avventuta apertura. 
- La proprietà ``socket.onmessage`` definisce l'handler invocato alla ricezione di un messaggio. Questo handler
  invoca ``setMessageToWindow`` di :ref:`ioutils.js` per visualizzare il messaggio nell'area :ref:`robotDisplay`.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Chiamate HTTP al Controller
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%





La interazione tra il Browser che contiene la pagina HTML e il Controller della Web application
è relativa a richieste:

- di tipo :blue:`GET`, iniviata dal Browser all'inizio della connessione;
- di tipo :blue:`POST`, provenienti dalle parti di input della :ref:`ConfigurationArea and Data`.

  .. list-table:: 
    :widths: 50,50
    :width: 80%

    * -   
        .. image::  ./_static/img/Robot22/webRobot22GuiConfigCmds.PNG
           :align: center 
           :width: 100%

      -
          .. image::  ./_static/img/Robot22/webRobot22GuiRobotCmds.PNG
           :align: center 
           :width: 100%

 



 

.. code::
  
  //Metodi di INTERAZIONE ...

    @GetMapping("/") 		 
    public String entry(Model viewmodel) {
      buildThePage(viewmodel);    
    }

    //Richieste di configurazione
    @PostMapping("/setprotocol")
    public String setprotocol(Model m,@RequestParam String protocol){...}
    
    @PostMapping("/setwebcamip")
    public String setwebcamip(Model m,@RequestParam String ipaddr){...}
    
    @PostMapping("/setrobotip")
    public String setrobotip(Model m,@RequestParam String ipaddr){...}


    //Comandi al robot
    @PostMapping("/robotmove"))
    public String doMove(Model m,@RequestParam String move ){...}
 
Al termine della elaborazione di ciascuna richiesta, il Controller risponde al Browser 
come descritto in :ref:`Creazione della pagina di risposta`.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Creazione della pagina di risposta
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Il metodo  ``buildThePage`` restituisce la pagina di risposta partendo dal template referenziato 
dalla variabile ``mainPage``. Questo template include campi che vengono 
aggiornati da `Thymeleaf`_ con i valori correnti degli attibuti del Model
(metodo :ref:`setConfigParams`).  

&&&&&&&&&&&&&&&&&&&&&&&&&&&
buildThePage
&&&&&&&&&&&&&&&&&&&&&&&&&&& 

.. code::

  protected String buildThePage(Model viewmodel) {
      setConfigParams(viewmodel);
      return mainPage;
  }


&&&&&&&&&&&&&&&&&&&&&&&&&&&
setConfigParams
&&&&&&&&&&&&&&&&&&&&&&&&&&&
 
.. code::

  protected void setConfigParams(Model viewmodel){
    viewmodel.addAttribute("protocol", protocol);
    viewmodel.addAttribute("webcamip", webcamip);
    viewmodel.addAttribute("robotip",  robotip);
  }

.. const webcamip = document.getElementById("webcamip"); ELIMINATO

+++++++++++++++++++++++++++++++++++
setprotocol
+++++++++++++++++++++++++++++++++++

Il metodo ``setprotocol`` del Controller tiene traccia nel Model del protocollo che l'utente intende usare
per le interazioni tra :ref:`RobotController`  e il :ref:`basicrobot22`.

.. code::

  protected boolean usingTcp  = false;

    @PostMapping("/setprotocol")
    public String setprotocol(Model viewmodel, @RequestParam String protocol  ){
        this.protocol = protocol;
        usingTcp      = protocol.equals("tcp");
        viewmodel.addAttribute("protocol", protocol);
        return buildThePage(viewmodel);
    }


L'utente può scegliere tra ``TCP`` e ``CoAP``. In ogni caso, il protocollo CoAP viene comunque usato 
per realizzare la :ref:`Interazione BrToRc (basicrobot22-RobotController)`.


+++++++++++++++++++++++++++++++++++
setwebcamip
+++++++++++++++++++++++++++++++++++

Il metodo ``setwebcamip`` del Controller tiene traccia nel Model dell'indirzzo IP della WebCam su Android (o su PC).
Lo stream prodotto da questa WebCam (una volta attivata) viene visualizzato nella card superiore della :ref:`WebcamArea`.

.. code::

    @PostMapping("/setwebcamip")
    public String setwebcamip(Model viewmodel, @RequestParam String ipaddr  ){
        webcamip = ipaddr;
        viewmodel.addAttribute("webcamip", webcamip);
        return buildThePage(viewmodel);
    }

+++++++++++++++++++++++++++++++++++
setrobotip
+++++++++++++++++++++++++++++++++++

Il metodo ``setrobotip`` del Controller tiene traccia nel Model dell'indirzzo IP del robot (``ipaddr``) immesso dall'utente
e inizializza una connessione con :ref:`basicrobot22` usando il protocollo selezionato dall'utente con :ref:`setprotocol`.

Per realizzare le connessioni, usiamo la libreria ``it.unibo.comm2022-2.0.jar`` costruita dal progetto
``it.unibo.comm2022`` descritto in :ref:`Supporti per comunicazioni`.


In ogni caso, inizializza anche una connessione CoAP con il robot, associando ad essa un  
:ref:`RobotCoapObserver` che ha lo scopo di realizzare la :ref:`Interazione RcToPg (RobotController-Pagina)`.

 
.. code::

    @PostMapping("/setrobotip")
    public String setrobotip(Model viewmodel, @RequestParam String ipaddr  ){
        robotip = ipaddr;
         viewmodel.addAttribute("robotip", robotip);
        if( usingTcp ) RobotUtils.connectWithRobotUsingTcp(ipaddr+":8020");
        //Attivo comunque una connessione CoAP per osservare basicrobot22
        CoapConnection conn = RobotUtils.connectWithRobotUsingCoap(ipaddr+":8020");
        conn.observeResource( new RobotCoapObserver() );
        return buildThePage(viewmodel);
    }


%%%%%%%%%%%%%%%%%%%%%%%%%%%%
RobotUtils.java
%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La classe ``RobotUtils`` è una utility class che fornisce le operazioni di connessione che costruiscono
oggetti di tipo :ref:`Interaction2021` (di cui :ref:`CoapConnection` è una specializzazione).


.. code::

  public class RobotUtils {
    public static final int robotPort  = 8020;
    private static Interaction2021 conn;

   public static void connectWithRobotUsingTcp(String addr){ 
    ... 
    conn = TcpClientSupport.connect(addr, robotPort, 10);
   }
   public static CoapConnection connectWithRobotUsingCoap(String addr){ 
    ... 
    String ctxqakdest       = "ctxbasicrobot";
    String qakdestination 	= "basicrobot";
    String path   = ctxqakdest+"/"+qakdestination;
    conn = new CoapConnection(addr, path);   
    retunr (CoapConnection)  conn;
  }


++++++++++++++++++++++++++++++++++++++++++++++++++
Interazione RcToBr (RobotController-basicrobot22)
++++++++++++++++++++++++++++++++++++++++++++++++++

La interazione tra il Controller e il robot viene attivata dall'utente usando i *button* 
della :ref:`RobotCmdArea`, la cui pressione provoca la esecuzione del metodo ``doMOve``
del :ref:`RobotController`.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
doMove
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Il metodo ``doMove`` del Controller non modifica il Model, 
ma realizza la interazione con :ref:`basicrobot22`
invocando il metodo :ref:`RobotUtils.sendMsg`.

.. code::

    @PostMapping("/robotmove")
    public String doMove(Model viewmodel , @RequestParam String move ){
        try {
          RobotUtils.sendMsg(robotName,move);
        } catch (Exception e) {
            ColorsOut.outerr("RobotController | doMove ERROR:"+e.getMessage());
        }
        return mainPage;
    }

&&&&&&&&&&&&&&&&&&&&&&&&&&
RobotUtils.sendMsg
&&&&&&&&&&&&&&&&&&&&&&&&&&

.. code::

  public class RobotUtils {
   public static  IApplMessage moveAril(String robotName, String cmd  ) {
      //costruisce dispatch o request
   }
   public static void sendMsg(String robotName, String cmd){ 
        try {
            String msg =  moveAril(robotName,cmd).toString();
            conn.forward( msg );
        } catch (Exception e) {...}
   }
  }

Il metodo ``moveAril`` restituisce:

- un dispatch in relazione ai comandi :blue:`w,s,l,r,h`
- una request in relazione al comando :blue:`p` (step)

++++++++++++++++++++++++++++++++++++++++++++++++++
Interazione BrToRc (basicrobot22-RobotController)
++++++++++++++++++++++++++++++++++++++++++++++++++

Avviene sfruttando il fatto che   :ref:`basicrobot22` è una risorsa *CoAP-observable*
(si veda :ref:`Attori come risorse CoAP`) che aggiorna i suoi *CoAP-observers* utilizzando
l'operazione built-in *updateResource* per le  informazioni connesse al :ref:`requisito  *observable*`.

Per ricevere e gestire queste informazioni (messaggi di stato), viene introdotto il POJO *RobotCoapObserver*.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
RobotCoapObserver
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Il *RobotCoapObserver* è un POJO che usa :ref:`WebSocketConfiguration<Configurazione con WebSocketConfigurer>` 
per inviare i messaggi di stato a tutti i client HTTP connessi al server.


.. code::

  public class RobotCoapObserver implements CoapHandler{

      @Override
      public void onLoad(CoapResponse response) {
          //send info over the websocket
          WebSocketConfiguration.wshandler.sendToAll(
            "" + response.getResponseText());
      }

      @Override
      public void onError() { ... }
  }



++++++++++++++++++++++++++++++++++++++++++++++++++
Interazione RcToPg (RobotController-Pagina)
++++++++++++++++++++++++++++++++++++++++++++++++++

Il :ref:`RobotCoapObserver` si avvake del ``WebSocketHandler`` gia introdotto 
in :ref:`WebApplication con SpringBoot<Il gestore WebSocketHandler>` per 
inviare i messaggi di stato a tutti i client HTTP connessi al server.

.. code::

  public class WebSocketHandler extends AbstractWebSocketHandler implements IWsHandler {
    ...
    public void sendToAll(TextMessage message) throws IOException{
        Iterator<WebSocketSession> iter = sessions.iterator();
        while( iter.hasNext() ){
            iter.next().sendMessage(message);
        }
    }
  }











