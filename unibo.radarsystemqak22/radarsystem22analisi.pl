%====================================================================================
% radarsystem22analisi description   
%====================================================================================
context(ctxprototipo0, "localhost",  "TCP", "8088").
 qactor( sonar22, ctxprototipo0, "it.unibo.sonar22.Sonar22").
  qactor( led22, ctxprototipo0, "it.unibo.led22.Led22").
  qactor( radar22, ctxprototipo0, "it.unibo.radar22.Radar22").
  qactor( controller22, ctxprototipo0, "it.unibo.controller22.Controller22").
