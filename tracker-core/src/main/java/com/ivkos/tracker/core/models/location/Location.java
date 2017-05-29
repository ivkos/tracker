package com.ivkos.tracker.core.models.location;

import lombok.*;

import javax.persistence.Embeddable;

import static java.lang.Math.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class Location
{
   private double latitude;
   private double longitude;

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
}
