package com.expenso.Expenso.entities.embedded.converter;

import com.expenso.Expenso.entities.embedded.AlertResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = false)
public class AlertResultConverter implements AttributeConverter<AlertResult, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(AlertResult attribute) {
    try {
      return objectMapper.writeValueAsString(attribute != null ? attribute.getData() : null);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting AlertResult to JSON", e);
    }
  }

  @Override
  public AlertResult convertToEntityAttribute(String dbData) {
    try {
      Map<String, Object> map = objectMapper.readValue(dbData, Map.class);
      return new AlertResult(map);
    } catch (Exception e) {
      throw new RuntimeException("Error reading JSON into AlertResult", e);
    }
  }
}