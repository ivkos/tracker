package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static com.ivkos.tracker.api.device.DeviceController.DEVICES;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(DEVICES)
public class DeviceController
{
   public static final String DEVICES = "/devices";
   public static final String DEVICES_UUID_OF_HARDWAREID = "/uuid-of/{hardwareId}";
   public static final String DEVICES_UUID = "/{uuid}";

   private final DeviceService service;

   @Autowired
   DeviceController(DeviceService service)
   {
      this.service = service;
   }

   @GetMapping
   HttpEntity findAll()
   {
      return ok(service.findAll());
   }

   @PostMapping
   HttpEntity registerDevice(@RequestBody @Valid Device device)
   {
      return ok(service.register(device));
   }

   @GetMapping(DEVICES_UUID)
   HttpEntity findById(@PathVariable UUID uuid)
   {
      return ok(service.findById(uuid));
   }

   @GetMapping(DEVICES_UUID_OF_HARDWAREID)
   HttpEntity getUuidForHardwareId(@PathVariable long hardwareId)
   {
      return ok(service.getDeviceDefinitionByHardwareId(hardwareId));
   }
}
