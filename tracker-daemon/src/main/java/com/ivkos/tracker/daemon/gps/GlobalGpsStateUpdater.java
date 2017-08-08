package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.ivkos.gpsd4j.client.GpsdClient;
import com.ivkos.gpsd4j.messages.Satellite;
import com.ivkos.gpsd4j.messages.enums.NMEAMode;
import com.ivkos.gpsd4j.messages.reports.SKYReport;
import com.ivkos.gpsd4j.messages.reports.TPVReport;
import com.ivkos.tracker.core.models.gps.GpsFixMode;
import com.ivkos.tracker.core.models.location.Location;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

class GlobalGpsStateUpdater
{
   private final GlobalGpsState state;
   private final GpsdClient client;

   @InjectLogger
   private Logger logger;

   @Inject
   GlobalGpsStateUpdater(GlobalGpsState state, GpsdClient client)
   {
      this.state = state;
      this.client = client;

      client.addHandler(TPVReport.class, this::handleTPV)
            .addHandler(SKYReport.class, this::handleSKY);
   }

   public void handleTPV(TPVReport tpv)
   {
      state.lockWrite();
      try {
         state.setLastUpdatedTime(ZonedDateTime.now());
         state.setFixMode(toGpsFixMode(tpv.getMode()));
         state.setSatelliteTime(tpv.getTime().atZone(UTC));

         {
            Double lat = tpv.getLatitude();
            Double lon = tpv.getLongitude();
            Double alt = tpv.getAltitude();

            if (lat != null && lon != null) {
               state.setLocation(new Location(lat, lon, alt));
            } else {
               state.setLocation(null);
            }
         }

         state.setSpeed(tpv.getSpeed());
         state.setCourse(tpv.getCourse());
         state.setClimbRate(tpv.getClimbRate());

         state.setErrLat(tpv.getLatitudeError());
         state.setErrLon(tpv.getLongitudeError());
         state.setErrAlt(tpv.getAltitudeError());
      } finally {
         state.unlockWrite();
      }
   }

   public void handleSKY(SKYReport sky)
   {
      List<Satellite> satellites = sky.getSatellites();
      int satelliteCount = satellites != null ? satellites.size() : 0;

      state.lockWrite();
      try {
         state.setLastUpdatedTime(ZonedDateTime.now());
         state.setSatelliteCount(satelliteCount);
      } finally {
         state.unlockWrite();
      }
   }

   private static GpsFixMode toGpsFixMode(NMEAMode mode)
   {
      switch (mode) {
         case NotSet:
            return GpsFixMode.NOT_SEEN;
         case NoFix:
            return GpsFixMode.NO_FIX;
         case TwoDimensional:
            return GpsFixMode.FIX_2D;
         case ThreeDimensional:
            return GpsFixMode.FIX_3D;
         default:
            throw new IllegalArgumentException("Cannot convert NMEAMode " + mode.toString() + " to a GpsFixMode");
      }
   }
}
