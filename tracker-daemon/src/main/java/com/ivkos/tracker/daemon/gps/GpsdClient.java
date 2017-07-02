package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;

import java.io.IOException;

public class GpsdClient
{
   private final String host;
   private final int port;

   private final GpsdMessageListener listener;

   private GPSdEndpoint endpoint;
   private boolean isRunning;

   @Inject
   GpsdClient(@Named("daemon.gpsd.host") String host,
              @Named("daemon.gpsd.port") int port,
              GpsdMessageListener listener)
   {
      this.host = host;
      this.port = port;
      this.listener = listener;
   }

   public void start()
   {
      if (this.isRunning) throw new IllegalStateException("Client is already running");

      ResultParser resultParser = new ResultParser();

      try {
         endpoint = new GPSdEndpoint(host, port, resultParser, false);
         endpoint.addListener(listener);
         endpoint.watch(true, true);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      endpoint.start();
      this.isRunning = true;
   }

   public void stop()
   {
      if (!this.isRunning) throw new IllegalStateException("Client is not running");

      endpoint.stop();
      this.isRunning = false;
   }
}
