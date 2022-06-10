.. role:: red
.. role:: blue  
.. role:: remark   

.. _rst editor: http://rst.ninjs.org/

.. _Domain Driven Design : https://it.wikipedia.org/wiki/Domain-driven_design
.. _gradle: https://gradle.org/ 
.. _GradleIntro: ../../../../GradleIntro/userdocs/Gradle.intro.html
.. _IntelliJ: https://www.jetbrains.com/idea/
.. _template2022: _static/templateToFill.html
.. _Eclipse IDE for Java and DSL Developers: https://www.eclipse.org/downloads/packages/release/juno/sr2/eclipse-ide-java-and-dsl-developers
.. _Basic Git commands: https://confluence.atlassian.com/bitbucketserver/basic-git-commands-776639767.html
.. _Video on GIT: https://www.youtube.com/watch?v=HVsySz-h9r4
.. _github: https://github.com/
.. _opinionated: https://govdevsecopshub.com/2021/02/26/opinionated-software-what-it-is-and-how-it-enables-devops/

======================================
WorkspaceSetup
======================================    

- :ref:`Creazione progetto con Gradle`
- :ref:`Creazione di un progetto vuoto`
- :ref:`Verso l'applicazione`
- :ref:`Uso di GIT` 

.. _it.unibo.radarSystem22:

 
----------------------------------
Creazione progetto con Gradle
----------------------------------
Gradle è uno strumento open source per automatizzare la costruzione (build) del software.

Gradle è ormai uno standard di fatto in questo settore ed è interessante non solo in quanto
strumento, ma anche perchè applica i principi del `Domain Driven Design`_ (**DDD**) 
per modellare il suo proprio  domain-building software.

Per un approfondimento su Gradle si veda  `GradleIntro`_.

Iniziamo creando un progetto con l'aiuto di `Gradle`_

#. Apro sul PC una directory di lavoro (ad esempio **issLab2022**) e mi posiziono in essa.
#. Creo una directory per il progetto di nome :blue:`prova` e mi posiziono in essa.
#. Eseguo il comando
  
   .. code::

      gradle init (select 2, 3, 1, 2, 1, -, -)
   
   La risposta ``2`` alla prima domanda, implica che Gradle costruisca una applicazione-base che possiamo subito 
   eseguire e testare.

#. Eseguo il codice generato:

    .. code::

      gradlew build -x test  //esclude i test
      gradlew run

    L'output dovrebbe essere:

   .. code::

        > Task :app:run
        Hello World!
        BUILD SUCCESSFUL in 2s
        2 actionable tasks: 1 executed, 1 up-to-date

#. Apro il progetto in `IntelliJ`_, noto le directories ``main`` e ``test`` 
   

#. Osservo il contenuto di  ``app/build.gradle.kts``:

    .. code::

        plugins {
            // Apply the application plugin 
            //to add support for building a CLI application in Java.
            application
        }
        repositories {
            // Use JCenter for resolving dependencies.
            jcenter()
        }
        dependencies {
            // Use JUnit test framework.
            testImplementation("junit:junit:4.13")

            // This dependency is used by the application.
            implementation("com.google.guava:guava:29.0-jre")
        }
        application {
            // Define the main class for the application.
            mainClass.set("it.unibo.radarSystem22.App")
        }
#. Osservo il contenuto del file ``.gitignore``:

    .. code::
 
       # Ignore Gradle project-specific cache directory
       .gradle
       # Ignore Gradle build output directory
       build

#. Eseguo i test (usando una command shell o il Terminal di ``IntelliJ``):

   .. code::

        gradlew build  	//does also the testing
        gradlew test	//does only the tests

#. Osservo ``app/build/reports/tests/test/index.html``


Se rieseguiamo ``gradle init`` dando  ``1`` come risposta alla prima domanda, Gradle costruisce una 
struttura più scarna (meno "`opinionated`_") da cui conviene partire in questo nostro lavoro.

----------------------------------
Creazione di un progetto vuoto
----------------------------------


#. Apro sul PC la directory di lavoro (**issLab2022**) e mi posiziono in essa.
#. Creo la directory **unibolibs**, che conterrà le librerie (file ``.jar``) da noi sviluppate.
   Per il momento, inserisco le seguenti librerie:

   -  ``2p301.jar``
   - ``uniboInterfaces.jar``
   - ``unibonoawtsupports.jar``
   - ``radarPojo.jar``
#. Creo una directory di nome :blue:`it.unibo.radarSystem22` per il progetto  e mi posiziono in essa.
#. Eseguo il comando
  
   .. code::

      gradle init (select 1, 1, - )

