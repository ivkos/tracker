package com.ivkos.tracker.daemon.gps;

import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.core.support.GpsStateComparator;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class GpsStateHistoryHolder
{
   private final ConcurrentSkipListSet<GpsState> history = new ConcurrentSkipListSet<>(GpsStateComparator.getInstance());

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
}
