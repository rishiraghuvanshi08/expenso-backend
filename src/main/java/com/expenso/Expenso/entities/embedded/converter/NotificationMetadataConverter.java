package com.expenso.Expenso.entities.embedded.converter;

import com.expenso.Expenso.entities.embedded.NotificationMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = false)
public class NotificationMetadataConverter implements AttributeConverter<NotificationMetadata, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(NotificationMetadata attribute) {
    try {
      return objectMapper.writeValueAsString(attribute != null ? attribute.getData() : null);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not convert metadata to JSON", e);
    }
  }

  @Override
  public NotificationMetadata convertToEntityAttribute(String dbData) {
    try {
      return new NotificationMetadata(objectMapper.readValue(dbData, Map.class));
    } catch (Exception e) {
      throw new RuntimeException("Could not convert JSON to metadata", e);
    }
  }
}