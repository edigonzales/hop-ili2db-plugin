package ch.so.agi.ili2db.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Ili2dbOptionCatalog {

  private static final List<Ili2dbOptionDefinition> DEFINITIONS = buildDefinitions();
  private static final Map<String, Ili2dbOptionDefinition> BY_KEY = byKey(DEFINITIONS);

  private Ili2dbOptionCatalog() {}

  public static List<Ili2dbOptionDefinition> allDefinitions() {
    return DEFINITIONS;
  }

  public static Ili2dbOptionDefinition findByKey(String key) {
    if (key == null) {
      return null;
    }
    return BY_KEY.get(normalizeKey(key));
  }

  private static Map<String, Ili2dbOptionDefinition> byKey(List<Ili2dbOptionDefinition> definitions) {
    Map<String, Ili2dbOptionDefinition> map = new ConcurrentHashMap<>();
    for (Ili2dbOptionDefinition definition : definitions) {
      map.put(normalizeKey(definition.key()), definition);
    }
    return map;
  }

  private static String normalizeKey(String key) {
    return key.trim().toLowerCase(Locale.ROOT);
  }

  private static List<Ili2dbOptionDefinition> buildDefinitions() {
    List<Ili2dbOptionDefinition> defs = new ArrayList<>();

    // General CLI options not explicitly covered by the Main/Dataset tabs.
    defs.add(new Ili2dbOptionDefinition("metaConfig", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("validConfig", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("referenceData", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("preScript", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("postScript", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("dbparams", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("log", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("xtflog", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("proxy", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("proxyPort", Ili2dbOptionType.INTEGER));
    defs.add(new Ili2dbOptionDefinition("verbose", Ili2dbOptionType.BOOLEAN));

    defs.add(new Ili2dbOptionDefinition("deleteData", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("disableAreaValidation", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("disableRounding", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("disableBoundaryRecoding", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("forceTypeValidation", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("skipReferenceErrors", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("skipGeometryErrors", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("skipPolygonBuilding", Ili2dbOptionType.BOOLEAN));

    defs.add(new Ili2dbOptionDefinition("createEnumTabs", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createEnumTabsWithId", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createSingleEnumTab", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createEnumTxtCol", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createEnumColAsItfCode", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("beautifyEnumDispName", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createFk", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createFkIdx", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createUnique", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createNumChecks", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createTextChecks", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createDateTimeChecks", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createMandatoryChecks", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createImportTabs", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createStdCols", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createTypeDiscriminator", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createTypeConstraint", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createGeomIdx", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createTidCol", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createBasketCol", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createDatasetCol", Ili2dbOptionType.BOOLEAN));

    defs.add(new Ili2dbOptionDefinition("importTid", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("exportTid", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("importBid", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("exportFetchSize", Ili2dbOptionType.INTEGER));
    defs.add(new Ili2dbOptionDefinition("importBatchSize", Ili2dbOptionType.INTEGER));
    defs.add(new Ili2dbOptionDefinition("maxNameLength", Ili2dbOptionType.INTEGER));
    defs.add(new Ili2dbOptionDefinition("t_id_Name", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("idSeqMin", Ili2dbOptionType.INTEGER));
    defs.add(new Ili2dbOptionDefinition("idSeqMax", Ili2dbOptionType.INTEGER));

    defs.add(new Ili2dbOptionDefinition("sqlEnableNull", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("sqlColsAsText", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("sqlExtRefCols", Ili2dbOptionType.BOOLEAN));

    defs.add(new Ili2dbOptionDefinition("noSmartMapping", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("smart1Inheritance", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("smart2Inheritance", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("coalesceCatalogueRef", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("coalesceMultiSurface", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("coalesceMultiLine", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("coalesceMultiPoint", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("coalesceArray", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("coalesceJson", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("expandStruct", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("expandMultilingual", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("expandLocalised", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("disableNameOptimization", Ili2dbOptionType.BOOLEAN));

    defs.add(new Ili2dbOptionDefinition("defaultSrsAuth", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("modelSrsCode", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("multiSrs", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("domains", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("altSrsModel", Ili2dbOptionType.STRING));

    defs.add(new Ili2dbOptionDefinition("attachmentsPath", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("translation", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("ver3-translation", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createMetaInfo", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("createNlsTab", Ili2dbOptionType.BOOLEAN));
    defs.add(new Ili2dbOptionDefinition("iliMetaAttrs", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("plugins", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("nameLang", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("baskets", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("topics", Ili2dbOptionType.STRING));
    defs.add(new Ili2dbOptionDefinition("ILIGML20", Ili2dbOptionType.BOOLEAN));

    defs.add(
        new Ili2dbOptionDefinition("setupPgExt", Ili2dbOptionType.BOOLEAN, Ili2dbFlavor.ILI2PG));
    defs.add(
        new Ili2dbOptionDefinition("oneGeomPerTable", Ili2dbOptionType.BOOLEAN, Ili2dbFlavor.ILI2PG));
    defs.add(
        new Ili2dbOptionDefinition("gpkgMultiGeomPerTable", Ili2dbOptionType.BOOLEAN, Ili2dbFlavor.ILI2GPKG));

    return List.copyOf(defs);
  }
}
