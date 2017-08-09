package com.ivkos.tracker.daemon.support;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.String.format;

class ApplicationConfigurationModule extends AbstractModule
{
   private final String propertiesFileName;

   ApplicationConfigurationModule(String propertiesFileName)
   {
      this.propertiesFileName = propertiesFileName;
   }

   @Override
   protected void configure()
   {
      Properties props = new Properties();

      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream stream = classLoader.getResourceAsStream(propertiesFileName);

      if (stream == null) {
         throw new RuntimeException(format("Could not get resource file '%s'", propertiesFileName));
      }

      try {
         props.load(stream);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      Names.bindProperties(binder(), props);

      bind(DeviceDefinitionManager.class).asEagerSingleton();
   }
}
