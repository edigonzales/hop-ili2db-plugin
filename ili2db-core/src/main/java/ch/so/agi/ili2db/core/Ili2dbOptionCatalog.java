package ch.so.agi.ili2db.core;

import java.util.ArrayList;
import java.util.EnumSet;
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

  public static List<Ili2dbOptionDefinition> definitionsFor(
      Ili2dbFlavor flavor, Ili2dbFunction function) {
    List<Ili2dbOptionDefinition> result = new ArrayList<>();
    for (Ili2dbOptionDefinition definition : DEFINITIONS) {
      if (definition.appliesTo(flavor, function)) {
        result.add(definition);
      }
    }
    return result;
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
      map.put(normalizeKey(definition.getKey()), definition);
    }
    return map;
  }

  private static String normalizeKey(String key) {
    return key.trim().toLowerCase(Locale.ROOT);
  }

  private static List<Ili2dbOptionDefinition> buildDefinitions() {
    List<Ili2dbOptionDefinition> defs = new ArrayList<>();

    // General CLI options not explicitly covered by the Main/Dataset tabs.
    defs.add(Ili2dbOptionDefinition.all("metaConfig", "Meta-Config file", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("validConfig", "Validation config file", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("referenceData", "Reference data file", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("preScript", "Pre script", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("postScript", "Post script", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("dbparams", "DB params file", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("log", "Log file", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("xtflog", "XTF log file", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("proxy", "Proxy host", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("proxyPort", "Proxy port", Ili2dbOptionType.INTEGER));
    defs.add(Ili2dbOptionDefinition.all("verbose", "Verbose validation", Ili2dbOptionType.BOOLEAN));

    defs.add(Ili2dbOptionDefinition.all("deleteData", "Delete data on import", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("disableAreaValidation", "Disable AREA validation", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("disableRounding", "Disable rounding", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("disableBoundaryRecoding", "Disable boundary recoding", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("forceTypeValidation", "Force type validation", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("skipReferenceErrors", "Skip reference errors", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("skipGeometryErrors", "Skip geometry errors", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("skipPolygonBuilding", "Skip polygon building", Ili2dbOptionType.BOOLEAN));

    defs.add(Ili2dbOptionDefinition.all("createEnumTabs", "Create enum tables", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createEnumTabsWithId", "Create enum tables with id", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createSingleEnumTab", "Create single enum table", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createEnumTxtCol", "Create enum text column", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createEnumColAsItfCode", "Enum column as ITF code", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("beautifyEnumDispName", "Beautify enum dispName", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createFk", "Create FK constraints", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createFkIdx", "Create FK indexes", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createUnique", "Create unique constraints", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createNumChecks", "Create numeric check constraints", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createTextChecks", "Create text check constraints", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createDateTimeChecks", "Create datetime check constraints", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createMandatoryChecks", "Create mandatory check constraints", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createImportTabs", "Create import tables", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createStdCols", "Create std columns", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createTypeDiscriminator", "Create type discriminator", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createTypeConstraint", "Create type constraints", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createGeomIdx", "Create geometry index", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createTidCol", "Create TID column", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createBasketCol", "Create basket column", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createDatasetCol", "Create dataset column", Ili2dbOptionType.BOOLEAN));

    defs.add(Ili2dbOptionDefinition.all("importTid", "Import transient TID", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("exportTid", "Export transient TID", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("importBid", "Import transient BID", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("exportFetchSize", "Export fetch size", Ili2dbOptionType.INTEGER));
    defs.add(Ili2dbOptionDefinition.all("importBatchSize", "Import batch size", Ili2dbOptionType.INTEGER));
    defs.add(Ili2dbOptionDefinition.all("maxNameLength", "Max SQL name length", Ili2dbOptionType.INTEGER));
    defs.add(Ili2dbOptionDefinition.all("t_id_Name", "Name of t_id column", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("idSeqMin", "ID sequence minimum", Ili2dbOptionType.INTEGER));
    defs.add(Ili2dbOptionDefinition.all("idSeqMax", "ID sequence maximum", Ili2dbOptionType.INTEGER));

    defs.add(Ili2dbOptionDefinition.all("sqlEnableNull", "Enable SQL nulls", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("sqlColsAsText", "Map all SQL columns as text", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("sqlExtRefCols", "Map ext refs as columns", Ili2dbOptionType.BOOLEAN));

    defs.add(Ili2dbOptionDefinition.all("noSmartMapping", "Disable smart mapping", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("smart1Inheritance", "Enable smart1 inheritance", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("smart2Inheritance", "Enable smart2 inheritance", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("coalesceCatalogueRef", "Coalesce catalogue refs", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("coalesceMultiSurface", "Coalesce multi-surface", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("coalesceMultiLine", "Coalesce multi-line", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("coalesceMultiPoint", "Coalesce multi-point", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("coalesceArray", "Coalesce arrays", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("coalesceJson", "Coalesce JSON", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("expandStruct", "Expand structures", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("expandMultilingual", "Expand multilingual", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("expandLocalised", "Expand localised", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("disableNameOptimization", "Disable name optimization", Ili2dbOptionType.BOOLEAN));

    defs.add(Ili2dbOptionDefinition.all("defaultSrsAuth", "Default SRS authority", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("modelSrsCode", "Model SRS code", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("multiSrs", "Enable multi SRS", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("domains", "Domain assignments", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("altSrsModel", "Alternative SRS model", Ili2dbOptionType.STRING));

    defs.add(Ili2dbOptionDefinition.all("attachmentsPath", "Attachments path", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("translation", "Translation mapping", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("ver3-translation", "Use ver3 translation", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createMetaInfo", "Create metainfo", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("createNlsTab", "Create NLS table", Ili2dbOptionType.BOOLEAN));
    defs.add(Ili2dbOptionDefinition.all("iliMetaAttrs", "ILI metadata attributes file", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("plugins", "Plugin folder", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("nameLang", "Name language", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("baskets", "Baskets", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("topics", "Topics", Ili2dbOptionType.STRING));
    defs.add(Ili2dbOptionDefinition.all("ILIGML20", "Use ILIGML 2.0", Ili2dbOptionType.BOOLEAN));

    defs.add(
        Ili2dbOptionDefinition.forFlavors(
            "setupPgExt",
            "Create PostgreSQL extensions",
            Ili2dbOptionType.BOOLEAN,
            EnumSet.of(Ili2dbFlavor.ILI2PG)));
    defs.add(
        Ili2dbOptionDefinition.forFlavors(
            "oneGeomPerTable",
            "One geometry per table",
            Ili2dbOptionType.BOOLEAN,
            EnumSet.of(Ili2dbFlavor.ILI2PG)));
    defs.add(
        Ili2dbOptionDefinition.forFlavors(
            "gpkgMultiGeomPerTable",
            "GeoPackage multiple geometries per table",
            Ili2dbOptionType.BOOLEAN,
            EnumSet.of(Ili2dbFlavor.ILI2GPKG)));

    return List.copyOf(defs);
  }
}
