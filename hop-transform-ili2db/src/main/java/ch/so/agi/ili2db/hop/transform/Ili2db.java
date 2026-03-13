package ch.so.agi.ili2db.hop.transform;

import ch.so.agi.ili2db.core.DatasetMode;
import ch.so.agi.ili2db.core.DefaultIli2dbExecutor;
import ch.so.agi.ili2db.core.GpkgTargetMode;
import ch.so.agi.ili2db.core.Ili2dbExecutionRequest;
import ch.so.agi.ili2db.core.Ili2dbExecutionResult;
import ch.so.agi.ili2db.core.Ili2dbExternalLogLevel;
import ch.so.agi.ili2db.core.Ili2dbFlavor;
import ch.so.agi.ili2db.core.Ili2dbFunction;
import ch.so.agi.ili2db.core.Ili2dbOptionCodec;
import ch.so.agi.ili2db.core.Ili2dbOptionEntry;
import ch.so.agi.ili2db.core.ImportSourceMode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMeta;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransform;
import org.apache.hop.pipeline.transform.TransformMeta;

public class Ili2db extends BaseTransform<Ili2dbMeta, Ili2dbData> {

  private static final Class<?> PKG = Ili2dbMeta.class;
  private static final String ILIDATA_PREFIX = "ilidata:";
  private static final String IMPORT_SOURCE_LIST_SEPARATOR = ";";

  public Ili2db(
      TransformMeta transformMeta,
      Ili2dbMeta meta,
      Ili2dbData data,
      int copyNr,
      PipelineMeta pipelineMeta,
      Pipeline pipeline) {
    super(transformMeta, meta, data, copyNr, pipelineMeta, pipeline);
  }

  @Override
  public boolean processRow() throws HopException {
    Object[] row = getRow();

    if (!data.initialized) {
      initializeData();
    }

    if (row == null) {
      if (canExecuteWithoutInput()) {
        data.emittedSingleStaticRow = true;
        RowExecutionOutcome outcome = executeForRow(null);
        putRow(data.outputRowMeta, createOutputRow(new Object[0], outcome));
        return true;
      }
      setOutputDone();
      return false;
    }

    RowExecutionOutcome outcome = executeForRow(row);
    Ili2dbExecutionResult result = outcome.result();

    if (!result.isSuccess() && meta.isFailOnError()) {
      throw new HopTransformException(result.getMessage(), result.getError());
    }

    putRow(data.outputRowMeta, createOutputRow(row, outcome));

    if (!result.isSuccess() && isBasic()) {
      logBasic(BaseMessages.getString(PKG, "Ili2db.Transform.RunFailed", result.getMessage()));
    }

    return true;
  }

