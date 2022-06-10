.. role:: red 
.. role:: blue 
.. role:: remark
.. role:: worktodo

.. _SOLID: https://it.wikipedia.org/wiki/SOLID

===================================
ConclusioniISS22
===================================

I punti salienti del nostro lavoro si possono riassumere dicendo che, in :ref:`Ingegneria`:

#. :remark:`Non c'è prodotto (codice) senza progetto ...`
#. :remark:`Non c'è progetto senza analisi del problema ...`
#. :remark:`Non c'è problema senza requisiti.`

Il problema è che quasi sempre le analisi e i progetti :red:`non sono esplicitamente definiti` e/o espressi in modo :blue:`coerente,
consistente e non amabiguo`.

Nella  :ref:`Ingegneria del software<Programmatori = (non) ingegneri?>`:

- :remark:`I modelli permettono di esprimere elementi essenziali di una analisi o di un progetto`

Inoltre: 

- :remark:`I modelli eseguibili sono comprensibili ad una machina e quindi formalmente definiti`

- :remark:`I modelli eseguibili permettono di analizzare, sperimentare e decidere, prima di codificare`

- Nell'ambito di un processo di sviluppo :ref:`agile-iterativo<Indicazioni sul processo di produzione>`:

  :remark:`Il software deve essere facilmente configurabile/modificabile`
  
  in quanto  i requisiti possono essere precisati (o anche cambiare) in corso d'opera e perchè 
  alcune problematche potrebbero sfuggire durante la prima anailsi del problema.

- Questo obiettivo può/deve essere raggiunto :
  
   - tenendo conto dei principi `SOLID`_  
   - considerando che l' :ref:`architettura<Architetture>` :blue:`(logica) di un sistema software` è 
   
     :remark:`un invariante con cui pianificare il lavoro e disciplinare la variabilità`
   - impostando il lavoro come un processo di :blue:`zooming` entro l'architettura logica
