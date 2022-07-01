.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo


.. _bannerOnline: https://manytools.org/hacker-tools/ascii-banner/
.. _Bootstrap4: https://www.w3schools.com/bootstrap4/bootstrap_get_started.asp
.. _Bootstrap5: https://www.w3schools.com/bootstrap5/
.. _Grids: https://www.w3schools.com/bootstrap5/bootstrap_grid_basic.php
.. _Cards: https://www.w3schools.com/bootstrap5/bootstrap_cards.php
.. _Toasts: https://www.w3schools.com/bootstrap5/bootstrap_toast.php

.. _jsdelivr: https://www.jsdelivr.com/
.. _WebJars: https://mvnrepository.com/artifact/org.webjars
.. _WebJarsExplained: https://www.baeldung.com/maven-webjars 
.. _WebJarsDocs: https://getbootstrap.com/docs/5.1/getting-started/introduction/
.. _WebJarsExamples: https://getbootstrap.com/docs/5.1/examples/
.. _WebJarsContainer: https://getbootstrap.com/docs/5.1/layout/containers/
.. _Heart-beating: https://stomp.github.io/stomp-specification-1.2.html#Heart-beating


========================================
webrobot22
========================================

#. :ref:`webrobot22: startup`
#. Preparazione della pagina con `Bootstrap5`_
#. Sezione area comandi
#. Sezione cam
#. Connessione Ws con il server
#. Connessione TCP/CoAP con il robot (o applicazione)
#. Risposta js ai comandi
#. Azioni del controller in seguito a un comando




-----------------------------------------------------------
webrobot22: startup
-----------------------------------------------------------

- Estraggo ``webRobot22.zip`` in webRobot22
- Cambio da ``7.4.1`` a ``7.4.2`` in ``webRobot22\gradle\wrapper\gradle-wrapper.properties``
- In ``build.gradle`` modifico:
  
  .. code::

      version = '1.0'
      sourceCompatibility = '11'

-  Aggiungo il file ``gradle.properties`` con il contenuto:

   .. code::

       kotlinVersion = 1.6.0

-  Inserisco ``banner.txt`` in *src\main\resources\* usando `bannerOnline`_ (*small* font)
-  In ``application.properties`` in *src\main\resources\* inserisco*

   .. code::

       spring.application.name = webRobot22
       spring.banner.location  = classpath:banner.txt
       server.port             = 8085      

- In ``build.gradle`` inserisco:
 
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
        implementation name: 'it.unibo.qakactor-2.7'
        implementation name: 'unibonoawtsupports'  //required by the old infrastructure
        implementation name: 'unibo.actor22-1.1'   //using actor22comm in ConnQakBase
    }
    mainClassName = 'unibo.webRobot22.WebRobot22Application'
    jar {
        println("executing jar")
        from sourceSets.main.allSource
        manifest {
            attributes 'Main-Class': "$mainClassName"
        }
    }
 
- Eseguo ``gradlew run`` e apro un browser su ``localhost:8085``


-----------------------------------------------------------
webrobot22: pagina
-----------------------------------------------------------

- :ref:`WebApplication con SpringBoot`
- :ref:`Configurazione con WebSocketConfigurer`
- :ref:`Trasferimento di immagini: indexAlsoImages.html`
- :ref:`Bootstrap e webJars`
- :ref:`WebSocket in SpringBoot: versione STOMP`
- :ref:`Client (in Java per programmi)`

+++++++++++++++++++++++++++++++
Costruzione della pagina
+++++++++++++++++++++++++++++++

- static/css/bootstrap.css
- Creo ``RobotController.java`` in *src\main\java\it\unibo\robotWeb2020*
-  Inserisco ``BasicRobotCmdGui.html`` in *src\main\resources\templates*
-  Aggiungo ``wsminimal.js`` in resources   
- static/vendors

- Robots/common/IWsHandler e WebSocketConfiguration
- preprazione della pagina
- definizione delle azioni
- wsminimal.js



-----------------------------------------------------------
Comandare il robot
-----------------------------------------------------------



Handler dispatch failed; nested exception is java.lang.NoClassDefFoundError: kotlin/jvm/internal/Intrinsics


++++++++++++++++++++++++++++++++++++
Bootstrap
++++++++++++++++++++++++++++++++++++

- Bootstrap 4 was released in 2018
- Bootstrap 5 has switched to JavaScript instead of jQuery.
- W3.CSS is an excellent alternative to Bootstrap 5.
- ``jsDelivr`` provides CDN support for Bootstrap's CSS and JavaScript:

 .. code::

    <!-- Latest compiled and minified CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Latest compiled JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

- There are two container classes to choose from: ``.container`` (fixed width)  ``.container-fluid``
- ``.container-sm|md|lg|xl`` classes to determine when the container should be responsive
- By default, containers have left and right padding, with no top or bottom padding.


- The Bootstrap `Grids`_ system has four classes: xs (phones), sm (tablets), md (desktops), and lg (larger desktops).
- Bootstrap's `Grids`_ system is built with flexbox and allows up to 12 columns across the page.
- The Bootstrap 5 `Grids`_ system has six classes:

    - ``.col-`` (extra small devices - screen width less than 576px)
    - ``.col-sm-`` (small devices - screen width equal to or greater than 576px)
    - ``.col-md-`` (medium devices - screen width equal to or greater than 768px)
    - ``.col-lg-`` (large devices - screen width equal to or greater than 992px)
    - ``.col-xl-`` (xlarge devices - screen width equal to or greater than 1200px)
    - ``.col-xxl-`` (xxlarge devices - screen width equal to or greater than 1400px)

- `Cards`_: bordered box with some padding around its content. 
  It includes options for headers, footers, content, colors, etc.

- Responsive images automatically adjust to fit the size of the screen.
  ``img-fluid`` class applies max-width: 100%; and height: auto; to the image.  
  The image will then scale nicely to the parent element.
  
++++++++++++++++++++++++++++++++++++
Card with webcam
++++++++++++++++++++++++++++++++++++

- Open Windows Settings and choose Devices
- Click the Windows Start Menu Button.
- Click Camera
- ipwecab e SimpleMjpegView

 .. code::
     
    <script>
    function myFunction() {
    window.open("https://www.w3schools.com");
    }
    </script>

++++++++++++++++++++++++++++++++++++
Thymeleaf URL Syntax
++++++++++++++++++++++++++++++++++++

https://www.thymeleaf.org/doc/articles/standardurlsyntax.html

- Absolute URLs
  
   .. code::

    <a th:href="@{http://www.thymeleaf/documentation.html}">

- Context-relative URLs
  
 - Server-relative URLs
 
    .. code::

    <a th:href="@{~/billing-app/showDetails.htm}">

-----------------------------------------------------------
Enable SpringBoot live DevTools
-----------------------------------------------------------
settings(ctrl +alt+s) -> Build,Execution,Deployment -> compiler, check "Build project automatically"
Enable option 'allow auto-make to start even if developed application is currently running' in 
Settings -> Advanced Settings under compiler

