package com.ivkos.tracker.api.spring;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.OffsetDateTime;

@Converter(autoApply = true)
public class OffsetDateTimeAttributeConverter implements AttributeConverter<OffsetDateTime, String>
{
   @Override
   public String convertToDatabaseColumn(OffsetDateTime attribute)
   {
      return attribute == null ? null : attribute.toString();
   }

   @Override
   public OffsetDateTime convertToEntityAttribute(String dbData)
   {
      return dbData == null ? null : OffsetDateTime.parse(dbData);
   }
}
