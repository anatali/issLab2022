%====================================================================================
% testguerra description   
%====================================================================================
context(ctx_wasteservice_proto_ctx, "localhost",  "TCP", "8050").
 qactor( wastetruck, ctx_wasteservice_proto_ctx, "it.unibo.wastetruck.Wastetruck").
  qactor( sonarshim, ctx_wasteservice_proto_ctx, "it.unibo.sonarshim.Sonarshim").
  qactor( pathexec, ctx_wasteservice_proto_ctx, "it.unibo.pathexec.Pathexec").
