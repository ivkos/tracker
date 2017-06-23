package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeviceVerifierTest
{
   private static final String SECRET = "KqKep61yLNxdQ8ugsdhJZnDZWAQd9aQhkbDKBke2FW4LbDt5O82kUXSlcjKLAIMa";

   private static final String PI_SERIAL = "000000001337d00d";
   private static final UUID EXPECTED_UUID = UUID.fromString("a80eea9d-5d4e-3be9-924d-655771b27b27");

   private final DeviceVerifier verifier = new DeviceVerifier(SECRET);

   @Test(expected = IllegalArgumentException.class)
   public void invalidSerialId()
   {
      verifier.calculateUuidForHardwareId("123x");
   }

   @Test(expected = NullPointerException.class)
   public void nullSerialId()
   {
      verifier.calculateUuidForHardwareId(null);
   }

   @Test
   public void verifyClientDevice()
   {
      Device device = new Device(EXPECTED_UUID, PI_SERIAL);
      assertTrue(verifier.verify(device));
   }

   @Test(expected = NullPointerException.class)
   public void verifyClientDeviceWithNull()
   {
      verifier.verify(null);
   }

   @Test
   public void calculateUuidForSerialId()
   {
      UUID uuid = verifier.calculateUuidForHardwareId(PI_SERIAL);
      assertEquals(EXPECTED_UUID, uuid);

      long longSerialId = Long.parseUnsignedLong(PI_SERIAL, 16);
      UUID uuid2 = verifier.calculateUuidForHardwareId(longSerialId);
      assertEquals(EXPECTED_UUID, uuid2);
   }
}
