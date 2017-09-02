package com.ivkos.tracker.daemon.api;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ivkos.tracker.core.models.command.Command;
import com.ivkos.tracker.daemon.gps.GpsStateHistoryUpdater;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

class CommandExecutor
{
   public static final String CMD_REBOOT = "sudo systemctl reboot";
   public static final String CMD_SHUTDOWN = "sudo systemctl poweroff";

   private final ApiClient client;
   private final GpsStateHistoryReporter reporter;
   private final GpsStateHistoryUpdater historyUpdater;

   @InjectLogger
   private Logger logger;

   @Inject
   CommandExecutor(@Named("daemon.api.heartbeatInterval") long heartbeatInterval,
                   ApiClient client, Vertx vertx,
                   GpsStateHistoryReporter reporter, GpsStateHistoryUpdater historyUpdater)
   {
      this.client = client;
      this.reporter = reporter;
      this.historyUpdater = historyUpdater;

      fetchPendingCommands();

      vertx.setPeriodic(heartbeatInterval, __ -> fetchPendingCommands());
   }

   private void fetchPendingCommands()
   {
      client.getPendingCommands(this::handlePendingCommandsResponse);
   }

   private void handlePendingCommandsResponse(AsyncResult<HttpResponse<JsonArray>> res)
   {
      if (res.failed()) {
         logger.error("Could not fetch pending commands", res.cause());
         return;
      }

      List<LinkedHashMap<?, ?>> originalList = res.result().body().getList();
      List<Command> pendingCommands = originalList
            .stream()
            .map(JsonObject::mapFrom)
            .map(o -> o.mapTo(Command.class))
            .collect(toList());

      for (Command command : pendingCommands)
         if (!executeCommand(command))
            break;
   }

   private boolean executeCommand(Command command)
   {
      String arg = command.getArguments();

      client.confirmCommandReceipt(command);

      switch (command.getType()) {
         case REPORT_INTERVAL:
            reporter.setInterval(Long.parseLong(arg));
            return true;

         case TRACK_INTERVAL:
            historyUpdater.setInterval(Long.parseLong(arg));
            return true;

         case RAW:
            executeRawCommand(arg);
            return true;

         case REBOOT:
            executeRawCommand(CMD_REBOOT);
            return false;

         case SHUTDOWN:
            executeRawCommand(CMD_SHUTDOWN);
            return false;

         default:
            return true;
      }
   }

   private void executeRawCommand(String cmd)
   {
      Runtime runtime = Runtime.getRuntime();

      try {
         runtime.exec(cmd);
      } catch (IOException e) {
         logger.error("Command failed: " + cmd, e);
      }
   }

   static class PendingCommandsResponse extends ArrayList<Command> { }
}
