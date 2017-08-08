package com.ivkos.tracker.daemon.gps;

import com.ivkos.tracker.core.models.gps.GpsState;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class GlobalGpsState extends GpsState
{
   private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

   GlobalGpsState()
   {
      super();
   }

   public GpsState copy()
   {
      GpsState copy = new GpsState();

      lockRead();
      try {
         copy.setLastUpdatedTime(getLastUpdatedTime());
         copy.setSatelliteTime(getSatelliteTime());
         copy.setLocation(getLocation());
         copy.setErrLat(getErrLat());
         copy.setErrLon(getErrLon());
         copy.setErrAlt(getErrAlt());
         copy.setSpeed(getSpeed());
         copy.setCourse(getCourse());
         copy.setClimbRate(getClimbRate());
         copy.setFixMode(getFixMode());
         copy.setSatelliteCount(getSatelliteCount());

         return copy;
      } finally {
         unlockRead();
      }
   }

   public void lockRead() { this.lock.readLock().lock(); }

   public void unlockRead() { this.lock.readLock().unlock(); }

   public void lockWrite() { this.lock.writeLock().lock(); }

   public void unlockWrite() { this.lock.writeLock().unlock(); }
}
