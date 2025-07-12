package com.expenso.Expenso.entities.embedded;

import java.util.Map;

public class AlertResult {
  private Map<String, Object> data;

  public AlertResult() {}

  public AlertResult(Map<String, Object> data) {
    this.data = data;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }
}