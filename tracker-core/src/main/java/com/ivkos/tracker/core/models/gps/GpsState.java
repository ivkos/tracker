package com.ivkos.tracker.core.models.gps;

import com.ivkos.tracker.core.models.location.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.ZonedDateTime;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class GpsState
{
   private ZonedDateTime lastUpdatedTime;
   private ZonedDateTime satelliteTime;

   @Embedded
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
      if (!isDataAvailable()) {
         return "Data pending...";
      }

      StringBuilder sb = new StringBuilder(256);

      if (lastUpdatedTime != null) {
         sb.append(format("Last updated: %s", lastUpdatedTime));
         sb.append(lineSeparator());
      }

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

      if (errLat != null && errLon != null) {
         sb.append(format(
               "Precision: Lat ±%.2f m / Lon ±%.2f m",
               errLat, errLon
         ));

         if (errAlt != null) {
            sb.append(format(" / Alt ±%.2f m", errAlt));
         }

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
   }
}
