package com.expenso.Expenso.entities.embedded.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

@Converter(autoApply = false)
public class DeliveryChannelConverter implements AttributeConverter<List<String>, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting delivery channels to JSON", e);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    try {
      return Arrays.asList(objectMapper.readValue(dbData, String[].class));
    } catch (Exception e) {
      throw new RuntimeException("Error parsing delivery channels from JSON", e);
    }
  }
}

