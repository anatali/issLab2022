%====================================================================================
% tf22sprint0 description   
%====================================================================================
context(ctxwasteservice, "localhost",  "TCP", "8013").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxdriver, "127.0.0.1",  "TCP", "8040").
context(ctxrasp, "127.0.0.1",  "TCP", "8070").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( wasteservice, ctxwasteservice, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxwasteservice, "it.unibo.transporttrolley.Transporttrolley").
  qactor( wastewervicewtatusgui, ctxwasteservice, "it.unibo.wastewervicewtatusgui.Wastewervicewtatusgui").
  qactor( driversimulator, ctxdriver, "it.unibo.driversimulator.Driversimulator").
  qactor( sonaronrasp, ctxrasp, "it.unibo.sonaronrasp.Sonaronrasp").
