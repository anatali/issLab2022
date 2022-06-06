%====================================================================================
% basicrobot description   
%====================================================================================
mqttBroker("broker.hivemq.com", "1883", "unibo/basicrobot").
context(ctxbasicrobot, "localhost",  "TCP", "8020").
 qactor( datacleaner, ctxbasicrobot, "rx.dataCleaner").
  qactor( distancefilter, ctxbasicrobot, "rx.distanceFilter").
  qactor( basicrobot, ctxbasicrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( envsonarhandler, ctxbasicrobot, "it.unibo.envsonarhandler.Envsonarhandler").
  qactor( pathexec, ctxbasicrobot, "it.unibo.pathexec.Pathexec").
