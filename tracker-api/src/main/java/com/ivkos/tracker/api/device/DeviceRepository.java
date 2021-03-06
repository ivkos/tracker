package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


interface DeviceRepository extends JpaRepository<Device, UUID>
{
   List<Device> findAllByOrderByDateCreatedAsc();

   Optional<Device> findById(UUID id);

   Optional<Device> findByHardwareId(long hardwareId);
}
