package com.ivkos.tracker.api.location;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface DeviceLocationRepository extends JpaRepository<DeviceLocation, UUID>
{
   List<DeviceLocation> getAllByDeviceOrderByDateCreatedDesc(Device device);
}
