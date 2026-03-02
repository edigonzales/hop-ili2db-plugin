package ch.so.agi.ili2db.hop.action;

import ch.so.agi.ili2db.core.DefaultIli2dbExecutor;
import ch.so.agi.ili2db.core.Ili2dbExecutionRequest;
import ch.so.agi.ili2db.core.Ili2dbExecutionResult;
import ch.so.agi.ili2db.core.Ili2dbExternalLogLevel;
import ch.so.agi.ili2db.core.Ili2dbFlavor;
import ch.so.agi.ili2db.core.Ili2dbFunction;
import ch.so.agi.ili2db.core.Ili2dbOptionCodec;
import ch.so.agi.ili2db.core.Ili2dbOptionEntry;
import java.util.ArrayList;
import java.util.List;
import org.apache.hop.core.ICheckResult;
import org.apache.hop.core.Result;
import org.apache.hop.core.annotations.Action;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.ActionBase;
import org.apache.hop.workflow.action.IAction;

@Action(
    id = "INTERLIS_ILI2DB_ACTION",
    name = "i18n::ActionIli2db.Name",
    description = "i18n::ActionIli2db.Description",
    image = "ch/so/agi/ili2db/hop/action/icons/ili2db.svg",
    categoryDescription = "i18n:org.apache.hop.workflow:ActionCategory.Category.General",
    documentationUrl = "/workflow/actions/ili2db.html",
    keywords = {"i18n::ActionIli2db.keyword", "interlis", "ili2db", "ili2pg", "ili2gpkg"})
public class ActionIli2db extends ActionBase implements IAction {

  @HopMetadataProperty private String flavor;
  @HopMetadataProperty private String function;

  @HopMetadataProperty private String connectionName;
  @HopMetadataProperty private String gpkgFilePath;
  @HopMetadataProperty private String schemaName;

  @HopMetadataProperty private String importFilePath;

  @HopMetadataProperty private String modelName;
  @HopMetadataProperty private String modelDir;
  @HopMetadataProperty private String defaultSrsCode;
  @HopMetadataProperty private boolean implicitSchemaImport;
  @HopMetadataProperty private boolean strokeArcs;
  @HopMetadataProperty private boolean nameByTopic;
  @HopMetadataProperty private boolean disableValidation;

  @HopMetadataProperty private String serializedOptions;

  @HopMetadataProperty private String datasetName;

  public ActionIli2db() {
    this("");
  }

  public ActionIli2db(String name) {
    super(name, "");
    setDefault();
  }

  private void setDefault() {
    flavor = "ILI2GPKG";
    function = "IMPORT";

    connectionName = "";
    gpkgFilePath = "";
    schemaName = "";

    importFilePath = "";

    modelName = "";
    modelDir = "";
    defaultSrsCode = "2056";
    implicitSchemaImport = false;
    strokeArcs = true;
    nameByTopic = true;
    disableValidation = false;

    serializedOptions = "";

    datasetName = "";
  }

