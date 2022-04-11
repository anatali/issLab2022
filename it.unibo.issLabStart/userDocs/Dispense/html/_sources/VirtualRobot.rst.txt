.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _Pierfrancesco Soffritti: https://github.com/PierfrancescoSoffritti/ConfigurableThreejsApp
.. _three.js : https://threejs.org/
.. _Node.js : https://nodejs.org/it/
.. _Docker Hub: https://hub.docker.com/
.. _DDR Robot: https://www.youtube.com/watch?v=aE7RQNhwnPQ

.. _devsDdr: ./_static/devsDdr.html

.. http://faculty.salina.k-state.edu/tim/robotics_sg/Control/kinematics/unicycle.html
.. https://www.epfl.ch/labs/la/wp-content/uploads/2018/08/Kappeler.Rapport.pdf.pdf
.. https://www.youtube.com/watch?v=ZekupxukiOM  Simulatore python  install pygame  https://www.youtube.com/watch?v=zHboXMY45YU

.. _Introduction to Docker and DockerCompose: ./_static/IntroDocker22.html
.. _Introduzione a JSON-Java: https://www.baeldung.com/java-org-json
.. _I WebSocket Comunicazione Asincrona Full-Duplex Per Il Web: http://losviluppatore.it/i-websocket-comunicazione-asincrona-full-duplex-per-il-web/
.. _org.json: https://www.baeldung.com/java-org-json
.. _ws: https://www.npmjs.com/package/ws
.. _socket.io: https://socket.io/docs/v4/

==========================================
VirtualRobot
==========================================

Unibo ha sviluppato un ambiente virtuale (denominato ``WEnv`` ) che include un robot 
che simula un *Differential Drive robot* (**DDR**) reale. 

------------------------------------
Differential Drive robot 
------------------------------------

Un `DDR Robot`_ possiede due ruote motrici sullo stesso asse e una terza ruota condotta (non motrice).
La  tecnica *differential drive* consiste nel far muovere le ruote motrici a velocità
indipendenti l’una dall’altra.  

Nel seguito faremo riferimento a una forma semplificata di DDR in cui le possibìili mosse sono:

- muoversi avanti-indietro lungo una direzione costante
- fermarsi
- ruotare di 90° a destra o sinistra 

Queste mosse sono realizzate inviando opportuni comandi al robot (simulato o reale).

++++++++++++++++++++++++++++++++++++ 
Robot reali
++++++++++++++++++++++++++++++++++++ 

Per la costruzione di un DDR reale si veda `devsDdr`_.
Al momento useremo una versione simulata, che descriviamo nella sezione :ref:`WEnv`.

Lo scopo non è certo quello di affrontare i problemi di progettazione tipici di un corso di robotica, ma quello di
introdurre casi di studio non banali per la costruzione di sistemi software distribuiti **reattivi**, **proattivi** e 
**situati** in un ambiente che può essere fonte di :ref:`Eventi`.


------------------------------------
WEnv
------------------------------------

L'ambiente virtuale WEnv  è stato realizzato principalmente da `Pierfrancesco Soffritti`_ utilizzando la 
libreria JavaScript `three.js`_. Il codice è disponibile nel progetto ``it.unibo.virtualRobot2020``.
 
Per attivare WEnv:

- Installare `Node.js`_
- In ``it.unibo.virtualRobot2020\node\WEnv\server``, eseguire **npm install**
- In ``it.unibo.virtualRobot2020\node\WEnv\WebGLScene``, eseguire **npm install**
- In ``it.unibo.virtualRobot2020\node\WEnv\server\src``, eseguire **node WebpageServer.js**


+++++++++++++++++++++++++++++++++++
Usare WEnv 
+++++++++++++++++++++++++++++++++++

Aprendo un browser su  **localhost:8090**, WEnv mostrerà una scena MASTER in cui sono abilitati i **comandi da tastiera**: :blue:`w,a,s,d,h`.

Se apriamo  *'Strumenti di sviluppo'* del browser, vedremo i messaggi inviati su ``console.log`` dal codice Javascript 
(embedded nel file ``IndexOk.html``) che gestisce la scena e i comandi da tastiera.

La figura che segue mostra:

#. Il comando di attivazione di ``WebpageServer.js``
#. Il browser con ``console.log``
#. La console di un programma applicativo.


.. image::  ./_static/img/VirtualRobot/vrExplain.PNG
    :align: center 
    :width: 100%

WEnv si può attivare anche usando una immagine docker; si veda :ref:`WEnv come immagine docker`.

++++++++++++++++++++++++++++++++++++
Scene per WEnv
++++++++++++++++++++++++++++++++++++

