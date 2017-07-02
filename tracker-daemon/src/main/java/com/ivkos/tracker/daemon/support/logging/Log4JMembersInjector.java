package com.ivkos.tracker.daemon.support.logging;

import com.google.inject.MembersInjector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

class Log4JMembersInjector<T> implements MembersInjector<T>
{
   private final Field field;
   private final Logger logger;

   Log4JMembersInjector(Field field)
   {
      this.field = field;

      this.logger = LogManager.getLogger(field.getDeclaringClass());

      field.setAccessible(true);
   }

   @Override
   public void injectMembers(T t)
   {
      try {
         field.set(t, logger);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }
}
