package com.ivkos.tracker.api.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ivkos.tracker.core.models.command.Command;
import com.ivkos.tracker.core.models.device.Device;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "command")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class DeviceCommand
{
   @Id
   @GeneratedValue
   private UUID id;

   @ManyToOne
   @JsonIgnore
   private Device device;

   @Embedded
   private Command command;

   public DeviceCommand(Device device, Command command)
   {
      this.device = device;
      this.command = command;
   }
}
