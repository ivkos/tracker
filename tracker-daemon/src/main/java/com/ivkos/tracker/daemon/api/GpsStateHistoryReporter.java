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

   private int historyReportInterval;
   private long currentTimerId;

   @InjectLogger
   private Logger logger;

   @Inject
   GpsStateHistoryReporter(@Named("daemon.api.historyReportInterval") int historyReportInterval,
                           @Named("daemon.gpsStateHistoryUpdateInterval") long gpsStateHistoryUpdateInterval,
                           GpsStateHistoryHolder historyHolder, Vertx vertx, ApiClient client)
   {
      this.historyReportInterval = historyReportInterval;

      this.historyHolder = historyHolder;
      this.vertx = vertx;
      this.client = client;

      // This is done once so we send the server a location (if available) as soon as possible after launch
      vertx.setTimer(gpsStateHistoryUpdateInterval + 1000, __ -> sendHistory());

      runTimer();
   }

   public void setInterval(int interval)
   {
      this.historyReportInterval = interval;

      vertx.cancelTimer(currentTimerId);
      runTimer();
   }

   private void runTimer()
   {
      this.currentTimerId = vertx.setPeriodic(historyReportInterval * 1000, __ -> sendHistory());
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
