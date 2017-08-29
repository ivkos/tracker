package com.ivkos.tracker.api.history;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface DeviceHistoryRepository extends JpaRepository<DeviceGpsState, UUID>
{
   List<DeviceGpsState> getAllByDeviceOrderByDateCreatedDesc(Device device);
}
