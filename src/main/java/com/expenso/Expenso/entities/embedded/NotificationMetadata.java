package com.expenso.Expenso.entities.embedded;

import java.util.Map;

public class NotificationMetadata {
  private Map<String, Object> data;

  public NotificationMetadata() {}

  public NotificationMetadata(Map<String, Object> data) {
    this.data = data;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }
}
