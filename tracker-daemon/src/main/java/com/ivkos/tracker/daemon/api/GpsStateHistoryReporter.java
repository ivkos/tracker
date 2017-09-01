package com.ivkos.tracker.daemon.api;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.daemon.gps.GpsStateHistoryHolder;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class GpsStateHistoryReporter
{
   private final GpsStateHistoryHolder historyHolder;
   private final Vertx vertx;
   private final ApiClient client;

   private long locationHistoryReportInterval;
   private long currentTimerId;

   @InjectLogger
   private Logger logger;

   @Inject
   GpsStateHistoryReporter(@Named("daemon.api.locationHistoryReportInterval") long locationHistoryReportInterval,
                           @Named("daemon.state.updateInterval") long gpsStateUpdateInterval,
                           GpsStateHistoryHolder historyHolder, Vertx vertx, ApiClient client)
   {
      this.locationHistoryReportInterval = locationHistoryReportInterval;

      this.historyHolder = historyHolder;
      this.vertx = vertx;
      this.client = client;

      // This is done once so we send the server a location (if available) as soon as possible after launch
      vertx.setTimer(gpsStateUpdateInterval + 1000, __ -> sendHistory());

      runTimer();
   }

   public void setInterval(long interval)
   {
      this.locationHistoryReportInterval = interval;

      vertx.cancelTimer(currentTimerId);
      runTimer();
   }

   private void runTimer()
   {
      this.currentTimerId = vertx.setPeriodic(locationHistoryReportInterval, __ -> sendHistory());
   }

   private void sendHistory()
   {
      Set<GpsState> historyView = historyHolder.getAll();

      client.sendHistory(historyView, res -> {
         if (res.succeeded()) {
            historyHolder.removeAll(historyView);
         } else {
            logger.error("Could not send GPS state history to server", res.cause());
         }
      });
   }
}
