%====================================================================================
% robotappl1 description   
%====================================================================================
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxrobotappl, "localhost",  "TCP", "8078").
 qactor( pathexec, ctxbasicrobot, "external").
  qactor( robotappl1, ctxrobotappl, "it.unibo.robotappl1.Robotappl1").
