package com.ivkos.tracker.api.command;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface DeviceCommandRepository extends JpaRepository<DeviceCommand, UUID>
{
   List<DeviceCommand> getAllByDevice(Device device);

   List<DeviceCommand> getAllByDeviceAndCommandDateReceivedIsNull(Device device);

   Optional<DeviceCommand> findByCommandIdAndDevice(UUID commandId, Device device);
}
