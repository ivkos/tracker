package com.ivkos.tracker.core.models.location;

import lombok.*;

import javax.persistence.Embeddable;

import static java.lang.Math.*;
import static java.lang.String.format;

@Getter
@EqualsAndHashCode
@Embeddable
public class Location
{
   @NonNull
   private Double latitude;

   @NonNull
   private Double longitude;

   private Double altitude;

   Location() { }

   public Location(Double latitude, Double longitude)
   {
      this.latitude = latitude;
      this.longitude = longitude;
   }

   public Location(Double latitude, Double longitude, Double altitude)
   {
      this.latitude = latitude;
      this.longitude = longitude;
      this.altitude = altitude;
   }

   public double distanceTo(Location otherLocation)
   {
      return distance(this, otherLocation);
   }

   public static double distance(Location location1, Location location2)
   {
      final short R = 6371;

      double latDistance = toRadians(location2.getLatitude() - location1.getLatitude());
      double lonDistance = toRadians(location2.getLongitude() - location1.getLongitude());

      double a = sin(latDistance / 2)
            * sin(latDistance / 2)
            + cos(toRadians(location1.getLatitude()))
            * cos(toRadians(location2.getLatitude()))
            * sin(lonDistance / 2)
            * sin(lonDistance / 2);

      double c = 2 * atan2(sqrt(a), sqrt(1 - a));

      return R * c * 1000;
   }

   @Override
   public String toString()
   {
      char verticalDirection = latitude <= 0 ? 'S' : 'N';
      char horizontalDirection = longitude <= 0 ? 'W' : 'E';

      String result = format("%f° %s / %f° %s",
            latitude, verticalDirection,
            longitude, horizontalDirection);

      if (altitude != null) {
         result += format(" / %.2f m", altitude);
      }

      return result;
   }
}
