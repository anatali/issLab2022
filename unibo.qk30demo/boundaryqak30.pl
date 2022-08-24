%====================================================================================
% boundaryqak30 description   
%====================================================================================
context(ctxboundaryqak30, "localhost",  "TCP", "8032").
context(ctxbasicrobot, "localhost",  "TCP", "8020").
context(ctxconsole, "localhost",  "TCP", "8042").
context(ctxobserver, "localhost",  "TCP", "8044").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( boundaryqak30, ctxboundaryqak30, "it.unibo.boundaryqak30.Boundaryqak30").
  qactor( cmdconsole, ctxconsole, "it.unibo.cmdconsole.Cmdconsole").
  qactor( applobserver, ctxobserver, "it.unibo.applobserver.Applobserver").
