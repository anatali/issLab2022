%====================================================================================
% mapqak22 description   
%====================================================================================
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxmapqak22, "localhost",  "TCP", "8032").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( mapqak22, ctxmapqak22, "it.unibo.mapqak22.Mapqak22").
