package com.ivkos.tracker.daemon.gps;

import com.google.inject.AbstractModule;
import com.ivkos.tracker.core.models.gps.GpsState;

import static com.google.inject.Scopes.SINGLETON;

public class GpsModule extends AbstractModule
{
   @Override
   protected void configure()
   {
      bind(GpsState.class).in(SINGLETON);
      bind(GpsdClient.class).in(SINGLETON);
      bind(GpsdMessageListener.class).in(SINGLETON);
   }
}
