%====================================================================================
% sonarqak22 description   
%====================================================================================
mqttBroker("broker.hivemq.com", "1883", "unibo/nat/radar").
context(ctxsonarqak22, "localhost",  "TCP", "8080").
 qactor( sonarsimulator, ctxsonarqak22, "sonarSimulator").
  qactor( sonardatasource, ctxsonarqak22, "sonarHCSR04Support2021").
  qactor( datacleaner, ctxsonarqak22, "dataCleaner").
  qactor( sonarqak22, ctxsonarqak22, "it.unibo.sonarqak22.Sonarqak22").
  qactor( sonarmastermock, ctxsonarqak22, "it.unibo.sonarmastermock.Sonarmastermock").
