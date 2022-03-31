.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo 

.. ``  https://bashtage.github.io/sphinx-material/rst-cheatsheet/rst-cheatsheet.html

.. _pattern-proxy: https://it.wikipedia.org/wiki/Proxy_pattern

.. _port-adapter: https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)

.. _clean-architecture:  https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html

.. _microservizio: https://en.wikipedia.org/wiki/Microservices

.. _pattern-decorator: https://it.wikipedia.org/wiki/Decorator

.. _CoAP: https://coap.technology/

.. _tuProlog: https://apice.unibo.it/xwiki/bin/view/Tuprolog/

.. _SonarAlone.c : ../../../../../issLab2022/it.unibo.raspIntro2022/code/c/SonarAlone.c
.. _LedSonar.c : ../../../../../issLab2022/it.unibo.raspIntro2022/code/c/LedSonar.c
.. _led25GpioTurnOn.sh : ../../../../../issLab2022/it.unibo.raspIntro2022/code/bash/led25GpioTurnOn.sh
.. _led25GpioTurnOff.sh : ../../../../../issLab2022/it.unibo.raspIntro2022/code/bash/led25GpioTurnOff.sh
.. _radarPojo.jar : _static/code/radarPojo.jar

.. _pipe : https://it.wikipedia.org/wiki/Pipe_(informatica)

.. _template2022 : _static/templateToFill.html

======================================
RadarSystem
======================================

.. CostruireSoftware.html#indicazioni-sul-processo-di-produzione

Tenendo conto di quanto detto in *Indicazioni-sul-processo-di-produzione* (:doc:`CostruireSoftware`),
impostiamo un processo di produzione del software partendo da un insieme di requisiti.


.. _requirements:

--------------------------------------
Requisiti
--------------------------------------

Si desidera costruire un'applicazione software capace di: 

.. _radarGui:

- (requisito :blue:`radarGui`) mostrare le distanze rilevate da un sensore Sonar``HC-SR04`` connesso a un RaspberryPi 
  su un display (``RadarDisplay``) a forma di radar connesso a un PC.
  
.. image:: ./_static/img/Radar/radarDisplay.png 
   :align: center
   :width: 20%

.. _ledAlarm:

- (requisito :blue:`ledAlarm`) accendere un Led se la distanza rilevata dal Sonar è inferiore a un valore limite prefissato
  denominato ``DLIMIT``.

--------------------------------------
Analisi dei Requisiti
--------------------------------------
Iniziamo anallizzando il testo, cercando di chiarire con il committente il signifcato dei termini in esso presenti.
Questa comunicazione a livello umano è fondamentale per formulare requisiti che siano:

- Chiari, Corretti, Completi, Concisi
- Non ambigui, consistenti
- Tracciabili, Realizzabili, Collaudabili

+++++++++++++++++++++++++++++++++++++
Tracciabilità
+++++++++++++++++++++++++++++++++++++
Poichè il testo dei requisiti fornisce già un nome per ciascun requisito, si ha già un solido punto
di partenza per la :blue:`forward traceability`.

+++++++++++++++++++++++++++++++++++++
User stories
+++++++++++++++++++++++++++++++++++++

Una user-story che esprime il funzionamento atteso del sistema, catturando tutti i requisiti può essere
così espressa:

.. epigraph:: 
  
   :blue:`User-story US1`: come utente mi aspetto che il Led si accenda se pongo un ostacolo a distanza ``d<DILIMT`` 
   dal Sonar e che il Led si spenga non appena porto l'ostacolo ad una  distanza ``d>DILIMT``.
   In ogni caso posso vedere illuminarsi un punto sul ``RadarDisplay`` a distanza ``d`` 
   dal centro lungo   una  retta che forma un angolo :math:`\theta` 
   rispetto all'asse orizzontale del display.

   

+++++++++++++++++++++++++++++++++++++
Piano di testing (funzionale)
+++++++++++++++++++++++++++++++++++++  

La user-story precedente suggerisce anche un possibile test funzionale per la verifica del 
comportamento del software da sviluppare.

.. Un possibile test funzionale consiste nel porre un ostacolo davanti al Sonar
   prima a una distanza ``D>DLIMIT`` e poi a una distanza ``D<DLIMIT`` e osservare il valore
   visualizzato sulla GUI e lo stato del Led.

Tuttavia questo modo di procedere non è automatizzabile, in quanto richiede 
la presenza di un operatore umano. Nel seguito cercheremo di organizzare le cose in modo
da permettere :blue:`Test automatizzati`.


+++++++++++++++++++++++++++++++++++++
Glossario
+++++++++++++++++++++++++++++++++++++
La redazione di un glossario è utile per pervenire alla definizione di *Costumer requirements* 
(:blue:`C-requirements`) chiari e possibilmente non ambigui. 
Il nostro glossario, la cui redazione lasciamo al lettore, dovrà includere i termini 
*Sensore, Led, RadarDisplay* che corrispondono ad altrettanti :blue:`componenti` del sistema.

In questa sede però, la nostra attenzione si rivolge alla possibilità/necessità di esprimere
i requisiti ponendoci dal punto di vista dell'elaboratore, che (fortunatamente?!) non comprende
il linguaggio naturale.

