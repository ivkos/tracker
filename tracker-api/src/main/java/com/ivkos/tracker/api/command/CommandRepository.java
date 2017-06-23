package com.ivkos.tracker.api.command;

import com.ivkos.tracker.core.models.command.Command;
import com.ivkos.tracker.core.models.device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface CommandRepository extends JpaRepository<Command, UUID>
{
   List<Command> getAllByDevice(Device device);

   List<Command> getAllByDeviceAndDateReceivedIsNull(Device device);

   Optional<Command> findById(UUID id);
}
