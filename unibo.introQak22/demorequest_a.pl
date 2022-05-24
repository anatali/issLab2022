%====================================================================================
% demorequest_a description   
%====================================================================================
context(ctxcallers, "localhost",  "TCP", "8072").
context(ctxcalled, "127.0.0.1",  "TCP", "8074").
 qactor( callerqa1, ctxcallers, "it.unibo.callerqa1.Callerqa1").
  qactor( callerqa2, ctxcallers, "it.unibo.callerqa2.Callerqa2").
  qactor( qacalled, ctxcalled, "it.unibo.qacalled.Qacalled").
msglogging.
