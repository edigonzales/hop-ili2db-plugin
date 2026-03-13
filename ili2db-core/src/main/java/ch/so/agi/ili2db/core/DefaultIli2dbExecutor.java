package ch.so.agi.ili2db.core;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.basics.logging.LogListener;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DefaultIli2dbExecutor implements Ili2dbExecutor {

  private static final Object EXECUTION_LOCK = new Object();

  private final Map<Ili2dbFlavor, Ili2dbFlavorAdapter> adapters;
  private final Ili2dbExternalLogSink externalLogSink;

  public DefaultIli2dbExecutor() {
    this(null);
  }

  public DefaultIli2dbExecutor(Ili2dbExternalLogSink externalLogSink) {
    this.externalLogSink = externalLogSink;
    this.adapters = new EnumMap<>(Ili2dbFlavor.class);
    register(new Ili2pgFlavorAdapter());
    register(new Ili2gpkgFlavorAdapter());
  }

  private void register(Ili2dbFlavorAdapter adapter) {
    adapters.put(adapter.flavor(), adapter);
  }

  @Override
  public Ili2dbExecutionResult execute(Ili2dbExecutionRequest request) {
    try {
      Objects.requireNonNull(request, "request");
      Ili2dbFlavor flavor = Objects.requireNonNull(request.getFlavor(), "flavor");
      Ili2dbFunction function = Objects.requireNonNull(request.getFunction(), "function");

      validateRequest(request);

      Ili2dbFlavorAdapter adapter = adapters.get(flavor);
      if (adapter == null) {
        return Ili2dbExecutionResult.error("Unsupported ili2db flavor: " + flavor, null);
      }

      Config config = new Config();
      adapter.initConfig(config);

      config.setFunction(function.getConfigFunction());

      if (request.getModelName() != null && !request.getModelName().isBlank()) {
        config.setModels(request.getModelName());
      }
      if (request.getModelDir() != null && !request.getModelDir().isBlank()) {
        config.setModeldir(request.getModelDir());
      }
      if (request.getDefaultSrsCode() != null && !request.getDefaultSrsCode().isBlank()) {
        config.setDefaultSrsCode(request.getDefaultSrsCode());
      }

      config.setValidation(!request.isDisableValidation());

      if (request.isStrokeArcs()) {
        Config.setStrokeArcs(config, Config.STROKE_ARCS_ENABLE);
      }
      if (request.isNameByTopic()) {
        config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
      }

      if (request.getDatasetName() != null && !request.getDatasetName().isBlank()) {
        config.setDatasetName(request.getDatasetName());
      }

      if (request.getImportFile() != null && !request.getImportFile().isBlank()) {
        config.setXtffile(request.getImportFile());
        if (request.getImportFile().toLowerCase(Locale.ROOT).endsWith(".itf")) {
          config.setItfTransferfile(true);
        }
      }

      adapter.applyTarget(config, request);

      Ili2dbOptionApplier.apply(config, flavor, function, request.getOptionEntries());

      if (function == Ili2dbFunction.IMPORT
          || function == Ili2dbFunction.REPLACE
          || function == Ili2dbFunction.UPDATE) {
        config.setDoImplicitSchemaImport(request.isImplicitSchemaImport());
      }

      String dbUrl = adapter.getDbUrlConverter().makeUrl(config);
      if (dbUrl != null) {
        config.setDburl(dbUrl);
      }

      LogListener logListener = createLogListener();
      synchronized (EXECUTION_LOCK) {
        if (logListener != null) {
          EhiLogger.getInstance().addListener(logListener);
        }
        try {
          Ili2db.readSettingsFromDb(config);
          Ili2db.run(config, null);
        } finally {
          if (logListener != null) {
            EhiLogger.getInstance().removeListener(logListener);
          }
        }
      }

      return Ili2dbExecutionResult.ok("ili2db executed successfully");
    } catch (Exception e) {
      String message = e.getMessage() == null ? e.toString() : e.getMessage();
      return Ili2dbExecutionResult.error(message, e);
    }
  }

  private void validateRequest(Ili2dbExecutionRequest request) {
    if (request.getFlavor() == Ili2dbFlavor.ILI2PG) {
      require(request.getPgHost(), "PostgreSQL host is required");
      require(request.getPgPort(), "PostgreSQL port is required");
      require(request.getPgDatabase(), "PostgreSQL database is required");
      require(request.getPgUser(), "PostgreSQL user is required");
    }
    if (request.getFlavor() == Ili2dbFlavor.ILI2GPKG) {
      require(request.getGpkgFile(), "GeoPackage target file is required");
    }

    if (request.getFunction() == Ili2dbFunction.SCHEMA_IMPORT) {
      return;
    }

    if (request.getImportFile() == null || request.getImportFile().isBlank()) {
      throw new IllegalArgumentException(
          "Import source is required for function " + request.getFunction());
    }
  }

  private static void require(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
  }

  private LogListener createLogListener() {
    if (externalLogSink == null) {
      return null;
    }
    return event -> {
      if (event == null) {
        return;
      }
      Ili2dbExternalLogLevel level = toExternalLevel(event.getEventKind());
      String message = formatMessage(level, event.getEventMsg());
      externalLogSink.log(level, message, event.getException());
    };
  }

  private static Ili2dbExternalLogLevel toExternalLevel(int eventKind) {
    return switch (eventKind) {
      case LogEvent.ERROR -> Ili2dbExternalLogLevel.ERROR;
      case LogEvent.ADAPTION -> Ili2dbExternalLogLevel.WARN;
      case LogEvent.DEBUG_TRACE,
          LogEvent.STATE_TRACE,
          LogEvent.UNUSUAL_STATE_TRACE,
          LogEvent.BACKEND_CMD -> Ili2dbExternalLogLevel.DEBUG;
      default -> Ili2dbExternalLogLevel.INFO;
    };
  }

  private static String formatMessage(Ili2dbExternalLogLevel level, String eventMessage) {
    String prefix =
        switch (level) {
          case ERROR -> "Error";
          case WARN -> "Warning";
          case DEBUG -> "Debug";
          case INFO -> "Info";
        };

    if (eventMessage == null || eventMessage.isBlank()) {
      return prefix;
    }
    String trimmed = eventMessage.trim();
    if (trimmed.startsWith(prefix + ":")) {
      return trimmed;
    }
    return prefix + ": " + trimmed;
  }
}
