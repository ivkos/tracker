package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class GpsStatePeriodicConsumer
{
   public static final long DEFAULT_INTERVAL = 1000;

   private final GlobalGpsState globalGpsState;
   private final Vertx vertx;

   @InjectLogger
   private Logger logger;

   private long interval = DEFAULT_INTERVAL;
   private long currentTimerId;

   private Consumer<GpsState> consumer;

   @Inject
   GpsStatePeriodicConsumer(GlobalGpsState globalGpsState, Vertx vertx)
   {
      this.globalGpsState = globalGpsState;
      this.vertx = vertx;

      this.runTimer();
   }

   private void runTimer()
   {
      this.currentTimerId = vertx.setPeriodic(interval, __ -> doWork());
   }

   public void setAction(Consumer<GpsState> gpsStateConsumer)
   {
      this.consumer = gpsStateConsumer;
   }

   public void setInterval(long interval)
   {
      this.interval = interval;

      vertx.cancelTimer(currentTimerId);
      runTimer();
   }

   private void doWork()
   {
      if (consumer == null) return;

      vertx.executeBlocking(
            future -> {
               try {
                  consumer.accept(globalGpsState.copy());
                  future.complete();
               } catch (Throwable t) {
                  future.fail(t);
               }
            },

            result -> {
               if (result.failed()) {
                  logger.error(GpsStatePeriodicConsumer.class.getSimpleName() + " failed", result.cause());
               }
            }
      );
   }
}
