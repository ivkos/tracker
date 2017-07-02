package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.ivkos.tracker.core.models.gps.GpsState;

import java.util.function.Consumer;

public class GpsStatePeriodicReporter extends Thread
{
   public static final long DEFAULT_INTERVAL = 1000;

   private final GpsState state;
   private long interval = DEFAULT_INTERVAL;

   private Consumer<GpsState> gpsStateConsumer;

   @Inject
   GpsStatePeriodicReporter(GpsState state)
   {
      super(GpsStatePeriodicReporter.class.getSimpleName());
      this.state = state;
   }

   public void setAction(Consumer<GpsState> gpsStateConsumer)
   {
      this.gpsStateConsumer = gpsStateConsumer;
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
         if (gpsStateConsumer != null) {
            gpsStateConsumer.accept(state);
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
