package com.ivkos.tracker.core.models.command;

import static java.util.Arrays.stream;

public enum CommandType
{
   RAW(0, true),
   REPORT_INTERVAL(1, true),
   TRACK_INTERVAL(2, true),

   REBOOT(98, false),
   SHUTDOWN(99, false);

   private final int code;
   private transient final boolean isArgumentsRequired;

   CommandType(int code, boolean isArgumentsRequired)
   {
      this.code = code;
      this.isArgumentsRequired = isArgumentsRequired;
   }

   public int getCode()
   {
      return code;
   }

   public boolean isArgumentsRequired()
   {
      return isArgumentsRequired;
   }

   public static CommandType valueOf(int code)
   {
      return stream(CommandType.values())
            .filter(commandType -> commandType.getCode() == code)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Enum not found"));
   }
}