  private void initializeData() throws HopTransformException {
    IRowMeta inputRowMeta = getInputRowMeta();

    if ("SCHEMA_IMPORT".equalsIgnoreCase(meta.getFunction()) && inputRowMeta != null) {
      throw new HopTransformException(
          BaseMessages.getString(PKG, "Ili2db.Transform.SchemaImportWithInputNotSupported"));
    }

    data.outputRowMeta = inputRowMeta == null ? new RowMeta() : inputRowMeta.clone();
    meta.getFields(
        data.outputRowMeta,
        getTransformName(),
        null,
        null,
        this,
        getPipelineMeta().getMetadataProvider());

    if ("FIELD".equalsIgnoreCase(meta.getImportSourceMode())) {
      if (inputRowMeta == null) {
        throw new HopTransformException(
            BaseMessages.getString(PKG, "Ili2db.Transform.NoInputForImportField"));
      }
      data.inputImportFileIndex = inputRowMeta.indexOfValue(meta.getImportFileField());
      if (data.inputImportFileIndex < 0) {
        throw new HopTransformException(
            BaseMessages.getString(
                PKG, "Ili2db.Transform.ImportFieldNotFound", meta.getImportFileField()));
      }
    }

    if ("ILI2GPKG".equalsIgnoreCase(meta.getFlavor())
        && GpkgTargetMode.fromValue(meta.getGpkgTargetMode()) == GpkgTargetMode.FIELD) {
      if (inputRowMeta == null) {
        throw new HopTransformException(
            BaseMessages.getString(PKG, "Ili2db.Transform.NoInputForGpkgField"));
      }
      data.inputGpkgFileIndex = inputRowMeta.indexOfValue(meta.getGpkgFileField());
      if (data.inputGpkgFileIndex < 0) {
        throw new HopTransformException(
            BaseMessages.getString(PKG, "Ili2db.Transform.GpkgFieldNotFound", meta.getGpkgFileField()));
      }
    }

    if ("FIELD".equalsIgnoreCase(meta.getDatasetMode())) {
      if (inputRowMeta == null) {
        throw new HopTransformException(
            BaseMessages.getString(PKG, "Ili2db.Transform.NoInputForDatasetField"));
      }
      data.inputDatasetIndex = inputRowMeta.indexOfValue(meta.getDatasetField());
      if (data.inputDatasetIndex < 0) {
        throw new HopTransformException(
            BaseMessages.getString(PKG, "Ili2db.Transform.DatasetFieldNotFound", meta.getDatasetField()));
      }
    }

    data.outputSuccessIndex =
        data.outputRowMeta.indexOfValue(resolveOutputFieldName(meta.getOutputSuccessField(), "ili2db_success"));
    data.outputMessageIndex =
        data.outputRowMeta.indexOfValue(resolveOutputFieldName(meta.getOutputMessageField(), "ili2db_message"));
    data.outputDatasetIndex =
        data.outputRowMeta.indexOfValue(
            resolveOutputFieldName(meta.getOutputDatasetField(), "ili2db_dataset_effective"));
    data.outputFunctionIndex =
        data.outputRowMeta.indexOfValue(resolveOutputFieldName(meta.getOutputFunctionField(), "ili2db_function"));
    data.outputFlavorIndex =
        data.outputRowMeta.indexOfValue(resolveOutputFieldName(meta.getOutputFlavorField(), "ili2db_flavor"));
    data.outputTargetTypeIndex =
        data.outputRowMeta.indexOfValue(
            resolveOutputFieldName(meta.getOutputTargetTypeField(), "ili2db_target_type"));
    data.outputTargetIdIndex =
        data.outputRowMeta.indexOfValue(resolveOutputFieldName(meta.getOutputTargetIdField(), "ili2db_target_id"));
    data.outputTargetJdbcUrlIndex =
        data.outputRowMeta.indexOfValue(
            resolveOutputFieldName(meta.getOutputTargetJdbcUrlField(), "ili2db_target_jdbc_url"));
    data.outputConnectionIndex =
        data.outputRowMeta.indexOfValue(
            resolveOutputFieldName(meta.getOutputConnectionField(), "ili2db_connection"));
    data.outputTargetFileIndex =
        data.outputRowMeta.indexOfValue(
            resolveOutputFieldName(meta.getOutputTargetFileField(), "ili2db_target_file"));
    data.outputDatabaseSchemaIndex =
        data.outputRowMeta.indexOfValue(
            resolveOutputFieldName(meta.getOutputDatabaseSchemaField(), "ili2db_database_schema"));
    data.outputLogFilePathIndex =
        data.outputRowMeta.indexOfValue(
            resolveOutputFieldName(meta.getOutputLogFilePathField(), "log_file_path"));

    data.executor = new DefaultIli2dbExecutor(this::logExternalMessage);
    data.initialized = true;
  }

  private boolean canExecuteWithoutInput() {
    return getInputRowMeta() == null && !data.emittedSingleStaticRow;
  }

  private String resolveOutputFieldName(String configuredName, String defaultName) {
    return configuredName == null || configuredName.isBlank() ? defaultName : configuredName;
  }

