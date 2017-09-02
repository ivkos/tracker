package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ivkos.tracker.core.models.gps.GpsState;

public class GpsStateHistoryUpdater
{
   private final GpsStateHistoryHolder holder;
   private final GpsStatePeriodicConsumer consumer;
   private final GpsStateHistoryFileManager fileManager;

   @Inject
   public GpsStateHistoryUpdater(GpsStateHistoryHolder holder,
                                 GpsStatePeriodicConsumer consumer,
                                 GpsStateHistoryFileManager fileManager,
                                 @Named("daemon.state.updateInterval") long gpsStateUpdateInterval)
   {
      this.holder = holder;
      this.consumer = consumer;
      this.fileManager = fileManager;

      this.consumer.setInterval(gpsStateUpdateInterval);
      this.consumer.setAction(this::storeGpsState);
      this.consumer.start();
   }

   public void storeGpsState(GpsState state)
   {
      if (state == null) return;
      if (!state.isFixAvailable()) return;

      holder.add(state);
      fileManager.write(state);
   }

   public void setInterval(long interval)
   {
      this.consumer.setInterval(interval);
   }
}
