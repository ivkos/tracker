package com.ivkos.tracker.api.history;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

interface DeviceHistoryRepository extends JpaRepository<DeviceGpsState, UUID>
{
   Stream<DeviceGpsState> findAllByDeviceAndStateSatelliteTimeGreaterThanEqualAndStateSatelliteTimeLessThanEqual(
         Device device,
         OffsetDateTime from,
         OffsetDateTime to
   );

   Stream<DeviceGpsState> findAllByDeviceOrderByStateSatelliteTimeAsc(Device device);

   Stream<DeviceGpsState> findAllByDeviceOrderByStateSatelliteTimeDesc(Device device);

   Optional<DeviceGpsState> findFirstByDeviceOrderByStateSatelliteTimeDesc(Device device);

   Optional<DeviceGpsState> findOneByIdAndDevice(UUID id, Device device);
}
