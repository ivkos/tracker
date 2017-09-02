package com.ivkos.tracker.api.command;

import com.ivkos.tracker.core.models.command.Command;
import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ivkos.tracker.core.constants.ApiEndpoints.COMMANDS;
import static com.ivkos.tracker.core.constants.ApiEndpoints.COMMANDS_ID;
import static org.springframework.http.ResponseEntity.ok;

@RestController
class CommandController
{
   private final DeviceCommandService service;

   @Autowired
   CommandController(DeviceCommandService service)
   {
      this.service = service;
   }

   @GetMapping(COMMANDS)
   HttpEntity getPendingCommands(@AuthenticationPrincipal Device device)
   {
      List<Command> result = service.getAllPendingCommandsByDevice(device).stream()
            .map(DeviceCommand::getCommand)
            .collect(Collectors.toList());

      return ok(result);
   }

   @PutMapping(COMMANDS)
   HttpEntity addCommand(@Valid @RequestBody Command command, @AuthenticationPrincipal Device device)
   {
      return ok(service.add(device, command).getCommand());
   }

   @GetMapping(COMMANDS_ID)
   HttpEntity getCommandById(@PathVariable UUID id, @AuthenticationPrincipal Device device)
   {
      return ok(service.findByIdAndDevice(id, device).getCommand());
   }

   @PatchMapping(COMMANDS_ID)
   HttpEntity confirmReceipt(@PathVariable UUID id, @AuthenticationPrincipal Device device)
   {
      return ok(service.confirmReceipt(id, device).getCommand());
   }
}
