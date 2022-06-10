.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _visione olistica: https://it.wikipedia.org/wiki/Olismo
.. _state diagram: https://en.wikipedia.org/wiki/State_diagram#:~:text=A%20state%20diagram%20is%20a,this%20is%20a%20reasonable%20abstraction.
.. _Automa a stati finiti: https://it.wikipedia.org/wiki/Automa_a_stati_finiti
.. _Macchina d Moore: https://it.wikipedia.org/wiki/Macchina_di_Moore
.. _opinionated: https://govdevsecopshub.com/2021/02/26/opinionated-software-what-it-is-and-how-it-enables-devops/


==================================
Actor22Plus
==================================

---------------------------------------
tempxxx
---------------------------------------

Progetto: **unibo.wenvUsage22** package: *unibo.wenvUsage22.unibo.wenvUsage22.annot.walker*.

Abbiamo visto come la classe :ref:`QakActor22Fsm` forzi un ApplicationDesigner a concepire un attore come una 
`Macchina d Moore`_ i cui stati sono definiti da azioni, implementazioni della interfaccia 
:ref:`StateActionFun`.

L'ApplicationDesigner viene indotto a definire due metodi che :ref:`QakActor22Fsm` dichiara come **abstract**:

- il metodo :ref:`declareTheStates` che si avvale del metodo :ref:`declareState`
- il metodo :ref:`setTheInitialState<QakActor22Fsm: costruttore>`

Ora ci poniamo come obiettivo quello di 'nascondere' (ancora una volta!) questi dettagli, introducendo :ref:`Annotazioni` ai metodi
che realizzano gli stati dell'automa. 

+++++++++++++++++++++++++++++++++++++++++++++++++
Annotazioni State e Transition
+++++++++++++++++++++++++++++++++++++++++++++++++

A questo fine introduciamo:

