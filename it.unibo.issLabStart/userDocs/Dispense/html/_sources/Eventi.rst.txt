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

 


