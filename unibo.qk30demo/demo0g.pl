%====================================================================================
% demo0g description   
%====================================================================================
context(ctxtesta, "localhost",  "TCP", "8095").
context(ctxtestb, "localhost",  "TCP", "8096").
context(ctxtestc, "localhost",  "TCP", "8097").
 qactor( componenta, ctxtesta, "it.unibo.componenta.Componenta").
  qactor( componentb, ctxtestb, "it.unibo.componentb.Componentb").
  qactor( componentc, ctxtestc, "it.unibo.componentc.Componentc").
