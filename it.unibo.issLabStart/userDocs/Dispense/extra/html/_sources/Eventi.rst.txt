.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

===============================
Eventi
===============================

Concettualmente, una sorgente dovrebbe essere capace di 
emettere informazione percettibile da un numero qualsiasi di osservatori interessati,
senza che l'azione di osservazione possa alterare il funzionamento della sorgente.

Questa idea (che segnala un **abstraction gap**) 
suggerisce il concetto di *messaggio che non ha un preciso destinatario*
come indicato in :ref:`Terminologia di riferimento`.


:remark:`un attore può emette informazione in forma di evento`
 
Per implementare questa idea di evento, dobbiamo:

- dichiarare l'interesse da parte di un attore di percepire un evento ``Ev``;
- dotare 'geneticamente' un attore di una operazione ``emit`` che produce come effetto la inserzione 
  di ``Ev`` nella coda di tutti gli attori interessati, locali e remoti.

 Come supporto alla realizzazione, useremo un attore specializzato.

-----------------------------------
EventMsgHandler
-----------------------------------

Un attore che ha due compiti principali:

#. :blue:`registrazione`: tenere memoria del fatto che un attore  si è dichiarato interessato 
   alla percezione di un evento ``Ev``. Per questa funzione, *EventMsgHandler* introduce una mappa
   che associa il nome di un attore-observer al'id dell'evento che desidera osservare.
#. :blue:`aggiornamento`: inserire l'evento ``Ev`` nella coda di tutti gli attori interessati 
   quando un attore emette l'evento ``Ev``. Per questa funzione,  *EventMsgHandler*
   usa l'operazione :ref:`sendAMsg di Qak22Util`.

 
.. code:: Java

  public class EventMsgHandler extends QakActor22{
   static final String myName = "eventhandler";
   protected HashMap<String,String> eventObserverMap = 
                            new HashMap<String,String>();  
  @Override
  protected void handleMsg(IApplMessage msg) {
    if( msg.msgId().equals(Qak22Context.registerForEvent)) {
        eventObserverMap.put(msg.msgSender(), msg.msgContent());
    }else 
    if( msg.msgId().equals(Qak22Context.unregisterForEvent)) {
      eventObserverMap.remove(msg.msgSender(), msg.msgContent());
    }else 
      if( msg.isEvent() )  updateTheObservers( msg );
      else ColorsOut.outerr(myName + " msg unknown"); 
  }  


++++++++++++++++++++++++++++++++++++++++++
Qak22Context.registerAsEventObserver
++++++++++++++++++++++++++++++++++++++++++

Il compito di registrazione di :ref:`EventMsgHandler` viene svolto come conseguenza della ricezione 
di un *dispatch* con id **registerForEvent** che contiene il nome dell'attore-osservatore e l'identificatore
dell'evento osservato:

  ``msg(registerForEvent,dispatch,OBSERVERNAME,eventhandler,EVENTID,N)``

Questo messaggio è inviato da :ref:`Qak22Context` quando il livello applicativo ne invoca il metodo 
``registerAsEventObserver``

.. code:: Java

    public class Qak22Context { 
        ...
        public static final String registerForEvent   = "registerForEvent";
        public static final String unregisterForEvent = "unregisterForEvent";

        public static void registerAsEventObserver(String observer, String evId) {
            QakActor22 a = getActor(EventMsgHandler.myName);
            if( a == null ) new EventMsgHandler();
            IApplMessage m = CommUtils.buildDispatch(observer,
                                registerForEvent, evId, EventMsgHandler.myName);
            Qak22Util.sendAMsg( m, EventMsgHandler.myName ); 
        }
    }

++++++++++++++++++++++++++++++++++++++++++++
EventMsgHandler updateTheObservers
++++++++++++++++++++++++++++++++++++++++++++
L'operazione di :blue:`aggiornamento` degli observer registrati viene realizzata usando l'operazione 
:ref:`sendAMsg<sendAMsg di Qak22Util>`  per inviare il messaggio a tutti gli attori registrati, locali o remoti.

.. code:: Java

  protected void updateTheObservers(IApplMessage msg) {
    eventObserverMap.forEach(
    ( String actorName,  String evName) -> {
        if( evName.equals( msg.msgId()) ) {
            IApplMessage m = Qak22Util.buildEvent(
                msg.msgSender(), msg.msgId(), msg.msgContent(), actorName ) ;
            Qak22Util.sendAMsg( m );
          }
        } 
      ) ;
  }

Ovviamente gli osservatori remoti devono essere stati dichiarati come tali.




------------------------------
emit di un evento
------------------------------

L'operazione ``emit`` di un messaggio di evento ``Ev`` viene implementata nella classe 
:ref:`QakActor22<QakActor22: il costruttore>` 'ridirigendo' ``Ev`` a :ref:`EventMsgHandler` 
che a sua volta esegue il suo compito di :ref:`aggiornamento<EventMsgHandler updateTheObservers>`.

 
.. code:: Java

  protected void emit(IApplMessage msg) {
    if( msg.isEvent() ) {
      Qak22Util.sendAMsg( msg, EventMsgHandler.myName);
    }   	
  }





L'esempio di una sorgente di eventi si trova in :ref:`Il Sonar come attore che emette eventi`.

