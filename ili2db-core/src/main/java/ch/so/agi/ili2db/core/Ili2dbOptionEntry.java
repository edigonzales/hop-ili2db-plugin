package ch.so.agi.ili2db.core;

import java.util.Objects;

public class Ili2dbOptionEntry {
  private String key;
  private boolean enabled;
  private String value;

  public Ili2dbOptionEntry() {}

  public Ili2dbOptionEntry(String key, boolean enabled, String value) {
    this.key = key;
    this.enabled = enabled;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Ili2dbOptionEntry{"
        + "key='"
        + key
        + '\''
        + ", enabled="
        + enabled
        + ", value='"
        + value
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Ili2dbOptionEntry other)) {
      return false;
    }
    return enabled == other.enabled
        && Objects.equals(key, other.key)
        && Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, enabled, value);
  }
}