La scena del WEnv è costruita da una descrizione che può essere facilmente definita da un progettista di applicazioni
modificando il file ``sceneConfig.js``.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
sceneConfig.js
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Un esempio (relativo alla scena della figura precedente) può essere trovato in ``sceneConfig.js``.


.. code::

    const config = {
        floor: {
            size: { x: 31, y: 24                   }
        },
        player: {
            position: { x: 0.10, y: 0.16 },		//INIT
            //position: { x: 0.5, y: 0.5 },		//CENTER
            //position: { x: 0.8, y: 0.85 },		//END
            speed: 0.2
        },
        sonars: [
        ],
        movingObstacles: [
        ],
    staticObstacles: [
            {
                name: "plasticBox",
                centerPosition: { x: 0.34, y: 0.4},
                size: { x: 0.03, y: 0.07}
            },	 		 
            {
                name: "wallUp",
                centerPosition: { x: 0.44, y: 0.97},
                size: { x: 0.88, y: 0.01}
            },
            {
                name: "wallDown",
                centerPosition: { x: 0.44, y: 0.01},
                size: { x: 0.85, y: 0.01}
            },
            {
                name: "wallLeft",
                centerPosition: { x: 0.02, y: 0.48},
                size: { x: 0.01, y: 0.94}
            },
            {
                name: "wallRight",
                centerPosition: { x: 1.0, y: 0.5},
                size: { x: 0.01, y: 0.99}
            }
        ]
    }

    export default config;

E' possibile cambiare la scena in modo interattivo con la console che compare nella scena, per poi modificare manualmente il file 
``sceneConfig.js`` per conservare le modifiche.

++++++++++++++++++++++++++++++++++++
Sensori virtuali
++++++++++++++++++++++++++++++++++++

Il robot virtuale è dotato di due sensori di impatto, uno posto davanti e uno posto nella parte posteriore del robot.

E' inoltre possibile introdurre sonar virtuali che rilevano la posizione corrente del robot (o di altri ostacoli) nella scena.

--------------------------------------------
Comandi-base per il robot in cril 
--------------------------------------------

Il linguaggio per esprimere comandi di movimento del robot virtuale 
(detto *concrete-robot interaction language* o :blue:`cril` ) può essere 
introdotto in modo analogo al :ref:`Linguaggio-base di comando` per i dispostivi del RadarSystem,
come campi di una stringa JSON della forma che segue:

.. code::

    {"robotmove":"MOVE", "time":T} 
    
    MOVE ::= "turnLeft" | "turnRight" | 
             "moveForward" | "moveBackward" | "alarm"
    T    ::= naturalNum

Ad esempio, il comando 

    ``{"robotmove":"moveForward", "time":800}`` 

muove in avanti il robot per ``800 msec``. Il significato di **"alarm"** è di fermare il robot 
(non è stato chiamato ``halt`` per motivi 'storici').

Stringhe-comando di questa forma possono essere  inviate a WEnv in due modi diversi:

- come messaggi HTTP POST inviati sulla porta **8090**
- come messaggi inviati su un websocket alla porta **8091**

++++++++++++++++++++++++++++++++
Interazioni con HTTP
++++++++++++++++++++++++++++++++

L'invio di messaggi con HTTP implica una interazione logica di tipo request-response che blocca il 
chiamante.

Dopo l'esecuzione del comando, WEnv invia al chiamante una :blue:`risposta`, espressa in JSON :

.. code::

    {"endmove":"RESULT", "move":MOVE}   
    
    RESULT ::= true | false 

Il significato dei valori di ``RESULT`` è il seguente:

- **true**: mossa completata con successo
- **false**: mossa fallita (il robot virtuale ha  incontrato un ostacolo)


Riportiamo (si veda il progetto ``unibo.wenvUsage22``) un esempio di programma Java che esegue le mosse-base del robot mediante 
comandi in :ref:`cril<Comandi-base per il robot in cril>` contenuti in richieste HTTP (di tipo POST).

.. list-table:: 
  :widths: 35,75
  :width: 100%

  * - ClientNaiveUsingPost.java
    - :blue:`Key point`: Request-response :blue:`sincrona`. 

      Richiede 1 thread.

Osserviamo che:

- Il codice di comunicazione è scritto completamente dal progettista dell'applicazione.
- Una mossa può terminare prima del tempo indicato nel comando, restituendo la risposta **false**.  
- La gestione delle risposte JSON viene eseguita utilizzando la libreria  `org.json`_ 
  (vedi anche `Introduzione a JSON-Java`_ ).
- :remark:`Non è possibile interrompere l'esecuzione di una mossa.`


++++++++++++++++++++++++++++++++
Interazioni mediante WS
++++++++++++++++++++++++++++++++

