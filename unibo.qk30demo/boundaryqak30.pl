%====================================================================================
% boundaryqak30 description   
%====================================================================================
context(ctxboundaryqak30, "localhost",  "TCP", "8032").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( boundaryqak30, ctxboundaryqak30, "it.unibo.boundaryqak30.Boundaryqak30").
  qactor( cmdconsole, ctxboundaryqak30, "it.unibo.cmdconsole.Cmdconsole").
  qactor( applobserver, ctxboundaryqak30, "it.unibo.applobserver.Applobserver").
