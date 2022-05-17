%====================================================================================
% sentinel description   
%====================================================================================
context(ctxsentinel, "localhost",  "TCP", "8055").
 qactor( sentinel, ctxsentinel, "it.unibo.sentinel.Sentinel").
  qactor( sender, ctxsentinel, "it.unibo.sender.Sender").
