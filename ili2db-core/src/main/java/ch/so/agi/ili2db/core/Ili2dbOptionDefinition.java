package ch.so.agi.ili2db.core;

public record Ili2dbOptionDefinition(String key, Ili2dbOptionType type, Ili2dbFlavor flavor) {

  Ili2dbOptionDefinition(String key, Ili2dbOptionType type) {
    this(key, type, null);
  }

  public boolean appliesTo(Ili2dbFlavor selectedFlavor) {
    return flavor == null || flavor == selectedFlavor;
  }
}
