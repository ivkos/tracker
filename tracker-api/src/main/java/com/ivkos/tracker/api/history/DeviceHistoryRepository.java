package com.ivkos.tracker.api.history;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface DeviceHistoryRepository extends JpaRepository<DeviceGpsState, UUID>
{
   List<DeviceGpsState> getAllByDeviceOrderByStateSatelliteTimeDesc(Device device);

   List<DeviceGpsState> getAllByDeviceOrderByStateSatelliteTimeAsc(Device device);

   List<DeviceGpsState> getAllByDeviceAndStateSatelliteTimeGreaterThanEqualAndStateSatelliteTimeLessThanEqual(
         Device device,
         OffsetDateTime from,
         OffsetDateTime to
   );

   Optional<DeviceGpsState> findFirstByDeviceOrderByStateSatelliteTimeDesc(Device device);

   Optional<DeviceGpsState> findOneByIdAndDevice(UUID id, Device device);
}
