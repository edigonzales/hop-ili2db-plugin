package ch.so.agi.ili2db.core;

import java.util.Locale;

public enum Ili2dbFlavor {
  ILI2PG,
  ILI2GPKG;

  public static Ili2dbFlavor fromValue(String value) {
    if (value == null || value.isBlank()) {
      return ILI2GPKG;
    }
    return switch (value.trim().toUpperCase(Locale.ROOT)) {
      case "ILI2PG" -> ILI2PG;
      case "ILI2GPKG" -> ILI2GPKG;
      default -> throw new IllegalArgumentException("Unsupported flavor: " + value);
    };
  }
}