Dal punto di vista della 'macchina', l'unico modo per relazionarsi con un ente menzionato nel glossario 
è avere del software che lo rappresenta.

Poniamo dunque al committente anche domande da questo punto di vista, e altre domande volte 
a chiarire bene la natura del sistema da realizzare.

+++++++++++++++++++++++++++++++++++++
Domande al committente
+++++++++++++++++++++++++++++++++++++


.. list-table:: 
  :widths: 50,50
  :width: 100%

  * - Il committente fornisce software relativo al Led ?
    - Si, `led25GpioTurnOn.sh`_ e `led25GpioTurnOff.sh`_ (progetto *it.unibo.rasp2022*)
  * - Il committente fornisce software per il Sonar ?
    - Si, `SonarAlone.c`_ (progetto *it.unibo.rasp2022*)
  * - Il committente fornisce qualche libreria per la costruzione del RadarDisplay ?
    - Si, viene reso disponibile (progetto *it.unibo.java.radar*)  il supporto  `radarPojo.jar`_,
      che fornisce un **singleton JAVA** ``radarSupport`` capace di creare una GUI in 'stile radar' 
      e di visualizzare su di essa un valore di distanza intero fornito come ``String``:

      .. code:: java

        public class radarSupport {
        private static RadarControl rc;  
        public static void setUpRadarGui( ){
          rc=...
        }
        public static void update(String d,
              String dir){rc.update(d,dir);
        }
        }    
  * - Il LED può/deve essere connesso allo stesso RaspberryPi del sonar? 
    - Al momento si. In futuro però il LED potrebbe essere connesso a un diverso nodo di elaborazione.
  * - Il valore ``DLIMIT`` deve essere cablato nel sistema o è bene sia 
      definibile in modo configurabile dall'utente finale?
    - 
      .. _configDLIMIT:

      L'utente finale deve essere in grado di specificare in un 'file di configurazione' 
      il valore di questa distanza.
 
Dai requisiti possiamo asserire che:

- si tratta di realizzare il software per un **sistema distribuito** costituito da due nodi di elaborazione:
  un RaspberryPi e un PC convenzionale;
- i due nodi di elaborazione devono potersi  `scambiare informazione via rete`, usando supporti WIFI;
- i due nodi di elaborazione devono essere 'programmati' usando **tecnologie software diverse**.

+++++++++++++++++++++++++++++++++++++
In sintesi
+++++++++++++++++++++++++++++++++++++

:remark:`Si tratta di realizzare un sistema software distribuito ed eterogeneo`

Il sistema comprende un dispositivo di input (il Sonar) e due dispositivi di output (il Led e il RadarDisplay)

Al momento ci fermiamo qui, ma in futuro cerchremo di formalizzare meglio il risultato della nostra analisi,
definendo, se possibile, un modello del sistema da costruire, in cui sono evidenti i componenti e le loro relazioni
(si veda :ref:`L'architettura logica`).

Prima di procedere alla analisi del problema, proviamo però a vedere cosa potrebbe accadere se cercassimo
di individuare subito una qualche soluzione, passando al HOW senza approfondire il WHAT (si ricordi :ref:`SCRUM`) e 
senza impostare  :ref:`Progettazione e sviluppo come processo evolutivo`.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Se non fosse distribuito ...
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Osserviamo che, in assenza del requisito `radarGui`_, si potrebbe pensare di soddisfare il requisito `ledAlarm`_
introducendo una semplice modifica nel codice di `SonarAlone.c`_ .

Trattandosi di un programma ``C``, la modifica potrebbe consistere nella introduzione di una funzione come la seguente:

.. code:: c

  void updateTheLed( int cm ) {
    if( cm < DLIMIT ) digitalWrite(LED, HIGH);
    else digitalWrite(LED, LOW);
  }

Questa funzione andrebbe invocata ad ogni iterazione del ciclo principale nel ``main``, come ad esempio in: `LedSonar.c`_.

Il punto critico di questa impostazione è che la parte strutturale del sistema risulta 'annegata' nel programma che 
esprime il funzionamento. In particolare, i requisiti parlano di Led e Sonar,
ma a questi dispositivi non corrisponde alcun codice specifico, gestibile in modo separato dal codice che
realizza la logica applicativa.

Tutto funziona, ma le dimensioni architetturali relative alla **struttura** del sistema in termini di componenti e
alla loro **interazione** :blue:`non sono esplicitamente espresse`.  

Più strutturata da punto di vista architetturale è la :ref:`Soluzione in Python` basata su `pipe`_ che potrebbe essere
rappresentata come segue:

.. image:: ./_static/img/Architectures/pipe.png 
   :align: center
   :width: 40%

Ma anche questa si presta a critiche ...

:worktodo:`WORKTODO: Anche la pipe "non va bene" perchè ...`

------------------------------------------
Impostazione del workspace
------------------------------------------

Prima di procedere con lo sviluppo del software, è opportuno creare un appropriato ambiente di lavoro.
A tal fine si veda :doc:`WorkspaceSetup`.
