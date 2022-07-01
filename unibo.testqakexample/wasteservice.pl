%====================================================================================
% wasteservice description   
%====================================================================================
context(ctxwasteservice, "localhost",  "TCP", "8013").
 qactor( wasteservice, ctxwasteservice, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxwasteservice, "it.unibo.transporttrolley.Transporttrolley").
