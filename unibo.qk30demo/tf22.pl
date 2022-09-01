%====================================================================================
% tf22 description   
%====================================================================================
context(ctxdriver, "127.0.0.1",  "TCP", "8040").
context(ctxwasteservice, "localhost",  "TCP", "8013").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxrasp, "127.0.0.1",  "TCP", "8070").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( wasteservice, ctxwasteservice, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxwasteservice, "it.unibo.transporttrolley.Transporttrolley").
  qactor( driver, ctxdriver, "it.unibo.driver.Driver").
  qactor( sonaronrasp, ctxrasp, "it.unibo.sonaronrasp.Sonaronrasp").
