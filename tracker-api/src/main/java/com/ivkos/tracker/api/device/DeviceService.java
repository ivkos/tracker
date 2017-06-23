package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
class DeviceService
{
   private final DeviceRepository repository;
   private final DeviceVerifier verifier;

   @Autowired
   public DeviceService(DeviceRepository repository, DeviceVerifier verifier)
   {
      this.repository = repository;
      this.verifier = verifier;
   }

   public List<Device> findAll()
   {
      return repository.findAll();
   }

   public Device findById(UUID id)
   {
      return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Device not found"));
   }

   public Device register(Device device)
   {
      boolean idExists = repository.findById(device.getId()).isPresent();
      boolean hardwareIdExists = repository.findByHardwareId(device.getHardwareId()).isPresent();

      if (idExists || hardwareIdExists) {
         throw new EntityExistsException("Device already registered");
      }

      if (!verifier.verify(device)) {
         throw new IllegalArgumentException("Invalid device ID");
      }

      return repository.save(device);
   }

   public Device getDeviceDefinitionByHardwareId(long hardwareId)
   {
      UUID uuid = verifier.calculateUuidForHardwareId(hardwareId);
      return new Device(uuid, hardwareId);
   }
}
