package com.ivkos.tracker.core.models.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class Command
{
   @Column(updatable = false)
   private LocalDateTime dateIssued = now();

   @NonNull
   @Column(nullable = false)
   private CommandType type;

   @Nullable
   private String arguments;

   public Command(@NotNull CommandType type)
   {
      this.type = requireNonNull(type, "type");

      if (type.isArgumentsRequired()) {
         throw new IllegalStateException("Arguments are required for command of type " + type.name());
      }
   }

   public Command(@NotNull CommandType type, @NotNull String arguments)
   {
      this.type = requireNonNull(type, "type");

      if (requireNonNull(arguments, "arguments").isEmpty()) {
         throw new IllegalStateException("Arguments are required for command of type " + type.name());
      }

      this.arguments = arguments;
   }
}
