package com.ivkos.tracker.api.spring;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Converter(autoApply = true)
public class OffsetDateTimeAttributeConverter implements AttributeConverter<OffsetDateTime, Timestamp>
{
   @Override
   public Timestamp convertToDatabaseColumn(OffsetDateTime dateTime)
   {
      if (dateTime == null) {
         return null;
      }

      return Timestamp.from(dateTime.toInstant());
   }

   @Override
   public OffsetDateTime convertToEntityAttribute(Timestamp timestamp)
   {
      if (timestamp == null) {
         return null;
      }

      return OffsetDateTime.ofInstant(timestamp.toInstant(), UTC);
   }
}
