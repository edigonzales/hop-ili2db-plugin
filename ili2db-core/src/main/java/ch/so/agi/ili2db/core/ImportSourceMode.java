package ch.so.agi.ili2db.core;

import java.util.Locale;

public enum ImportSourceMode {
  STATIC_PATH,
  FIELD;

  public static ImportSourceMode fromValue(String value) {
    if (value == null || value.isBlank()) {
      return STATIC_PATH;
    }
    return switch (value.trim().toUpperCase(Locale.ROOT)) {
      case "STATIC_PATH" -> STATIC_PATH;
      case "FIELD" -> FIELD;
      default -> throw new IllegalArgumentException("Unsupported import source mode: " + value);
    };
  }
}