  private RowExecutionOutcome executeForRow(Object[] row) throws HopTransformException {
    Ili2dbExecutionRequest request = new Ili2dbExecutionRequest();

    request.setFlavor(Ili2dbFlavor.fromValue(meta.getFlavor()));
    request.setFunction(Ili2dbFunction.fromValue(meta.getFunction()));
    request.setModelName(resolve(meta.getModelName()));
    request.setModelDir(resolve(meta.getModelDir()));
    request.setSchemaName(resolve(meta.getSchemaName()));
    request.setDefaultSrsCode(resolve(meta.getDefaultSrsCode()));
    request.setImplicitSchemaImport(meta.isImplicitSchemaImport());
    request.setStrokeArcs(meta.isStrokeArcs());
    request.setNameByTopic(meta.isNameByTopic());
    request.setDisableValidation(meta.isDisableValidation());
    request.setImportFile(resolveImportFile(row));
    request.setDatasetName(resolveDatasetValue(row));

    String targetType;
    String targetId;
    String targetJdbcUrl;
    String outputConnection;
    String outputTargetFile;
    String outputDatabaseSchema;

    if (request.getFlavor() == Ili2dbFlavor.ILI2PG) {
      String resolvedConnectionName = resolveConnectionName();
      DatabaseMeta databaseMeta = loadDatabaseMeta(resolvedConnectionName);
      request.setPgHost(resolve(databaseMeta.getHostname()));
      request.setPgPort(resolve(databaseMeta.getPort()));
      request.setPgDatabase(resolve(databaseMeta.getDatabaseName()));
      request.setPgUser(resolve(databaseMeta.getUsername()));
      request.setPgPassword(resolve(databaseMeta.getPassword()));

      targetType = "DB";
      targetId = resolvedConnectionName;
      targetJdbcUrl = buildPgJdbcUrl(request.getPgHost(), request.getPgPort(), request.getPgDatabase());
      outputConnection = resolvedConnectionName;
      outputTargetFile = null;
      outputDatabaseSchema = request.getSchemaName();
    } else {
      String absolutePath = toAbsolutePath(resolveGpkgFile(row));
      request.setGpkgFile(absolutePath);
      targetType = "FILE";
      targetId = absolutePath;
      targetJdbcUrl = buildSqliteJdbcUrl(absolutePath);
      outputConnection = null;
      outputTargetFile = absolutePath;
      outputDatabaseSchema = request.getSchemaName();
    }

    List<Ili2dbOptionEntry> entries = decodeAndResolveOptions(meta.getSerializedOptions());
    String outputLogFilePath = resolveLogFilePath(request);
    if (outputLogFilePath != null) {
      entries = withLogFileOption(entries, outputLogFilePath);
    }
    request.setOptionEntries(entries);

    Ili2dbExecutionResult result = data.executor.execute(request);
    return new RowExecutionOutcome(
        result,
        request.getDatasetName(),
        targetType,
        targetId,
        targetJdbcUrl,
        outputConnection,
        outputTargetFile,
        outputDatabaseSchema,
        outputLogFilePath);
  }

  private String resolveConnectionName() {
    return resolve(meta.getConnectionName());
  }

