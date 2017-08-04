package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/devices")
class DeviceController
{
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

   @GetMapping("/{uuid}")
   HttpEntity findById(@PathVariable UUID uuid)
   {
      return ok(service.findById(uuid));
   }

   @GetMapping("/uuid-of/{hardwareId}")
   HttpEntity getUuidForHardwareId(@PathVariable long hardwareId,
                                   @RequestHeader("X-Api-Secret") String apiSecretHeader,
                                   @Value("${app.secret}") String apiSecret)
   {
      if (!apiSecret.equals(apiSecretHeader)) {
         // TODO Throw more concrete exception
         throw new RuntimeException("Incorrect API secret");
      }

      return ok(service.getDeviceDefinitionByHardwareId(hardwareId));
   }

   @PostMapping
   HttpEntity registerDevice(@RequestBody @Valid Device device)
   {
      return ok(service.register(device));
   }
}
