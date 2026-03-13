package ch.so.agi.ili2db.core;

import java.util.Locale;

public enum GpkgTargetMode {
  STATIC_PATH,
  FIELD;

  public static GpkgTargetMode fromValue(String value) {
    if (value == null || value.isBlank()) {
      return STATIC_PATH;
    }
    return switch (value.trim().toUpperCase(Locale.ROOT)) {
      case "STATIC_PATH" -> STATIC_PATH;
      case "FIELD" -> FIELD;
      default -> throw new IllegalArgumentException("Unsupported GPKG target mode: " + value);
    };
  }
}
