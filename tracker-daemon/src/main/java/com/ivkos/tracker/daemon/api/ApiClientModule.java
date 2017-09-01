package com.ivkos.tracker.daemon.api;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class ApiClientModule extends AbstractModule
{
   @Override
   protected void configure()
   {
      bind(ApiClient.class).in(Scopes.SINGLETON);
      bind(HeartbeatPinger.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   Vertx provideVertx()
   {
      return Vertx.vertx();
   }

   @Provides
   @Singleton
   WebClientOptions provideWebClientOptions(@Named("daemon.api.host") String apiHost,
                                            @Named("daemon.api.port") int apiPort,
                                            @Named("daemon.api.ssl") boolean ssl)
   {
      return new WebClientOptions()
            .setUserAgent("tracker-daemon/1")
            .setKeepAlive(false)
            .setDefaultHost(apiHost)
            .setDefaultPort(apiPort)
            .setSsl(ssl);
   }

   @Provides
   @Singleton
   WebClient provideWebClient(Vertx vertx, WebClientOptions options)
   {
      return WebClient.create(vertx, options);
   }
}
