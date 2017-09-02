package com.ivkos.tracker.api.command;

import com.ivkos.tracker.core.models.command.Command;
import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static java.time.OffsetDateTime.now;

@Service
class DeviceCommandService
{
   private final DeviceCommandRepository repository;

   @Autowired
   DeviceCommandService(DeviceCommandRepository repository)
   {
      this.repository = repository;
   }

   public List<DeviceCommand> getAllCommandsByDevice(Device device)
   {
      return repository.getAllByDevice(device);
   }

   public DeviceCommand findByIdAndDevice(UUID id, Device device)
   {
      return repository.findByCommandIdAndDevice(id, device)
            .orElseThrow(() -> new EntityNotFoundException("Command not found"));
   }

   public List<DeviceCommand> getAllPendingCommandsByDevice(Device device)
   {
      return repository.getAllByDeviceAndCommandDateReceivedIsNull(device);
   }

   public DeviceCommand add(Device device, Command command)
   {
      DeviceCommand deviceCommand = new DeviceCommand(device, command);
      return repository.save(deviceCommand);
   }

   public DeviceCommand confirmReceipt(UUID commandUuid, Device device)
   {
      DeviceCommand deviceCommand = findByIdAndDevice(commandUuid, device);

      deviceCommand.getCommand().setDateReceived(now());

      return repository.save(deviceCommand);
   }
}
