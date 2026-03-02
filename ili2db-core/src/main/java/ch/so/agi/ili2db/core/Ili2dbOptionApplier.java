package ch.so.agi.ili2db.core;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import java.util.List;
import java.util.Locale;

public final class Ili2dbOptionApplier {

  private static final String HTTP_PROXY_HOST = "ch.interlis.ili2c.http_proxy_host";
  private static final String HTTP_PROXY_PORT = "ch.interlis.ili2c.http_proxy_port";

  private Ili2dbOptionApplier() {}

  public static void apply(
      Config config,
      Ili2dbFlavor flavor,
      Ili2dbFunction function,
      List<Ili2dbOptionEntry> optionEntries) {
    if (optionEntries == null || optionEntries.isEmpty()) {
      return;
    }

    for (Ili2dbOptionEntry entry : optionEntries) {
      if (entry == null || !entry.isEnabled() || entry.getKey() == null || entry.getKey().isBlank()) {
        continue;
      }
      Ili2dbOptionDefinition definition = Ili2dbOptionCatalog.findByKey(entry.getKey());
      if (definition != null) {
        validateEntryType(definition, entry.getValue());
      }
      if (definition != null && !definition.appliesTo(flavor, function)) {
        continue;
      }
      applySingle(config, flavor, entry.getKey(), entry.getValue());
    }
  }

