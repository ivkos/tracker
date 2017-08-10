package com.ivkos.tracker.daemon.support;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ivkos.tracker.core.models.device.Device;
import io.vertx.core.json.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class DeviceDefinitionManager
{
   static {
      JavaTimeModule javaTimeModule = new JavaTimeModule();

      Arrays.asList(Json.mapper, Json.prettyMapper)
            .forEach(mapper -> mapper.registerModule(javaTimeModule));
   }

   private final Device device;

   @Inject
   DeviceDefinitionManager(@Named("daemon.deviceDefinitionFilePath") String deviceDefinitionFilePath)
   {
      requireNonNull(deviceDefinitionFilePath, "deviceDefinitionFilePath must not be null");

      try (FileReader reader = new FileReader(deviceDefinitionFilePath)) {
         this.device = Json.mapper.readValue(reader, Device.class);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(format("Device definition file '%s' not found. Has the device been initialized?",
               deviceDefinitionFilePath), e);
      } catch (IOException e) {
         throw new RuntimeException(format("Could not read device definition file '%s'", deviceDefinitionFilePath), e);
      }
   }

   public Device getDeviceDefinition()
   {
      return device;
   }
}
