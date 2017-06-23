package com.ivkos.tracker.api.location;

import com.ivkos.tracker.core.models.device.Device;
import com.ivkos.tracker.core.models.location.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Entity
@Table(name = "location")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
class DeviceLocation
{
   @Id
   @GeneratedValue
   private UUID id;

   private LocalDateTime dateCreated = now();

   @ManyToOne
   private Device device;

   @Embedded
   private Location location;

   public DeviceLocation(Device device, Location location)
   {
      this.device = device;
      this.location = location;
   }
}