#. Notiamo che viene creato un file ``build.gradle`` vuoto; inseriamo in questo file il seguente contenuto:

   .. code::

    plugins {
        id 'java'
        id 'eclipse'
        id 'application'
	    id 'jacoco'
    }

    version '1.0'
    sourceCompatibility = 1.8

    repositories {
        jcenter()
        flatDir { dirs '../unibolibs' }  
    }

    dependencies {
        testCompile group: 'junit', name: 'junit', version: '4.12'

    //CUSTOM unibo
        compile name: '2p301'
        compile name: 'uniboInterfaces'
        compile name: 'unibonoawtsupports'
        //RADAR (support and GUI)
        compile name: 'radarPojo'
        compile group: 'org.pushingpixels', name: 'trident', version: '1.3'
    }

    sourceSets {
        main.java.srcDirs += 'src'
        main.java.srcDirs += 'resources'
        test.java.srcDirs += 'test'		 
    }

    //Avoid duplication of src in Eclipse
    eclipse {
        classpath {
            sourceSets -= [sourceSets.main, sourceSets.test]	
        }		
    }

    mainClassName = 'it.unibo.xxx'  //TODO

    jar {
        from sourceSets.main.allSource	
        manifest {
            attributes 'Main-Class': "$mainClassName"
        }
    }
    distributions {
        main {
            contents {
                from './RadarSystemConfig.json'
            }
        }
    }

#. Apro una command shell su ``...issLab2022/it.unibo.radarSystem22`` ed eseguo 

   .. code::

      gradlew eclipse

   Noto che vengono creati i files ``.classpath`` e ``.project`` che descrivono il progetto in Eclipse.


----------------------------------
Verso l'applicazione
----------------------------------
 
#. Apro `Eclipse IDE for Java and DSL Developers`_ (2021 06) sul workspace ``issLab2022``.
#. Importo nel workspace il progetto `it.unibo.radarSystem22`
#. Seleziono :blue:`versione 1.8` del **compilatore Java** e della  **jre Java**
#. Creo directory ``userDocs`` e ``resources``
#. Inserisco in ``userDocs`` copia del file  `template2022`_ con la mia foto, 
   ridenominandolo `radarSytem22.html` 
   e copio i requisiti dati dal committente.
#. Creo la sourceDirectory ``src`` e ``test`` e noto che vengono inserite in *Java Build Path*
#. Inserisco in ``src`` i packages

   .. code::

      it.unibo.radarSystem22.main

#. Inserisco nel package ``it.unibo.radarSystem22.main`` una classe di prova:

   .. code::

    package it.unibo.radarSystem22.main;
    import radarPojo.radarSupport;

    public class RadarUsageMain {
 	public void doJob() {
		System.out.println("start");
		radarSupport.setUpRadarGui();
 		radarSupport.update( "40", "60");
 	}
	public static void main(String[] args) {
		new RadarUsageMain().doJob();
	}
    }

#. Inserisco in ``test`` il package

   .. code::

      it.unibo.radarSystem22 
 

----------------------------------
Uso di GIT
----------------------------------
Una volta creato il progetto, è opportuno salvarlo su un nostro repository GIT.


Per un aiuto ad usare GIT può essere utile consultare `Basic Git commands`_
e/o guardare il video `Video on GIT`_ di cui  riportiamo l'inizio di alcuni punti salienti:

.. code::

    0:00  - Introduction
    1:31  - Distributed vs Central Version Control
    3:17  - Installing Git
    3:39  - First Time Setup
    6:36  - Getting Started (Local repository)
    10:41 - Git File Control
    14:55 - Getting Started (Remote repository)
    20:37 - Branching
    20:50 - Common Workflow
    23:03 - Push Branch on remote
    27:38 - Faster Example
    29:41 - Conclusion


Per quanto riguarda il nostro progetto:

#. Mi posiziono sulla directory di lavoro  ``issLab2022``.
#. Eseguo:
   
   .. code::

       git init  //creates the directory .git	
       git status

#. Osservo il contenuto del file generato ``.gitignore`` :

   .. code::
 
      git status --ignored	//see ignored files 

   I files elencati non saranno salvati sul repository.
#. Eseguo i comandi     
   .. code::
 
       git add -A
       git commit -m "progetto iniziale"
       git log
       git status

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Creazione di un repository remoto   
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

#. Supponendo di avere accesso su `github`_ come user di nome ``userxyz``, creiamo un repository personale di nome 
   ``iss2022``, selezionando il tipo **public**, con *README* file e   **Add .gitignore** (*template Java*). 
   Quindi aggiungiamo il nostro progetto al repository:

    .. code::

        git remote add origin https://github.com/userxyz/iss2022 
        git remote -v   //osservo

#. Posizionato sulla directory ``issLab2022``, salvo il progetto corrente sul repository remoto.
   
    .. code::

       git push origin master




 