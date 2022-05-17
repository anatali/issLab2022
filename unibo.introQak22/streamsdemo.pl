%====================================================================================
% streamsdemo description   
%====================================================================================
context(ctxstreamsdemo, "localhost",  "TCP", "8045").
 qactor( sonarsimulator, ctxstreamsdemo, "rx.sonarSimulator").
  qactor( datalogger, ctxstreamsdemo, "rx.dataLogger").
  qactor( datacleaner, ctxstreamsdemo, "rx.dataCleaner").
  qactor( distancefilter, ctxstreamsdemo, "rx.distanceFilter").
  qactor( qasink, ctxstreamsdemo, "it.unibo.qasink.Qasink").
msglogging.