- una classe ``QakActor22FsmAnnot`` che estende :ref:`QakActor22Fsm` con operazionicapaci di elaborare le annotazioni sui metodi
- l'annotazione ``State`` che denota un metodo come stato. 
    
  .. code:: 

    @java (ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface State {
	    String name() default "s0";
	    boolean initial() default  false;   
    }  

- l'annotazione ``Transition`` che denota una transizione. 
    
  .. code:: 

    @Target (ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Transitions.class)
    public @interface Transition {
	    String name() default "t0";
	    String state()  ;
	    String msgId()  ;
    }

La classe  ``QakActor22FsmAnnot`` definisce il metodo :ref:`declareTheStates` come analizzatore di tutte le annotazioni ``State`` e ``Transition``
presenti nella classe specilizzata dall'ApplicationDesigner. Per ciascun metodo annotato invoca il metodo

    ``elabStateMethod(Method m, String stateName)``

che 
- tiene traccia delle informazioni dichiarate nelle annotazioni ``Transition`` in due liste (``nextStates`` e ``msgIds``)
- invoca il metodo :ref:`declareState` creando una istanza di :ref:`StateActionFun`:

  .. code:: Java

        declareState( stateName, new StateActionFun() {
        @Override
            public void run( IApplMessage msg ) {
            try {
                curMethod.invoke(  myself, msg   );  //I metodi hanno this come arg implicito
                for( int j=0; j<nextStates.size();j++ ) {
                    addTransition( nextStates.elementAt(j), msgIds.elementAt(j) );
                }					
                nextState();
            } catch ( Exception e) {
						ColorsOut.outerr("wrong execution for:"+ stateName + " - " + e.getMessage());
            }
            }			        			  
        });//declareState		


In altre parole, la classe ``QakActor22FsmAnnot`` costruisce in modo automatico quelle parti di codice richieste da 
:ref:`QakActor22Fsm` e che nella versione non annotata dovevano essere scritte dall'ApplicationDesigner.
 
+++++++++++++++++++++++++++++++++++++++++++++++++
Reafctoring del BoundaryWalker 
+++++++++++++++++++++++++++++++++++++++++++++++++

Progetto: **unibo.wenvUsage22** code: *unibo.wenvUsage22.actors.robot.RobotBoundaryWalkerFsm*.


Prima di procedere alla definizione dell'attore, introduciamo  due nuove 'features'

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
VRobotMoves.step
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

La classe di utilità ``VRobotMoves`` definisce un metodo :blue:`step` che muove in avanti il robot per 300msec e attende
il tempo necessario al completaento della mossa.


  .. code:: Java

	public static void step(String name, Interaction2021 conn) {
		moveForward( name,conn,300 );	 	
		CommUtils.delay(350); 		
	}

Sappiamo che dopo questo comando possiamo avere due esiti:

- mossa completata con sucesso: WEnv invia sulla WSConnection un endmove
- collisione: WEnv invia sulla WS un endmove

:remark:`evitiamo di inviare nuovi comandi di movimento prima del completamento di una mossa`
 


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
WsConnWEnvObserver
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Il compito che diamo all'osservatore ``WsConnWEnvObserver`` (che specializza :ref:`WsConnSysObserver`) è di gestire le 
informazioni inviate da WEnv sulla WSConnection dopo l'esecuzione di uno :blue:`step`  in modo da emettere
due possibili eventi/emssaggi:

- **SystemData.endMoveOkEvent** se la mossa è stata completata con successo
- **SystemData.endMoveKoEvent** se la mossa non è stata completata (per via di una collisione)

Abbiamo anche fatto in modo da ricevere, in caso di fallimento, il tempo trascorso dall'inizio del movimento al momento del fallirmento.

A questo fine, si è introdotto in :ref:`WsConnSysObserver` un Timer, che viene attivato per ogni osservatore registrato  
nel metodo **sendLine** di :ref:`WsConnection` e fermato al termine di ogni mossa (dal metodo **update** di ``WsConnWEnvObserver``.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
BoundaryWalkerAnnot
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Con le premesse precedenti, la nuova versione del BoundaryWalker può essere definita come segue:



  .. code:: Java

    public class BoundaryWalkerAnnot extends QakActor22FsmAnnot  {
	private Interaction2021 conn;	
 	private int ncorner  = 0;
	
	public BoundaryWalkerAnnot(String name) {
		super(name);
 	}
		
        @State( name = "robotStart", initial=true)
        @Transition( state = "robotMoving" ,  msgId = SystemData.endMoveOkId )
        @Transition( state = "wallDetected" , msgId = SystemData.endMoveKoId )
        protected void robotStart( IApplMessage msg ) {
            conn = WsConnection.create("localhost:8091" ); 	 
            ((WsConnection)conn).addObserver( new WsConnWEnvObserver(getName()) );
            VRobotMoves.step(getName(),conn);
        }

        @State( name = "robotMoving" )
        @Transition( state = "robotMoving" ,  msgId = SystemData.endMoveOkId)
        @Transition( state = "wallDetected" , msgId = SystemData.endMoveKoId )
        protected void robotMoving( IApplMessage msg ) {
            outInfo(""+msg);	
            VRobotMoves.step(getName(),conn);
        }
        
        @State( name = "wallDetected" )
        @Transition( state = "robotMoving" , msgId = SystemData.endMoveOkId )
        @Transition( state = "endWork" ,     msgId = SystemData.haltSysCmdId)
        protected void wallDetected( IApplMessage msg ) {
            ncorner++;
            if( ncorner == 4 ) {
                autoMsg(SystemData.haltSysCmd(getName(),getName() ));
            }else VRobotMoves.turnLeft(getName(), conn);
        }
        
        @State( name = "endWork" )
        protected void endWork( IApplMessage msg ) {
            outInfo("BYE" );	
            System.exit(0);
        }
    }


Vediamo che:

-  :blue:`robotStart`: è  lo stato iniziale, in cui 'attore si connette al robot usando la WSConnection.
    In questo stato, l'attore invia un comando :blue:`step`, pianificando le transizioni come segue:
        
        - in caso di soccesso,  passerà nello stato :blue:`robotMoving`
        - in caso di fallimento, ipotizza una collisione (non allarmi o altro) e passerà nello stato :blue:`wallDetected`

-  :blue:`wallDetected`: è  lo stato in cui l'attora sa che il robot ha incontrato un muro.
    In questo stato, l'attore invia un comando di rotazione a sinistra, se capisce di non avere terminato il percorso.
    Se no, si auto-invia un messaggio di terminazione **SystemData.haltSysCmdId**.
    Le transizioni sono pianificate in modo che l'attore:

        - passerà nello stato :blue:`robotMoving` nel caso riceva un nessggio di mossa (rotazione) terminata con sueccsso
        - passerà nello stato :blue:`endWork` nel caso riceva il messio di terminazione *SystemData.haltSysCmdId*
        - il caso di fallimento della mossa di rotazione viene escluso
 
-  :blue:`endWork`: è  lo stato finale in cui l'attore termina il sistema.
  





- State Transition elaborate in QakActor22FsmAnnot.declareTheStates -> elabAnnotatedMethod. setTheInitialState 
- BoundaryWalkerAnnot  extends QakActor22FsmAnnot 
- WsConnSysObserver ha un timer che viene usato ad ogni sendLine (forward)
- WsConnWEnvObserver  Trasforma dati ricevuti su WS in SystemData.endMoveOk o in SystemData.endMoveKo
- VRobotMoves