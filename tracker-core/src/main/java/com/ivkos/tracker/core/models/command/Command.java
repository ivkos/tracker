package com.ivkos.tracker.core.models.command;

import com.ivkos.tracker.core.models.device.Device;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;
import static java.util.Objects.requireNonNull;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Entity
public class Command
{
   @Id
   @GeneratedValue
   @NonNull
   private UUID id;

   @Column(updatable = false)
   private ZonedDateTime dateIssued = now();

   @Nullable
   @Setter
   private ZonedDateTime dateReceived;

   @ManyToOne(optional = false)
   @NonNull
   private Device device;

   @NonNull
   @Column(nullable = false)
   private CommandType type;

   @Nullable
   private String arguments;

   public Command(@NotNull Device device, @NotNull CommandType type)
   {
      this.device = requireNonNull(device, "device");
      this.type = requireNonNull(type, "type");

      if (type.isArgumentsRequired()) {
         throw new IllegalStateException("Arguments are required for command of type " + type.name());
      }
   }

   public Command(@NotNull Device device, @NotNull CommandType type, @NotNull String arguments)
   {
      this.device = requireNonNull(device, "device");
      this.type = requireNonNull(type, "type");

      if (requireNonNull(arguments, "arguments").isEmpty()) {
         throw new IllegalStateException("Arguments are required for command of type " + type.name());
      }

      this.arguments = arguments;
   }
}
