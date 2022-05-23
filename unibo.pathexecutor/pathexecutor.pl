%====================================================================================
% pathexecutor description   
%====================================================================================
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxpathexecutor, "localhost",  "TCP", "8062").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( pathexec, ctxpathexecutor, "it.unibo.pathexec.Pathexec").
  qactor( pathcaller, ctxpathexecutor, "it.unibo.pathcaller.Pathcaller").
