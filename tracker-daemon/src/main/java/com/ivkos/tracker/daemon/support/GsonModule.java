package com.ivkos.tracker.daemon.support;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;

import static com.google.inject.Scopes.SINGLETON;

public class GsonModule extends AbstractModule
{
   @Override
   protected void configure()
   {
      bind(Gson.class).in(SINGLETON);
   }
}
