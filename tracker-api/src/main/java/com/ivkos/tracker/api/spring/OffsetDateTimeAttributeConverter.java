package com.ivkos.tracker.api.spring;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.OffsetDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;

import static java.time.temporal.ChronoField.*;

@Converter(autoApply = true)
public class OffsetDateTimeAttributeConverter implements AttributeConverter<OffsetDateTime, String>
{
   private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
         .parseCaseInsensitive()
         .appendValue(YEAR, 4)
         .appendValue(MONTH_OF_YEAR, 2)
         .appendValue(DAY_OF_MONTH, 2)
         .appendValue(HOUR_OF_DAY, 2)
         .appendValue(MINUTE_OF_HOUR, 2)
         .appendValue(SECOND_OF_MINUTE, 2)
         .appendOffset("+HHMM", "+0000")
         .toFormatter()
         .withResolverStyle(ResolverStyle.STRICT)
         .withChronology(IsoChronology.INSTANCE);

   @Override
   public String convertToDatabaseColumn(OffsetDateTime attribute)
   {
      return attribute == null ? null : attribute.format(formatter);
   }

   @Override
   public OffsetDateTime convertToEntityAttribute(String dbData)
   {
      return dbData == null ? null : OffsetDateTime.parse(dbData, formatter);
   }
}
