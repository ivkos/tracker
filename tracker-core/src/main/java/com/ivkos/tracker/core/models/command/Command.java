package com.ivkos.tracker.core.models.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.OffsetDateTime;
import java.util.UUID;

import static java.time.OffsetDateTime.now;
import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Embeddable
public class Command
{
   @Column(insertable = false, updatable = false)
   private UUID id;

   @Column(updatable = false)
   private OffsetDateTime dateIssued = now();

   @Setter
   private OffsetDateTime dateReceived;

   @Column(nullable = false)
   private CommandType type;

   private String arguments;

   public Command(CommandType type)
   {
      this.type = requireNonNull(type, "type");

      if (type.isArgumentsRequired()) {
         throw new IllegalStateException("Arguments are required for command of type " + type.name());
      }
   }

   public Command(CommandType type, String arguments)
   {
      this.type = requireNonNull(type, "type");

      if (requireNonNull(arguments, "arguments").isEmpty()) {
         throw new IllegalStateException("Arguments are required for command of type " + type.name());
      }

      this.arguments = arguments;
   }
}
