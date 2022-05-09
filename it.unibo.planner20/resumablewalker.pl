%====================================================================================
% resumablewalker description   
%====================================================================================
context(ctxresumablewalker, "localhost",  "TCP", "8038").
 qactor( resumablewalker, ctxresumablewalker, "it.unibo.resumablewalker.Resumablewalker").
  qactor( insensitivewalker, ctxresumablewalker, "it.unibo.insensitivewalker.Insensitivewalker").