L'invio di un comando di movimento al robot (mossa) mediante WebSocket `ws`_  sulla porta **8091**  
implica una forma di comunicazione :blue:`asincrona` 
(*fire-and-forget*) che può essere seguita dall'invio al client (sulla connessione ws) di informazioni 
su variazioni dello stato del 'mondo' dopo l'esecuzione della mossa, quali:

- dati emessi dai sonar presenti nella scena, se rilevano il robot in movimento
- dati emessi dai sensori di impatto posti davanti e dietro al robot, quando rilevano un ostacolo. 

Si noti che dati relativi a sonar presenti nella scena possono essere emessi indipendentemente dalla esecuzione
di mosse del robot, ad esempio in relazione alla rilevazione di ostacoli mobili. 

Esempi di messaggi (che qui denominiamo **messaggi di stato**) sono:

  .. code::

    { "sonarName": "sonarName", "distanza": 1, "asse": "x" }
    { "collision": true, "move": "moveForward"}



Poichè l'invio asincrono di un comando non blocca il chiamante, un client può inviare un nuovo 
comando prima che il precedente sia terminato. Per gestire situazioni di questo tipo, WEnv segue la
regola che segue:

:remark:`è possibile interrompere l'esecuzione di una mossa solo con il comando alarm.`

 
Supponendo che il metodo ``request( String crilCmd )`` esegua l'invio asincrono di un comando, 
allora la sequenza di comandi che  segue viene corettamente eseguita: 

.. code:: 

    public String moveForward(int duration)  { 
        return crilCmd("moveForward", duration) ;  
    }
    public String stop( ){ return crilCmd("alarm",10); }

    request( moveForward(  1800) );
    Thread.sleep( 500 );
    request( stop() );

I *messaggi di stato* dopo l'esecuzione di un comando possono essere:

.. code::

    {"endmove":"RESULT", "move":MOVE}   
    
    RESULT ::= halted | notallowed

Il significato dei valori di ``RESULT`` è il seguente:

- **halted**: mossa interrotta perchè il robot ha ricevuto un comando  ``alarm``
- **notallowed**: mossa rifiutata (non eseguita) in quanto la mossa relativa al comando precedente 
  non è ancora terminata


Riportiamo (si veda il progetto ``unibo.wenvUsage22``)  un esempio di programma Java che esegue le mosse-base del robot mediante 
comandi in :ref:`cril<Comandi-base per il robot in cril>` inviati come Stringhe su ``8091``


.. list-table:: 
  :widths: 25,75
  :width: 100%

  * - ClientNaiveUsingWs.java
    - Esegue le mosse di base del robot inviando comandi scritti in :ref:`cril<Comandi-base per il robot in cril>`.

      :blue:`Key point`: Richiesta :blue:`asincrona`

      Richiede 4 thread, a causa della libreria ``javax.websocket``.

Dal punto di vista 'sistemistico', osserviamo che:

- Il codice di comunicazione è scritto completamente dal progettista dell'applicazione, che utilizza 
  la libreria ``javax.websocket``  (vedi anche `I WebSocket Comunicazione Asincrona Full-Duplex Per Il Web`_ )
- Gli eventi del ciclo di vita dell'endpoint WebSocket sono gestiti mediante :ref:`Annotazioni` 
  secondo lo schema che segue:

  .. code:: Java

        @ClientEndpoint  //La classe viene trattata come un client WebSocket   
        implementa IssWsSupport di classe pubblicaIssOperations{
        ...
        public IssWsSupport( String url ){ ... }
        
        @OnOpen //richiamato quando si avvia una nuova connessione WebSocket
        public void onOpen(Session userSession){ ... }
            
        @OnMessage //richiamato quando  arriva un  messaggio
        public void onMessage(String message){ ... }

        @OnError //richiamato quando si verifica un problema con la comunicazione
        public void disconnesso (sessione di sessione, errore lanciabile){...}
            
        @Chiudi //chiamato alla chiusura della connessione WebSocket
        public void onClose(Session userSession,CloseReason reason){...}
        }

Dal punto di vista 'applicativo', osserviamo che:

- Il chiamante esegue concettualmente una *fire-and-forget*  
- Un eventuale messaggio di stato viene 'iniettata' nell'applicazione tramite una chiamata al metodo annotato 
  con ``@OnMessage``
- E' possibile :blue:`interrompere` la esecuzione di una mossa inviando il comando **alarm** 




++++++++++++++++++++++++++++++++
Note di implementazione
++++++++++++++++++++++++++++++++

L'implementazione di WEnv si basa su due componenti principali: 

- **server**: che definisce il programma ``WebpageServer.js`` scritto con il framework Node express  
- **WebGLScene**: componente che gestisce la scena 

