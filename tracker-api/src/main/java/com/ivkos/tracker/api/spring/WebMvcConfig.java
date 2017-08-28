package com.ivkos.tracker.api.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
class WebMvcConfig extends WebMvcConfigurerAdapter
{
   @Override
   public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
   {
      configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
      configurer.favorPathExtension(false);
      configurer.ignoreAcceptHeader(true);
      configurer.useJaf(false);
   }
}
