package com.ivkos.tracker.daemon.api;

import com.google.inject.Inject;
import com.ivkos.tracker.core.constants.ApiEndpoints;
import com.ivkos.tracker.core.constants.ApiHeaders;
import com.ivkos.tracker.core.models.command.Command;
import com.ivkos.tracker.core.models.device.Device;
import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.daemon.gps.GpsStateHistoryHolder;
import com.ivkos.tracker.daemon.support.DeviceDefinitionManager;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class ApiClient
{
   private final WebClient client;
   private final DeviceDefinitionManager deviceDefinitionManager;
   private final GpsStateHistoryHolder historyHolder;

   @InjectLogger
   private Logger logger;

   @Inject
   public ApiClient(WebClient client, DeviceDefinitionManager deviceDefinitionManager, GpsStateHistoryHolder
         historyHolder)
   {
      this.client = client;
      this.deviceDefinitionManager = deviceDefinitionManager;
      this.historyHolder = historyHolder;
   }

   public void sendHistory(Collection<GpsState> history, Handler<AsyncResult<HttpResponse<JsonObject>>> handler)
   {
      put(ApiEndpoints.HISTORY).sendJson(history, handler);
   }

   public void getPendingCommands(Handler<AsyncResult<HttpResponse<JsonArray>>> handler)
   {
      get(ApiEndpoints.COMMANDS)
            .as(BodyCodec.jsonArray())
            .send(handler);
   }

   public void confirmCommandReceipt(Command command)
   {
      patch(ApiEndpoints.COMMANDS + '/' + command.getId())
            .send(this::noop);
   }

   public HttpRequest<JsonObject> get(String url)
   {
      logger.debug("GET {}", url);
      return wrap(client.get(url));
   }

   public HttpRequest<JsonObject> post(String url)
   {
      logger.debug("POST {}", url);
      return wrap(client.post(url));
   }

   public HttpRequest<JsonObject> put(String url)
   {
      logger.debug("PUT {}", url);
      return wrap(client.put(url));
   }

   public HttpRequest<JsonObject> patch(String url)
   {
      logger.debug("PATCH {}", url);
      return wrap(client.patch(url));
   }

   private HttpRequest<JsonObject> wrap(HttpRequest<Buffer> httpRequest)
   {
      Device device = deviceDefinitionManager.getDeviceDefinition();

      return httpRequest
            .putHeader(ApiHeaders.DEVICE_ID, device.getId().toString())
            .putHeader(ApiHeaders.HARDWARE_ID, device.getHardwareId().toString())
            .putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
            .as(BodyCodec.jsonObject());
   }

   private void noop(AsyncResult<HttpResponse<JsonObject>> result)
   {
      if (result.failed()) {
         logger.error("Request failed", result.cause());
      }
   }
}