``WebpageServer.js`` utilizza due diversi tipi  di WebSocket:

- un socket basato sulla libreria `socket.io`_ che viene utilizzato per gestire 
  l'interazione con **WebGLScene**.

  :remark:`socket.io non è un'implementazione WebSocket.`

  Sebbene `socket.io`_ utilizzi effettivamente WebSocket come trasporto quando possibile, 
  aggiunge alcuni metadati a ciascun pacchetto: il tipo di pacchetto, lo spazio dei nomi  
  e l'ID di riconoscimento quando è necessario un riconoscimento del messaggio.
  Ecco perché un client WebSocket non sarà in grado di connettersi correttamente a un server Socket.IO 
  e un client `socket.io`_ non sarà in grado di connettersi a un server WebSocket.


- il websocker **8091** basato sulla libreria `ws`_ : questo socket viene utilizzato per gestire comandi 
  applicativi asincroni per muovere il robot inviati da client remoti e per inviare a client remoti 
  i *messaggi di stato*.

  WEnv utilizza la libreria Node https://github.com/einaros/ws per accettare questi comendi.

  :remark:`Il modulo ws non funziona nel browser: bisogna utilizzare l'oggetto WebSocket nativo.`

 

++++++++++++++++++++++++++++++++++++
WEnv come immagine docker
++++++++++++++++++++++++++++++++++++

WEnv viene anche distribuito come immagine Docker.
    
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Dockerfile e creazione dell'immagine
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Il file di nome **Dockerfile** nella directory ``it.unibo.virtualRobot2020`` contiene le istruzioni per creare una 
immagine Docker (per una introduizione a Docker si veda `Introduction to Docker and DockerCompose`_).

.. code::

    FROM node:13-alpine
    RUN mkdir -p /home/node      
    EXPOSE 8090
    EXPOSE 8091
    COPY ./node/WEnv /home/node/WEnv 
    COPY ./node/WEnv/WebGLScene /home/node/WEnv/WebGLScene
    #set default dir so that next commands executes in it
    WORKDIR /home/node/WEnv/WebGLScene
    RUN npm install
    WORKDIR /home/node/WEnv/server
    RUN npm install
    WORKDIR /home/node/WEnv/server/src
    CMD ["node", "WebpageServer"]    

L'immagine Docker può essere creata sul proprio PC eseguendo il comando (nella directory che contiene il *Dockerfile*):

    ``docker build -t virtualrobotdisi:3.0 .``    //Notare il .

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Esecuzione della immagine
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

L'immagine Docker di WEnv può essere attivata sul PC con il comando:

.. code::

    docker run -ti -p 8090:8090 -p 8091:8091 --rm  virtualrobotdisi:3.0
    

Il comando:

.. code::

    docker run -ti -p 8090:8090 
                  -p 8091:8091 --rm  virtualrobotdisi:3.0 /bin/sh

permette di ispezionare il contenuto della macchina virtuale e di attivare manualmente il sistema
(eseguendo  ``node WebpageServer.js``).


        
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Modificare la scena nella immagine
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Una volta attivata l'immagine docker, il comando 

  ``dockerps ps -a`` 

restituisce una tabella con 7 campi:

     ``CONTAINERID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES``

Per modificare il file che definisce la scena, si può copiare una nuova versione attraverso il comando ``docker cp`` 
e rendere permanente la modifica salvando il container.

 
.. code::

     //change the scene
    docker cp sceneConfig.js  CONTAINERID:/home/node/WEnv/WebGLScene/sceneConfig.js
    //Save the cotainer
    docker commit  CONTAINERID
    TAG the new image IMAGEID
    docker tag  IMAGEID IMGNAME:X.Y

    //Tag and register the image
    docker tag virtualrobotdisi:3.0 natbodocker/virtualrobotdisi:3.0
    docker push natbodocker/virtualrobotdisi:3.0


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Esecuzione con docker-compose
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


L'immagine viene distribuita anche su `Docker Hub`_ in ``docker.io/natbodocker/virtualrobotdisi:3.0``
come risulta nella spefifica del file ``virtualRobotOnly3.0.yaml``:

&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
virtualRobotOnly3.0.yaml
&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

.. code::

    version: '3'
    services:
    wenv:
        image: docker.io/natbodocker/virtualrobotdisi:3.0
        ports:
        - 8090:8090
        - 8091:8091

Il file ``virtualRobotOnly3.0.yaml`` permette l'attivazione di WEnv attraverso l'uso di docker-compose:

.. code::

    docker-compose -f virtualRobotOnly3.0.yaml  up   //per attivare
    docker-compose -f virtualRobotOnly3.0.yaml  down //per terminare

