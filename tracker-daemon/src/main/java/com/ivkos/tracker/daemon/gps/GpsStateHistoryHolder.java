package com.ivkos.tracker.daemon.gps;

import com.ivkos.tracker.core.models.gps.GpsState;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class GpsStateHistoryHolder
{
   private final ConcurrentSkipListSet<GpsState> history = new ConcurrentSkipListSet<>(new GpsStateComparator());

   public void add(GpsState state)
   {
      history.add(state);
   }

   public Set<GpsState> getAll()
   {
      return Collections.unmodifiableSet(history);
   }

   public boolean removeAll(Set<GpsState> s)
   {
      return history.removeAll(s);
   }

   static class GpsStateComparator implements Comparator<GpsState>
   {
      @Override
      public int compare(GpsState o1, GpsState o2)
      {
         ZonedDateTime state1SatTime = o1.getSatelliteTime();
         ZonedDateTime state2SatTime = o2.getSatelliteTime();

         if (state1SatTime != null && state2SatTime != null) {
            return state1SatTime.compareTo(state2SatTime);
         }

         ZonedDateTime state1LastUpdatedTime = o1.getLastUpdatedTime();
         ZonedDateTime state2LastUpdatedTime = o2.getLastUpdatedTime();

         if (state1LastUpdatedTime != null && state2LastUpdatedTime != null) {
            return state1LastUpdatedTime.compareTo(state2LastUpdatedTime);
         }

         return 0;
      }
   }
}
