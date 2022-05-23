%====================================================================================
% boundaryqak22 description   
%====================================================================================
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxboundaryqak22, "localhost",  "TCP", "8032").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( cleanerqak22, ctxboundaryqak22, "it.unibo.cleanerqak22.Cleanerqak22").
