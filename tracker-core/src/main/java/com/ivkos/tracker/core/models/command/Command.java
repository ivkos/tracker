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
   private CommandType commandType;

   @Nullable
   private String arguments;

   public Command(@NotNull CommandType commandType)
   {
      this.commandType = requireNonNull(commandType, "commandType");

      if (commandType.isArgumentsRequired()) {
         throw new IllegalStateException("Arguments are required for command of type " + commandType.name());
      }
   }

   public Command(@NotNull CommandType commandType, @NotNull String arguments)
   {
      this.commandType = requireNonNull(commandType, "commandType");

      if (requireNonNull(arguments, "arguments").isEmpty()) {
         throw new IllegalStateException("Arguments are required for command of type " + commandType.name());
      }

      this.arguments = arguments;
   }
}
