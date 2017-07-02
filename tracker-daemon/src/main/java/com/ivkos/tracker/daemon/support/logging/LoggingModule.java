package com.ivkos.tracker.daemon.support.logging;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class LoggingModule extends AbstractModule
{
   @Override
   protected void configure()
   {
      bindListener(Matchers.any(), new Log4JTypeListener());
   }
}
