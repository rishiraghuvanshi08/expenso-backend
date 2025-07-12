package com.expenso.Expenso.entities.embedded.converter;

import com.expenso.Expenso.entities.embedded.AlertConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = false)
public class AlertConfigConverter implements AttributeConverter<AlertConfig, String> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(AlertConfig attribute) {
    try {
      return mapper.writeValueAsString(attribute != null ? attribute.getData() : null);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to convert AlertConfig to JSON", e);
    }
  }

  @Override
  public AlertConfig convertToEntityAttribute(String dbData) {
    try {
      Map<String, Object> map = mapper.readValue(dbData, Map.class);
      return new AlertConfig(map);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON to AlertConfig", e);
    }
  }
}