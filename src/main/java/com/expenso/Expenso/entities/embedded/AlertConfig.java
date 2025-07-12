package com.expenso.Expenso.entities.embedded;

import java.util.Map;

public class AlertConfig {
  private Map<String, Object> data;

  public AlertConfig() {}

  public AlertConfig(Map<String, Object> data) {
    this.data = data;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }
}