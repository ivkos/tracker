package com.ivkos.tracker.core.support;

import com.ivkos.tracker.core.models.gps.GpsState;

import java.time.OffsetDateTime;
import java.util.Comparator;

public class GpsStateComparator implements Comparator<GpsState>
{
   private static GpsStateComparator instance;

   @Override
   public int compare(GpsState o1, GpsState o2)
   {
      OffsetDateTime state1SatTime = o1.getSatelliteTime();
      OffsetDateTime state2SatTime = o2.getSatelliteTime();

      if (state1SatTime != null && state2SatTime != null) {
         return state1SatTime.compareTo(state2SatTime);
      }

      OffsetDateTime state1LastUpdatedTime = o1.getLastUpdatedTime();
      OffsetDateTime state2LastUpdatedTime = o2.getLastUpdatedTime();

      if (state1LastUpdatedTime != null && state2LastUpdatedTime != null) {
         return state1LastUpdatedTime.compareTo(state2LastUpdatedTime);
      }

      return 0;
   }

   public static OffsetDateTime getTimeOfGpsState(GpsState state)
   {
      OffsetDateTime satTime = state.getSatelliteTime();
      OffsetDateTime updTime = state.getLastUpdatedTime();

      return satTime != null ? satTime : updTime;
   }

   public static GpsStateComparator getInstance()
   {
      if (instance == null) {
         instance = new GpsStateComparator();
      }

      return instance;
   }
}
