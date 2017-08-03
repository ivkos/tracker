package com.ivkos.tracker.daemon.support;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.ivkos.tracker.daemon.gps.GpsModule;
import com.ivkos.tracker.daemon.support.logging.LoggingModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationInjector
{
   private static final Logger logger = LogManager.getLogger(ApplicationInjector.class);
   private static final Injector injector = createInjector();

   private static Injector createInjector()
   {
      logger.info("Creating injector...");

      Injector injector = Guice.createInjector(
            Stage.DEVELOPMENT, // TODO: Change to PRODUCTION upon release

            new LoggingModule(),
            new ApplicationConfigurationModule("application.properties"),
            new GpsModule()
      );

      logger.info("Injector created successfully");

      return injector;
   }

   public static Injector getInjector()
   {
      return injector;
   }
}
