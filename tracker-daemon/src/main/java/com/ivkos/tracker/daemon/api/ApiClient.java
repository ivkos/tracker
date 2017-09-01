package com.ivkos.tracker.daemon.api;

import com.google.inject.Inject;
import com.ivkos.tracker.core.constants.ApiEndpoints;
import com.ivkos.tracker.core.constants.ApiHeaders;
import com.ivkos.tracker.core.models.device.Device;
import com.ivkos.tracker.daemon.support.DeviceDefinitionManager;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import io.vertx.core.AsyncResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.Logger;

public class ApiClient
{
   private final WebClient client;
   private final DeviceDefinitionManager deviceDefinitionManager;
   @InjectLogger
   private Logger logger;

   @Inject
   public ApiClient(WebClient client, DeviceDefinitionManager deviceDefinitionManager)
   {
      this.client = client;
      this.deviceDefinitionManager = deviceDefinitionManager;
   }

   public void sendHeartbeat()
   {
      post(ApiEndpoints.HEARTBEAT).send(this::noop);
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
