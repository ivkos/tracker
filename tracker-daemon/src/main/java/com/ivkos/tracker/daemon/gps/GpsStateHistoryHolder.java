package com.ivkos.tracker.daemon.gps;

import com.ivkos.tracker.core.models.gps.GpsState;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GpsStateHistoryHolder
{
   private final BlockingDeque<GpsState> history = new LinkedBlockingDeque<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();

   public void add(GpsState state)
   {
      lock.writeLock().lock();
      try {
         if (history.remainingCapacity() <= 0) {
            history.pollLast();
         }

         history.add(state);
      } finally {
         lock.writeLock().unlock();
      }
   }

   public List<GpsState> drain(int maxElements)
   {
      List<GpsState> result = new LinkedList<>();

      lock.writeLock().lock();
      try {
         history.drainTo(result, maxElements);
         return Collections.unmodifiableList(result);
      } finally {
         lock.writeLock().unlock();
      }
   }

   public List<GpsState> drain()
   {
      return this.drain(Integer.MAX_VALUE);
   }
}