  @Override
  public Result execute(Result previousResult, int nr) throws HopException {
    Result result = previousResult == null ? new Result() : previousResult;

    Ili2dbExecutionRequest request = new Ili2dbExecutionRequest();
    request.setFlavor(Ili2dbFlavor.fromValue(resolve(flavor)));
    request.setFunction(Ili2dbFunction.fromValue(resolve(function)));

    request.setSchemaName(resolve(schemaName));
    request.setModelName(resolve(modelName));
    request.setModelDir(resolve(modelDir));
    request.setDefaultSrsCode(resolve(defaultSrsCode));
    request.setImplicitSchemaImport(implicitSchemaImport);
    request.setStrokeArcs(strokeArcs);
    request.setNameByTopic(nameByTopic);
    request.setDisableValidation(disableValidation);
    request.setImportFile(resolve(importFilePath));
    request.setDatasetName(resolve(datasetName));

    List<Ili2dbOptionEntry> resolvedOptions = new ArrayList<>();
    for (Ili2dbOptionEntry entry : Ili2dbOptionCodec.decode(serializedOptions)) {
      if (entry == null) {
        continue;
      }
      String value = entry.getValue() == null ? null : resolve(entry.getValue());
      resolvedOptions.add(new Ili2dbOptionEntry(entry.getKey(), entry.isEnabled(), value));
    }
    request.setOptionEntries(resolvedOptions);

    if (request.getFlavor() == Ili2dbFlavor.ILI2PG) {
      DatabaseMeta databaseMeta = loadDatabaseMeta(resolve(connectionName));
      request.setPgHost(resolve(databaseMeta.getHostname()));
      request.setPgPort(resolve(databaseMeta.getPort()));
      request.setPgDatabase(resolve(databaseMeta.getDatabaseName()));
      request.setPgUser(resolve(databaseMeta.getUsername()));
      request.setPgPassword(resolve(databaseMeta.getPassword()));
    } else {
      request.setGpkgFile(resolve(gpkgFilePath));
    }

    Ili2dbExecutionResult executionResult =
        new DefaultIli2dbExecutor(this::logExternalMessage).execute(request);

    if (!executionResult.isSuccess()) {
      result.setResult(false);
      result.increaseErrors(1);
      logError("ili2db action failed: " + executionResult.getMessage(), executionResult.getError());
    } else {
      result.setResult(true);
      if (isBasic()) {
        logBasic("ili2db action executed successfully");
      }
    }

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

  private DatabaseMeta loadDatabaseMeta(String resolvedConnectionName) throws HopException {
    DatabaseMeta databaseMeta = DatabaseMeta.loadDatabase(getMetadataProvider(), resolvedConnectionName);
    if (databaseMeta == null) {
      throw new HopException("Configured database connection not found: " + resolvedConnectionName);
    }
    return databaseMeta;
  }

  @Override
  public void check(
      List<ICheckResult> remarks,
      WorkflowMeta workflowMeta,
      org.apache.hop.core.variables.IVariables variables,
      IHopMetadataProvider metadataProvider) {
    // Runtime validation is done during execute().
  }

  public String getFlavor() {
    return flavor;
  }

  public void setFlavor(String flavor) {
    this.flavor = flavor;
  }

  public String getFunction() {
    return function;
  }

  public void setFunction(String function) {
    this.function = function;
  }

  public String getConnectionName() {
    return connectionName;
  }

  public void setConnectionName(String connectionName) {
    this.connectionName = connectionName;
  }

  public String getGpkgFilePath() {
    return gpkgFilePath;
  }

  public void setGpkgFilePath(String gpkgFilePath) {
    this.gpkgFilePath = gpkgFilePath;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getImportFilePath() {
    return importFilePath;
  }

  public void setImportFilePath(String importFilePath) {
    this.importFilePath = importFilePath;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getDefaultSrsCode() {
    return defaultSrsCode;
  }

  public void setDefaultSrsCode(String defaultSrsCode) {
    this.defaultSrsCode = defaultSrsCode;
  }

  public String getModelDir() {
    return modelDir;
  }

  public void setModelDir(String modelDir) {
    this.modelDir = modelDir;
  }

  public boolean isImplicitSchemaImport() {
    return implicitSchemaImport;
  }

  public void setImplicitSchemaImport(boolean implicitSchemaImport) {
    this.implicitSchemaImport = implicitSchemaImport;
  }

  public boolean isStrokeArcs() {
    return strokeArcs;
  }

  public void setStrokeArcs(boolean strokeArcs) {
    this.strokeArcs = strokeArcs;
  }

  public boolean isNameByTopic() {
    return nameByTopic;
  }

  public void setNameByTopic(boolean nameByTopic) {
    this.nameByTopic = nameByTopic;
  }

  public boolean isDisableValidation() {
    return disableValidation;
  }

  public void setDisableValidation(boolean disableValidation) {
    this.disableValidation = disableValidation;
  }

  public String getSerializedOptions() {
    return serializedOptions;
  }

  public void setSerializedOptions(String serializedOptions) {
    this.serializedOptions = serializedOptions;
  }

  public String getDatasetName() {
    return datasetName;
  }

  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }
}
