.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

======================================
Using WEnv
======================================

C:\Didattica2021\issLab2021\unibo.wenvUsage22\resources\html\NaiveGui.html


--------------------------------------
Uso di WENv
--------------------------------------
++++++++++++++++++++++++++++++++++++++
package unibo.wenvUsage22.wshttp
++++++++++++++++++++++++++++++++++++++

- ClientUsingWsHttp: invia comandi cril su HttpConnection
- ClientUsingWs: invia comandi cril su WsConnection e implements IObserver
- ClientUsingWsHttp: iniva comandi su HTTP implements IObserver e riceve update dopo comandi da NaiveGui.html 

--------------------------------------
AttoriFSM base
--------------------------------------

++++++++++++++++++++++++++++++++++++++
package unibo.wenvUsage22.actors
++++++++++++++++++++++++++++++++++++++

- QakActor22Fsm extends QakActor22 
- 
- QakActor22FsmAnnot  extends QakActor22Fsm
- 
- FirstQakActor22Fsm: esempio di QakActor22Fsm che usa aril per muovere (w)
- MainFirstQakActor22Fsm: usa nuove annotazioni e FirstQakActor22Fsm

++++++++++++++++++++++++++++++++++++++++
package unibo.wenvUsage22.actors.robot
++++++++++++++++++++++++++++++++++++++++

- RobotMoverFsm: esempio di QakActor22Fsm che usa aril per muovere fino a ostacolo
- MainRobotMoverFsm: usa nuove annotazioni e RobotMoverFsm


--------------------------------------
AttoriFSM annotated
--------------------------------------

++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package unibo.wenvUsage22.annot
++++++++++++++++++++++++++++++++++++++++++++++++++++++++

- FirstAnnotated: primo esempio di QakActor22FsmAnnot
- MainFirstAnnotated : usa nuove annotazioni e FirstAnnotated


++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package unibo.wenvUsage22.actors.robot.annotated
++++++++++++++++++++++++++++++++++++++++++++++++++++++++

- RobotMoverFsmAnnotated: esempio di QakActor22FsmAnnot che usa aril  
- RobotController: invia comando aril (w) a RobotMoverFsmAnnotated
- MainRobotMoverFsmAnnotated: usa nuove annotazioni e RobotMoverFsmAnnotated,RobotController

--------------------------------------
AttoriFSM at work
--------------------------------------

++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package unibo.wenvUsage22.annot.walker
++++++++++++++++++++++++++++++++++++++++++++++++++++++++

- BoundaryWalkerAnnot
- MainBoundaryWalkerAnnot