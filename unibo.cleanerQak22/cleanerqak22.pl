%====================================================================================
% cleanerqak22 description   
%====================================================================================
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxcleanerqak22, "localhost",  "TCP", "8032").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( cleanerqak22, ctxcleanerqak22, "it.unibo.cleanerqak22.Cleanerqak22").
