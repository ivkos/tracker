package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.ivkos.tracker.core.models.gps.GpsFixMode;
import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.core.models.location.Location;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import de.taimos.gpsd4java.api.ObjectListener;
import de.taimos.gpsd4java.types.*;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

class GpsdMessageListener extends ObjectListener
{
   private final GpsState state;

   @InjectLogger
   private Logger logger;

   @Inject
   GpsdMessageListener(GpsState state)
   {
      this.state = state;
   }

   @Override
   public void handleTPV(TPVObject obj)
   {
      state.lockWrite();
      try {
         state.setFixMode(toGpsFixMode(obj.getMode()));

         {
            Double timestamp = obj.getTimestamp();
            if (!timestamp.isNaN()) {
               Instant instant = Instant.ofEpochSecond(timestamp.longValue());
               LocalDateTime satelliteTime = LocalDateTime.ofInstant(instant, UTC);
               state.setSatelliteTime(satelliteTime);
            } else {
               state.setSatelliteTime(null);
            }
         }

         {
            Double lat = obj.getLatitude();
            Double lon = obj.getLongitude();
            Double alt = obj.getAltitude();
            if (!lat.isNaN() && !lon.isNaN()) {
               Location location = new Location(lat, lon, coerceNaN(alt));
               state.setLocation(location);
            } else {
               state.setLocation(null);
            }
         }

         state.setSpeed(coerceNaN(obj.getSpeed()));
         state.setCourse(coerceNaN(obj.getCourse()));
         state.setClimbRate(coerceNaN(obj.getClimbRate()));

         state.setErrLat(coerceNaN(obj.getLatitudeError()));
         state.setErrLon(coerceNaN(obj.getLongitudeError()));
         state.setErrAlt(coerceNaN(obj.getAltitudeError()));
      } finally {
         state.unlockWrite();
      }
   }

   @Override
   public void handleSKY(SKYObject obj)
   {
      List<SATObject> satellites = obj.getSatellites();
      int satelliteCount = satellites != null ? satellites.size() : 0;

      state.lockWrite();
      try {
         state.setSatelliteCount(satelliteCount);
      } finally {
         state.unlockWrite();
      }
   }

   @Override
   public void handleATT(ATTObject obj) {}

   @Override
   public void handleDevices(DevicesObject obj)
   {
      obj.getDevices().forEach(device -> logger.info("Listening to device: " + device.getPath()));
   }

   private static Double coerceNaN(Double value)
   {
      if (value == null || value.isNaN()) return null;
      return value;
   }

   private static GpsFixMode toGpsFixMode(ENMEAMode mode)
   {
      switch (mode) {
         case NotSeen:
            return GpsFixMode.NOT_SEEN;
         case NoFix:
            return GpsFixMode.NO_FIX;
         case TwoDimensional:
            return GpsFixMode.FIX_2D;
         case ThreeDimensional:
            return GpsFixMode.FIX_3D;
         default:
            throw new IllegalArgumentException("Cannot convert ENMEAMode " + mode.toString() + " to a GpsFixMode");
      }
   }
}
