package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.lang.Long.parseUnsignedLong;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Component
public class DeviceVerifier
{
   private final String secret;

   @Autowired
   public DeviceVerifier(@NotNull @Value("${app.secret}") String secret)
   {
      requireNonNull(secret, "secret");
      this.secret = secret;
   }

   public boolean verify(@NotNull Device device)
   {
      requireNonNull(device, "device");
      return calculateUuidForHardwareId(device.getHardwareId())
            .equals(device.getId());
   }

   @NotNull
   public UUID calculateUuidForHardwareId(long hardwareId)
   {
      return UUID.nameUUIDFromBytes(buildSeedFromHardwareId(hardwareId));
   }

   @NotNull
   public UUID calculateUuidForHardwareId(@NotNull String hardwareId)
   {
      requireNonNull(hardwareId, "hardwareId");
      return calculateUuidForHardwareId(parseUnsignedLong(hardwareId, 16));
   }

   private byte[] buildSeedFromHardwareId(long hardwareId)
   {
      return format(
            "%s+%s+%s",
            secret, hardwareId, ((Long) hardwareId).hashCode()
      ).getBytes();
   }
}
