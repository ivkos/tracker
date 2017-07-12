package com.ivkos.tracker.core.models.gps;

import com.ivkos.tracker.core.models.location.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GpsState
{
   @Getter(AccessLevel.NONE)
   private transient final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

   private LocalDateTime satelliteTime;

   private Location location;
   private Double errLat, errLon, errAlt;

   private Double speed;
   private Double course;
   private Double climbRate;

   private GpsFixMode fixMode;
   private int satelliteCount = 0;

   public boolean isDataAvailable()
   {
      return fixMode != null;
   }

   public boolean isFixAvailable()
   {
      return fixMode != null && fixMode.hasFix();
   }

   @Override
   public String toString()
   {
      lockRead();
      try {
         if (!isDataAvailable()) {
            return "Data pending...";
         }

         StringBuilder sb = new StringBuilder();

         if (satelliteTime != null) {
            sb.append(format("Satellite time: %s", satelliteTime));
            sb.append(lineSeparator());
         }

         sb.append(format("Fix: %s", fixMode));
         sb.append(lineSeparator());

         sb.append(format("Satellites: %d", satelliteCount));
         sb.append(lineSeparator());

         if (!isFixAvailable()) {
            sb.append("No fix available.");
            sb.append(lineSeparator());
         }

         if (location != null) {
            sb.append(format("Location: %s", location));
            sb.append(lineSeparator());
         }

         if (errLat != null && errLon != null && errAlt != null) {
            sb.append(format(
                  "Precision: Lat ±%.2f m / Lon ±%.2f m / Alt ±%.2f m",
                  errLat, errLon, errAlt
            ));
            sb.append(lineSeparator());
         }

         if (speed != null) {
            sb.append(format("Speed: %.0f m/s", speed));
            sb.append(lineSeparator());
         }

         if (climbRate != null) {
            sb.append(format("Climb rate: %.0f m/s", climbRate));
            sb.append(lineSeparator());
         }

         if (course != null) {
            sb.append(format("Course: %.2f°", course));
            sb.append(lineSeparator());
         }

         return sb.toString();
      } finally {
         unlockRead();
      }
   }

   public void lockRead() { this.lock.readLock().lock(); }

   public void unlockRead() { this.lock.readLock().unlock(); }

   public void lockWrite() { this.lock.writeLock().lock(); }

   public void unlockWrite() { this.lock.writeLock().unlock(); }
}
