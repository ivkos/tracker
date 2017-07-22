package com.ivkos.tracker.daemon.gps;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.ivkos.gpsd4j.client.GpsdClient;
import com.ivkos.gpsd4j.client.GpsdClientOptions;
import com.ivkos.gpsd4j.messages.DeviceMessage;
import com.ivkos.tracker.core.models.gps.GpsState;

import static com.google.inject.Scopes.SINGLETON;

public class GpsModule extends AbstractModule
{
   @Override
   protected void configure()
   {
      bind(GpsState.class).in(SINGLETON);
      bind(GpsdMessageHandler.class).in(SINGLETON);
   }

   @Provides
   @Singleton
   GpsdClient provideGpsdClient(@Named("daemon.gpsd.host") String host,
                                @Named("daemon.gpsd.port") int port,
                                @Named("daemon.gpsd.device") String device)
   {
      GpsdClientOptions options = new GpsdClientOptions()
            .setReconnectAttempts(Integer.MAX_VALUE)
            .setReconnectOnDisconnect(true)
            .setReconnectInterval(2000)
            .setConnectTimeout(2000)
            .setIdleTimeout(10);

      return new GpsdClient(host, port, options)
            .setSuccessfulConnectionHandler(client -> {
               DeviceMessage deviceMsg = new DeviceMessage();
               deviceMsg.setPath(device);
               deviceMsg.setNative(true);

               client.sendCommand(deviceMsg).watch();
            });
   }
}
