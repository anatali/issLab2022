%====================================================================================
% ledqak22 description   
%====================================================================================
mqttBroker("broker.hivemq.com", "1883", "unibo/nat/radar").
context(ctxledqak22, "localhost",  "TCP", "8065").
 qactor( ledqak22, ctxledqak22, "it.unibo.ledqak22.Ledqak22").
