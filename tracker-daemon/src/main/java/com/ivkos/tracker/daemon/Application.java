package com.ivkos.tracker.daemon;

import com.ivkos.gpsd4j.client.GpsdClient;
import com.ivkos.tracker.daemon.gps.GpsStatePeriodicConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static com.ivkos.tracker.daemon.support.ApplicationInjector.getInjector;

public class Application
{
   private static final Logger logger = LogManager.getLogger(Application.class);

   public static void main(String[] args) throws Throwable
   {
      RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
      long jvmStartupTime = bean.getUptime();

      GpsdClient client = getInjector().getInstance(GpsdClient.class);

      long appStartupTime = bean.getUptime();
      logger.info("Application started in {} ms (JVM: {} ms + App: {} ms)",
            appStartupTime, jvmStartupTime, appStartupTime - jvmStartupTime);

      client.start();

      createStdoutReporter(1000).start();
   }

   private static GpsStatePeriodicConsumer createStdoutReporter(int interval)
   {
      GpsStatePeriodicConsumer stdoutReporter = getInjector().getInstance(GpsStatePeriodicConsumer.class);
      stdoutReporter.setInterval(interval);
      stdoutReporter.setAction(gpsState -> System.out.printf("%s\n---\n", gpsState));

      return stdoutReporter;
   }
}
