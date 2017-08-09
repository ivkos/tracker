package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.ivkos.tracker.core.models.gps.GpsState;

import java.util.function.Consumer;

public class GpsStatePeriodicConsumer extends Thread
{
   public static final long DEFAULT_INTERVAL = 1000;

   private final GlobalGpsState globalGpsState;
   private long interval = DEFAULT_INTERVAL;

   private Consumer<GpsState> consumer;

   @Inject
   GpsStatePeriodicConsumer(GlobalGpsState globalGpsState)
   {
      super(GpsStatePeriodicConsumer.class.getSimpleName());
      this.globalGpsState = globalGpsState;

      this.setDaemon(true);
   }

   public void setAction(Consumer<GpsState> gpsStateConsumer)
   {
      this.consumer = gpsStateConsumer;
   }

   public long getInterval()
   {
      return interval;
   }

   public void setInterval(long interval)
   {
      this.interval = interval;
      this.interrupt();
   }

   @Override
   public void run()
   {
      long localInterval = interval;

      while (!this.isInterrupted()) {
         if (consumer != null) {
            consumer.accept(globalGpsState.copy());
         }

         try {
            Thread.sleep(localInterval);
         } catch (InterruptedException e) {
            // handle interval changes
            if (interval != localInterval) {
               localInterval = interval;
            } else {
               break;
            }
         }
      }
   }
}
