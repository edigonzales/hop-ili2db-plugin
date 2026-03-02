package ch.so.agi.ili2db.core;

import java.util.EnumSet;
import java.util.Set;

public class Ili2dbOptionDefinition {
  private final String key;
  private final String label;
  private final Ili2dbOptionType type;
  private final Set<Ili2dbFlavor> flavors;
  private final Set<Ili2dbFunction> functions;

  public Ili2dbOptionDefinition(
      String key,
      String label,
      Ili2dbOptionType type,
      Set<Ili2dbFlavor> flavors,
      Set<Ili2dbFunction> functions) {
    this.key = key;
    this.label = label;
    this.type = type;
    this.flavors = flavors == null ? EnumSet.allOf(Ili2dbFlavor.class) : EnumSet.copyOf(flavors);
    this.functions =
        functions == null ? EnumSet.allOf(Ili2dbFunction.class) : EnumSet.copyOf(functions);
  }

  public static Ili2dbOptionDefinition all(String key, String label, Ili2dbOptionType type) {
    return new Ili2dbOptionDefinition(key, label, type, null, null);
  }

  public static Ili2dbOptionDefinition forFlavors(
      String key, String label, Ili2dbOptionType type, Set<Ili2dbFlavor> flavors) {
    return new Ili2dbOptionDefinition(key, label, type, flavors, null);
  }

  public String getKey() {
    return key;
  }

  public String getLabel() {
    return label;
  }

  public Ili2dbOptionType getType() {
    return type;
  }

  public boolean appliesTo(Ili2dbFlavor flavor, Ili2dbFunction function) {
    return flavors.contains(flavor) && functions.contains(function);
  }
}