  private DatabaseMeta loadDatabaseMeta(String connectionName) {
    try {
      DatabaseMeta databaseMeta = DatabaseMeta.loadDatabase(getMetadataProvider(), connectionName);
      if (databaseMeta == null) {
        throw new HopTransformException(
            BaseMessages.getString(PKG, "Ili2db.Transform.ConnectionNotFound", connectionName));
      }
      return databaseMeta;
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  private String resolveImportFile(Object[] row) throws HopTransformException {
    if (Ili2dbFunction.fromValue(meta.getFunction()) == Ili2dbFunction.SCHEMA_IMPORT) {
      return null;
    }

    ImportSourceMode mode = ImportSourceMode.fromValue(meta.getImportSourceMode());
    if (mode == ImportSourceMode.STATIC_PATH) {
      return resolve(meta.getImportFilePath());
    }

    if (row == null || data.inputImportFileIndex < 0) {
      return null;
    }

    try {
      return getInputRowMeta().getString(row, data.inputImportFileIndex);
    } catch (HopValueException e) {
      throw new HopTransformException(
          BaseMessages.getString(PKG, "Ili2db.Transform.ImportFieldReadError", meta.getImportFileField()), e);
    }
  }

  private String resolveDatasetValue(Object[] row) throws HopTransformException {
    DatasetMode mode = DatasetMode.fromValue(meta.getDatasetMode());
    if (mode == DatasetMode.STATIC) {
      return resolve(meta.getDatasetName());
    }

    if (row == null || data.inputDatasetIndex < 0) {
      return null;
    }

    try {
      return getInputRowMeta().getString(row, data.inputDatasetIndex);
    } catch (HopValueException e) {
      throw new HopTransformException(
          BaseMessages.getString(PKG, "Ili2db.Transform.DatasetFieldReadError", meta.getDatasetField()), e);
    }
  }

  private List<Ili2dbOptionEntry> decodeAndResolveOptions(String serialized) {
    List<Ili2dbOptionEntry> source = Ili2dbOptionCodec.decode(serialized);
    List<Ili2dbOptionEntry> resolved = new ArrayList<>(source.size());
    for (Ili2dbOptionEntry entry : source) {
      if (entry == null) {
        continue;
      }
      String value = entry.getValue() == null ? null : resolve(entry.getValue());
      resolved.add(new Ili2dbOptionEntry(entry.getKey(), entry.isEnabled(), value));
    }
    return resolved;
  }

  private String resolveGpkgFile(Object[] row) throws HopTransformException {
    GpkgTargetMode mode = GpkgTargetMode.fromValue(meta.getGpkgTargetMode());
    if (mode == GpkgTargetMode.STATIC_PATH) {
      return resolve(meta.getGpkgFilePath());
    }

    if (row == null || data.inputGpkgFileIndex < 0) {
      return null;
    }

    try {
      return getInputRowMeta().getString(row, data.inputGpkgFileIndex);
    } catch (HopValueException e) {
      throw new HopTransformException(
          BaseMessages.getString(PKG, "Ili2db.Transform.GpkgFieldReadError", meta.getGpkgFileField()), e);
    }
  }

  private static String toAbsolutePath(String path) {
    if (path == null || path.isBlank()) {
      return path;
    }
    return new File(path).getAbsoluteFile().toString();
  }

  private String buildPgJdbcUrl(String host, String port, String database) {
    if (host == null || host.isBlank() || database == null || database.isBlank()) {
      return null;
    }
    if (port == null || port.isBlank()) {
      return "jdbc:postgresql://" + host + "/" + database;
    }
    return "jdbc:postgresql://" + host + ":" + port + "/" + database;
  }

  private String buildSqliteJdbcUrl(String absolutePath) {
    if (absolutePath == null || absolutePath.isBlank()) {
      return null;
    }
    return "jdbc:sqlite:" + absolutePath;
  }

  private String resolveLogFilePath(Ili2dbExecutionRequest request) {
    if (!meta.isCreateLogFile()) {
      return null;
    }
    String resolvedDirectory = resolve(meta.getLogDirectory());
    if (resolvedDirectory == null || resolvedDirectory.isBlank()) {
      return null;
    }

    String fileName = buildLogFileName(request);
    return new File(new File(resolvedDirectory), fileName).getAbsolutePath();
  }

  static String buildLogFileName(Ili2dbExecutionRequest request) {
    String importFile = request == null ? null : request.getImportFile();
    String importFileName = extractImportFileName(importFile);
    if (importFileName != null) {
      return importFileName + ".log";
    }

    return buildFallbackLogFileName(request);
  }

  private static String buildFallbackLogFileName(Ili2dbExecutionRequest request) {
    String flavor = "unknown";
    String function = "unknown";
    if (request != null && request.getFlavor() != null) {
      flavor = request.getFlavor().name().toLowerCase(Locale.ROOT);
    }
    if (request != null && request.getFunction() != null) {
      function = request.getFunction().name().toLowerCase(Locale.ROOT);
    }
    long unique = System.currentTimeMillis();
    long randomPart = Math.abs(System.nanoTime());
    return "ili2db-" + flavor + "-" + function + "-" + unique + "-" + randomPart + ".log";
  }

  private static String extractImportFileName(String importFile) {
    if (importFile == null) {
      return null;
    }
    String trimmed = importFile.trim();
    if (trimmed.isEmpty()) {
      return null;
    }

    if (trimmed.contains(IMPORT_SOURCE_LIST_SEPARATOR)) {
      return null;
    }

    String fileName = trimmed;
    if (trimmed.regionMatches(true, 0, ILIDATA_PREFIX, 0, ILIDATA_PREFIX.length())) {
      fileName = trimmed.substring(ILIDATA_PREFIX.length());
    } else {
      String normalized = trimmed.replace('\\', '/');
      int separatorIndex = normalized.lastIndexOf('/');
      fileName = separatorIndex < 0 ? normalized : normalized.substring(separatorIndex + 1);
    }

    return sanitizeLogFileName(fileName);
  }

  private static String sanitizeLogFileName(String fileName) {
    if (fileName == null) {
      return null;
    }

    String trimmed = fileName.trim();
    if (trimmed.isEmpty()) {
      return null;
    }

    StringBuilder sanitized = new StringBuilder(trimmed.length());
    boolean hasAllowedCharacter = false;
    for (int i = 0; i < trimmed.length(); i++) {
      char character = trimmed.charAt(i);
      if (isAsciiLetterOrDigit(character) || character == '.' || character == '_' || character == '-') {
        sanitized.append(character);
        hasAllowedCharacter = true;
      } else {
        sanitized.append('_');
      }
    }

    if (!hasAllowedCharacter) {
      return null;
    }
    return sanitized.toString();
  }

  private static boolean isAsciiLetterOrDigit(char character) {
    return (character >= 'A' && character <= 'Z')
        || (character >= 'a' && character <= 'z')
        || (character >= '0' && character <= '9');
  }

  private List<Ili2dbOptionEntry> withLogFileOption(
      List<Ili2dbOptionEntry> entries, String logFilePath) {
    List<Ili2dbOptionEntry> result = new ArrayList<>();
    if (entries != null) {
      for (Ili2dbOptionEntry entry : entries) {
        if (entry == null || entry.getKey() == null) {
          continue;
        }
        if ("log".equalsIgnoreCase(entry.getKey())) {
          continue;
        }
        result.add(entry);
      }
    }
    result.add(new Ili2dbOptionEntry("log", true, logFilePath));
    return result;
  }

  private void logExternalMessage(Ili2dbExternalLogLevel level, String message, Throwable throwable) {
    if (level == null || message == null || message.isBlank()) {
      return;
    }
    switch (level) {
      case ERROR -> {
        if (throwable == null) {
          logError(message);
        } else {
          logError(message, throwable);
        }
      }
      case WARN, INFO -> {
        if (isBasic()) {
          logBasic(message);
        }
      }
      case DEBUG -> {
        if (isDetailed()) {
          logDetailed(message);
        }
      }
    }
  }

  private Object[] createOutputRow(Object[] inputRow, RowExecutionOutcome outcome) {
    Ili2dbExecutionResult result = outcome.result();
    Object[] outputRow = RowDataUtil.createResizedCopy(inputRow, data.outputRowMeta.size());
    outputRow[data.outputSuccessIndex] = result.isSuccess();
    outputRow[data.outputMessageIndex] = result.getMessage();
    outputRow[data.outputDatasetIndex] = outcome.datasetValue();
    outputRow[data.outputFunctionIndex] = meta.getFunction();
    outputRow[data.outputFlavorIndex] = meta.getFlavor();
    outputRow[data.outputTargetTypeIndex] = outcome.targetType();
    outputRow[data.outputTargetIdIndex] = outcome.targetId();
    outputRow[data.outputTargetJdbcUrlIndex] = outcome.targetJdbcUrl();
    outputRow[data.outputConnectionIndex] = outcome.connection();
    outputRow[data.outputTargetFileIndex] = outcome.targetFile();
    outputRow[data.outputDatabaseSchemaIndex] = outcome.databaseSchema();
    if (data.outputLogFilePathIndex >= 0) {
      outputRow[data.outputLogFilePathIndex] = outcome.logFilePath();
    }
    return outputRow;
  }

  private record RowExecutionOutcome(
      Ili2dbExecutionResult result,
      String datasetValue,
      String targetType,
      String targetId,
      String targetJdbcUrl,
      String connection,
      String targetFile,
      String databaseSchema,
      String logFilePath) {}
}
