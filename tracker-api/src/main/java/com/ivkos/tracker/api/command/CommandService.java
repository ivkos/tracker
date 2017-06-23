package com.ivkos.tracker.api.command;

import com.ivkos.tracker.core.models.command.Command;
import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
class CommandService
{
   private final CommandRepository repository;

   @Autowired
   public CommandService(CommandRepository repository)
   {
      this.repository = repository;
   }

   public List<Command> getAllCommandsByDevice(Device device)
   {
      return repository.getAllByDevice(device);
   }

   public List<Command> getAllPendingCommandsByDevice(Device device)
   {
      return repository.getAllByDeviceAndDateReceivedIsNull(device);
   }

   public Command add(Command Command)
   {
      return repository.save(Command);
   }

   public Command confirmReceipt(UUID CommandUuid)
   {
      Command command = repository.findById(CommandUuid)
            .orElseThrow(() -> new EntityNotFoundException("Command not found"));

      command.setDateReceived(now());

      return repository.save(command);
   }
}
