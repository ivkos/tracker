package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static com.ivkos.tracker.core.constants.ApiEndpoints.*;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class DeviceController
{
   private final DeviceService service;

   @Autowired
   DeviceController(DeviceService service)
   {
      this.service = service;
   }

   @GetMapping(DEVICES)
   HttpEntity findAll()
   {
      return ok(service.findAll());
   }

   @PostMapping(DEVICES)
   HttpEntity registerDevice(@RequestBody @Valid Device device)
   {
      return ok(service.register(device));
   }

   @GetMapping(DEVICES_ID)
   HttpEntity findById(@PathVariable UUID uuid)
   {
      return ok(service.findById(uuid));
   }

   @GetMapping(DEVICES_UUID_OF_HARDWARE_ID)
   HttpEntity getUuidForHardwareId(@PathVariable long hardwareId)
   {
      return ok(service.getDeviceDefinitionByHardwareId(hardwareId));
   }
}
