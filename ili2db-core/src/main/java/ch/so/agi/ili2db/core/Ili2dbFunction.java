package ch.so.agi.ili2db.core;

import ch.ehi.ili2db.gui.Config;
import java.util.Locale;

public enum Ili2dbFunction {
  SCHEMA_IMPORT(Config.FC_SCHEMAIMPORT),
  IMPORT(Config.FC_IMPORT),
  REPLACE(Config.FC_REPLACE),
  UPDATE(Config.FC_UPDATE),
  VALIDATE(Config.FC_VALIDATE);

  private final int configFunction;

  Ili2dbFunction(int configFunction) {
    this.configFunction = configFunction;
  }

  public int getConfigFunction() {
    return configFunction;
  }

  public static Ili2dbFunction fromValue(String value) {
    if (value == null || value.isBlank()) {
      return IMPORT;
    }
    return switch (value.trim().toUpperCase(Locale.ROOT)) {
      case "SCHEMA_IMPORT" -> SCHEMA_IMPORT;
      case "IMPORT" -> IMPORT;
      case "REPLACE" -> REPLACE;
      case "UPDATE" -> UPDATE;
      case "VALIDATE" -> VALIDATE;
      default -> throw new IllegalArgumentException("Unsupported function: " + value);
    };
  }
}
