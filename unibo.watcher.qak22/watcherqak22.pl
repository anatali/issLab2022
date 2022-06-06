%====================================================================================
% watcherqak22 description   
%====================================================================================
mqttBroker("broker.hivemq.com", "1883", "unibo/nat/radar").
context(ctxwatcherqak22, "localhost",  "TCP", "8067").
 qactor( watcherqak22, ctxwatcherqak22, "it.unibo.watcherqak22.Watcherqak22").
