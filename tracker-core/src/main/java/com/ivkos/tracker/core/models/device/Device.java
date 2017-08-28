package com.ivkos.tracker.core.models.device;


import lombok.*;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.Long.parseUnsignedLong;
import static java.time.LocalDateTime.now;

@Entity
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
@Getter
public class Device
{
   @Id
   @NonNull
   private UUID id;

   @Column(unique = true, updatable = false)
   @NonNull
   private Long hardwareId;

   @Column(updatable = false)
   private LocalDateTime dateCreated = now();

   @Setter
   @Nullable
   private LocalDateTime lastSeen;

   public Device(UUID id, String hardwareId)
   {
      this(id, parseUnsignedLong(hardwareId, 16));
   }
}