  private static void validateEntryType(Ili2dbOptionDefinition definition, String value) {
    if (definition.getType() == Ili2dbOptionType.STRING) {
      return;
    }
    if (definition.getType() == Ili2dbOptionType.INTEGER) {
      if (value == null || value.isBlank()) {
        throw new IllegalArgumentException("Missing integer value for option '" + definition.getKey() + "'");
      }
      try {
        Integer.parseInt(value.trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Invalid integer value for option '" + definition.getKey() + "': " + value, e);
      }
      return;
    }
    if (definition.getType() == Ili2dbOptionType.BOOLEAN) {
      if (value == null || value.isBlank()) {
        return;
      }
      if (!isBooleanToken(value)) {
        throw new IllegalArgumentException(
            "Invalid boolean value for option '" + definition.getKey() + "': " + value);
      }
    }
  }

  private static boolean isBooleanToken(String value) {
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    return "true".equals(normalized)
        || "1".equals(normalized)
        || "y".equals(normalized)
        || "yes".equals(normalized)
        || "on".equals(normalized)
        || "false".equals(normalized)
        || "0".equals(normalized)
        || "n".equals(normalized)
        || "no".equals(normalized)
        || "off".equals(normalized);
  }

  private static void applySingle(Config config, Ili2dbFlavor flavor, String rawKey, String value) {
    String key = normalize(rawKey);
    switch (key) {
      case "modeldir" -> config.setModeldir(value);
      case "metaconfig" -> config.setMetaConfigFile(value);
      case "validconfig" -> config.setValidConfigFile(value);
      case "referencedata" -> config.setReferenceData(value);
      case "prescript" -> config.setPreScript(value);
      case "postscript" -> config.setPostScript(value);
      case "dbparams" -> config.setDbParams(value);
      case "log" -> config.setLogfile(value);
      case "xtflog" -> config.setXtfLogfile(value);
      case "proxy" -> config.setValue(HTTP_PROXY_HOST, value);
      case "proxyport" -> config.setValue(HTTP_PROXY_PORT, value);
      case "verbose" -> config.setVerbose(parseBoolean(value, true));

      case "deletedata" -> {
        if (parseBoolean(value, true)) {
          config.setDeleteMode(Config.DELETE_DATA);
        }
      }
      case "doschemaimport" -> config.setDoImplicitSchemaImport(parseBoolean(value, true));
      case "disableareavalidation" -> config.setDisableAreaValidation(parseBoolean(value, true));
      case "disablerounding" -> config.setDisableRounding(parseBoolean(value, true));
      case "disableboundaryrecoding" ->
          config.setRepairTouchingLines(!parseBoolean(value, true));
      case "forcetypevalidation" ->
          config.setOnlyMultiplicityReduction(parseBoolean(value, true));
      case "skipreferenceerrors" -> config.setSkipReferenceErrors(parseBoolean(value, true));
      case "skipgeometryerrors" -> config.setSkipGeometryErrors(parseBoolean(value, true));
      case "skippolygonbuilding" -> {
        if (parseBoolean(value, true)) {
          Ili2db.setSkipPolygonBuilding(config);
        }
      }

      case "createenumtabs" -> {
        if (parseBoolean(value, true)) {
          config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
        }
      }
      case "createenumtabswithid" -> {
        if (parseBoolean(value, true)) {
          config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
        }
      }
      case "createsingleenumtab" -> {
        if (parseBoolean(value, true)) {
          config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
        }
      }
      case "createenumtxtcol" -> {
        if (parseBoolean(value, true)) {
          config.setCreateEnumCols(Config.CREATE_ENUM_TXT_COL);
        }
      }
      case "createenumcolasitfcode" -> {
        if (parseBoolean(value, true)) {
          config.setValue(Config.CREATE_ENUMCOL_AS_ITFCODE, Config.CREATE_ENUMCOL_AS_ITFCODE_YES);
        }
      }
      case "beautifyenumdispname" -> {
        if (parseBoolean(value, true)) {
          config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
        }
      }
      case "createfk" -> {
        if (parseBoolean(value, true)) {
          config.setCreateFk(Config.CREATE_FK_YES);
        }
      }
      case "createfkidx" -> {
        if (parseBoolean(value, true)) {
          config.setCreateFkIdx(Config.CREATE_FKIDX_YES);
        }
      }
      case "createunique" -> config.setCreateUniqueConstraints(parseBoolean(value, true));
      case "createnumchecks" -> config.setCreateNumChecks(parseBoolean(value, true));
      case "createtextchecks" -> config.setCreateTextChecks(parseBoolean(value, true));
      case "createdatetimechecks" -> config.setCreateDateTimeChecks(parseBoolean(value, true));
      case "createmandatorychecks" -> config.setCreateMandatoryChecks(parseBoolean(value, true));
      case "createimporttabs" -> config.setCreateImportTabs(parseBoolean(value, true));
      case "createstdcols" -> {
        if (parseBoolean(value, true)) {
          config.setCreateStdCols(Config.CREATE_STD_COLS_ALL);
        }
      }
      case "createtypediscriminator" -> {
        if (parseBoolean(value, true)) {
          config.setCreateTypeDiscriminator(Config.CREATE_TYPE_DISCRIMINATOR_ALWAYS);
        }
      }
      case "createtypeconstraint", "createtypeconstraints" ->
          config.setCreateTypeConstraint(parseBoolean(value, true));
      case "creategeomidx" -> {
        if (parseBoolean(value, true)) {
          config.setValue(Config.CREATE_GEOM_INDEX, Config.TRUE);
        }
      }
      case "createtidcol" -> {
        if (parseBoolean(value, true)) {
          config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        }
      }
      case "createbasketcol" -> {
        if (parseBoolean(value, true)) {
          config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        }
      }
      case "createdatasetcol" -> {
        if (parseBoolean(value, true)) {
          config.setCreateDatasetCols(Config.CREATE_DATASET_COL);
        }
      }

      case "importtid" -> config.setImportTid(parseBoolean(value, true));
      case "exporttid" -> config.setExportTid(parseBoolean(value, true));
      case "importbid" -> config.setImportBid(parseBoolean(value, true));
      case "exportfetchsize" -> config.setFetchSize(parseInteger(value));
      case "importbatchsize" -> config.setBatchSize(parseInteger(value));
      case "maxnamelength" -> config.setMaxSqlNameLength(value);
      case "t_id_name" -> config.setColT_ID(value);
      case "idseqmin" -> config.setMinIdSeqValue((long) parseInteger(value));
      case "idseqmax" -> config.setMaxIdSeqValue((long) parseInteger(value));

      case "sqlenablenull" -> {
        if (parseBoolean(value, true)) {
          config.setSqlNull(Config.SQL_NULL_ENABLE);
        }
      }
      case "sqlcolsastext" -> {
        if (parseBoolean(value, true)) {
          config.setSqlColsAsText(Config.SQL_COLS_AS_TEXT_ENABLE);
        }
      }
      case "sqlextrefcols" -> {
        if (parseBoolean(value, true)) {
          config.setSqlExtRefCols(Config.SQL_EXTREF_ENABLE);
        }
      }

      case "nosmartmapping" -> {
        if (parseBoolean(value, true)) {
          Ili2db.setNoSmartMapping(config);
        }
      }
      case "smart1inheritance" -> {
        if (parseBoolean(value, true)) {
          config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        }
      }
      case "smart2inheritance" -> {
        if (parseBoolean(value, true)) {
          config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        }
      }
      case "coalescecatalogueref" -> {
        if (parseBoolean(value, true)) {
          config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
        }
      }
      case "coalescemultisurface" -> {
        if (parseBoolean(value, true)) {
          config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
        }
      }
      case "coalescemultiline" -> {
        if (parseBoolean(value, true)) {
          config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
        }
      }
      case "coalescemultipoint" -> {
        if (parseBoolean(value, true)) {
          config.setMultiPointTrafo(Config.MULTIPOINT_TRAFO_COALESCE);
        }
      }
      case "coalescearray" -> {
        if (parseBoolean(value, true)) {
          config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
        }
      }
      case "coalescejson" -> {
        if (parseBoolean(value, true)) {
          config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
        }
      }
      case "expandstruct" -> {
        if (parseBoolean(value, true)) {
          config.setStructTrafo(Config.STRUCT_TRAFO_EXPAND);
        }
      }
      case "expandmultilingual" -> {
        if (parseBoolean(value, true)) {
          config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
        }
      }
      case "expandlocalised" -> {
        if (parseBoolean(value, true)) {
          config.setLocalisedTrafo(Config.LOCALISED_TRAFO_EXPAND);
        }
      }
      case "disablenameoptimization" -> {
        if (parseBoolean(value, true)) {
          config.setNameOptimization(Config.NAME_OPTIMIZATION_DISABLE);
        }
      }

      case "defaultsrsauth" -> config.setDefaultSrsAuthority(value);
      case "modelsrscode" -> config.setModelSrsCode(value);
      case "multisrs" -> config.setUseEpsgInNames(parseBoolean(value, true));
      case "domains" -> config.setDomainAssignments(value);
      case "altsrsmodel" -> config.setSrsModelAssignment(value);

      case "attachmentspath" -> config.setAttachmentsPath(value);
      case "translation" -> config.setIli1Translation(value);
      case "ver3-translation" -> config.setVer3_translation(parseBoolean(value, true));
      case "createmetainfo" -> config.setCreateMetaInfo(parseBoolean(value, true));
      case "createnlstab" -> config.setCreateNlsTab(parseBoolean(value, true));
      case "ilimetaattrs" -> config.setIliMetaAttrsFile(value);
      case "plugins" -> config.setPluginsFolder(value);
      case "namelang" -> config.setNameLanguage(value);
      case "baskets" -> config.setBaskets(value);
      case "topics" -> config.setTopics(value);
      case "iligml20" -> {
        if (parseBoolean(value, true)) {
          config.setTransferFileFormat(Config.ILIGML20);
        }
      }

      case "setuppgext" -> {
        if (flavor == Ili2dbFlavor.ILI2PG) {
          config.setSetupPgExt(parseBoolean(value, true));
        }
      }
      case "onegeompertable" -> {
        if (flavor == Ili2dbFlavor.ILI2PG) {
          config.setOneGeomPerTable(parseBoolean(value, true));
        }
      }
      case "gpkgmultigeompertable" -> {
        if (flavor == Ili2dbFlavor.ILI2GPKG) {
          config.setOneGeomPerTable(!parseBoolean(value, true));
        }
      }

      default -> {
        // Ignore unsupported keys to keep forward compatibility.
      }
    }
  }

  private static String normalize(String key) {
    return key == null ? "" : key.trim().toLowerCase(Locale.ROOT);
  }

  private static boolean parseBoolean(String value, boolean defaultValue) {
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    return switch (value.trim().toLowerCase(Locale.ROOT)) {
      case "true", "1", "y", "yes", "on" -> true;
      case "false", "0", "n", "no", "off" -> false;
      default -> defaultValue;
    };
  }

  private static int parseInteger(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Missing integer value");
    }
    return Integer.parseInt(value.trim());
  }
}
