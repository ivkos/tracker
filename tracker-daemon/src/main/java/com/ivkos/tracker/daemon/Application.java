package com.ivkos.tracker.daemon;

import com.google.gson.Gson;
import com.ivkos.tracker.daemon.gps.GpsStatePeriodicReporter;
import com.ivkos.tracker.daemon.gps.GpsdClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.ivkos.tracker.daemon.support.ApplicationInjector.getInjector;
import static java.lang.System.lineSeparator;

public class Application
{
   private static final Logger logger = LogManager.getLogger(Application.class);

   public static void main(String[] args) throws Throwable
   {
      logger.info("Application started");

      logger.info("Starting state updater daemon...");
      getInjector().getInstance(GpsdClient.class).start();

      logger.info("Starting stdout reporter...");
      createStdoutReporter(1000).start();

      logger.info("Starting json reporter...");
      createJsonFileReporter(5000).start();
   }

   private static GpsStatePeriodicReporter createJsonFileReporter(int interval)
   {
      GpsStatePeriodicReporter jsonReporter = getInjector().getInstance(GpsStatePeriodicReporter.class);
      Gson gson = getInjector().getInstance(Gson.class);
      jsonReporter.setInterval(interval);

      jsonReporter.setAction(gpsState -> {
         gpsState.lockRead();
         try {
            if (!gpsState.isDataAvailable()) return;
         } finally {
            gpsState.unlockRead();
         }

         try {
            FileWriter fileWriter = new FileWriter("gps.json", true);

            String json;

            gpsState.lockRead();
            try {
               json = gson.toJson(gpsState);
            } finally {
               gpsState.unlockRead();
            }

            fileWriter.append(json);
            fileWriter.append(lineSeparator());
            fileWriter.close();
         } catch (IOException e) {
            logger.error("Error while writing to json file", e);
         }
      });

      return jsonReporter;
   }

   private static GpsStatePeriodicReporter createStdoutReporter(int interval)
   {
      GpsStatePeriodicReporter stdoutReporter = getInjector().getInstance(GpsStatePeriodicReporter.class);
      stdoutReporter.setInterval(interval);

      stdoutReporter.setAction(gpsState -> {
         System.out.printf("Local time: %s\n%s\n---\n",
               LocalDateTime.now(),
               gpsState
         );
      });

      return stdoutReporter;
   }
}
