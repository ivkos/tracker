package com.ivkos.tracker.core.constants;

public final class ApiEndpoints
{
   public static final String DEVICES = "/devices";
   public static final String DEVICES_ID = DEVICES + "/{id}";
   public static final String DEVICES_UUID_OF_HARDWARE_ID = DEVICES + "/uuid-of/{hardwareId}";

   public static final String HISTORY = "/history";
   public static final String HISTORY_ID = HISTORY + "/{id}";
   public static final String HISTORY_LATEST = HISTORY + "/latest";
   public static final String HISTORY_RANGES = HISTORY + "/ranges";
   public static final String HISTORY_RANGES_FROM_TO = HISTORY_RANGES + "/from/{from}/to/{to}";

   public static final String COMMANDS = "/commands";
   public static final String COMMANDS_ID = COMMANDS + "/{id}";

   private ApiEndpoints() {}
}
