package ch.so.agi.ili2db.core;

import java.util.Locale;

public enum DatasetMode {
  STATIC,
  FIELD;

  public static DatasetMode fromValue(String value) {
    if (value == null || value.isBlank()) {
      return STATIC;
    }
    return switch (value.trim().toUpperCase(Locale.ROOT)) {
      case "STATIC" -> STATIC;
      case "FIELD" -> FIELD;
      default -> throw new IllegalArgumentException("Unsupported dataset mode: " + value);
    };
  }
}
