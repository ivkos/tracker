package com.ivkos.tracker.daemon.api;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.Vertx;

class HeartbeatPinger
{
   @Inject
   HeartbeatPinger(@Named("daemon.api.heartbeatInterval") long heartbeatInterval, ApiClient client, Vertx vertx)
   {
      client.sendHeartbeat();

      if (heartbeatInterval > 0) {
         vertx.setPeriodic(heartbeatInterval, __ -> client.sendHeartbeat());
      }
   }
}
