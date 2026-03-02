package ch.so.agi.ili2db.hop.transform;

import java.util.List;
import org.apache.hop.core.CheckResult;
import org.apache.hop.core.ICheckResult;
import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.value.ValueMetaBoolean;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.TransformMeta;

@Transform(
    id = "INTERLIS_ILI2DB_TRANSFORM",
    name = "i18n::Ili2dbMeta.Name",
    description = "i18n::Ili2dbMeta.Description",
    image = "ch/so/agi/ili2db/hop/transform/icons/ili2db.svg",
    categoryDescription = "i18n:org.apache.hop.pipeline.transform:BaseTransform.Category.Input",
    documentationUrl = "/pipeline/transforms/ili2db.html",
    keywords = {"i18n::Ili2dbMeta.keyword", "interlis", "ili2db", "ili2pg", "ili2gpkg"})
public class Ili2dbMeta extends BaseTransformMeta<Ili2db, Ili2dbData> {

  private static final Class<?> PKG = Ili2dbMeta.class;

  @HopMetadataProperty private String flavor;
  @HopMetadataProperty private String function;

  @HopMetadataProperty private String connectionName;
  @HopMetadataProperty private String gpkgFilePath;
  @HopMetadataProperty private String schemaName;

  @HopMetadataProperty private String importSourceMode;
  @HopMetadataProperty private String importFilePath;
  @HopMetadataProperty private String importFileField;

  @HopMetadataProperty private String modelName;
  @HopMetadataProperty private String modelDir;
  @HopMetadataProperty private String defaultSrsCode;
  @HopMetadataProperty private boolean implicitSchemaImport;
  @HopMetadataProperty private boolean strokeArcs;
  @HopMetadataProperty private boolean nameByTopic;
  @HopMetadataProperty private boolean disableValidation;

  @HopMetadataProperty private String serializedOptions;

  @HopMetadataProperty private String datasetMode;
  @HopMetadataProperty private String datasetName;
  @HopMetadataProperty private String datasetField;

  @HopMetadataProperty private boolean failOnError;

  @HopMetadataProperty private String outputSuccessField;
  @HopMetadataProperty private String outputMessageField;
  @HopMetadataProperty private String outputDatasetField;
  @HopMetadataProperty private String outputFunctionField;
  @HopMetadataProperty private String outputFlavorField;
  @HopMetadataProperty private String outputTargetTypeField;
  @HopMetadataProperty private String outputTargetIdField;
  @HopMetadataProperty private String outputTargetJdbcUrlField;
  @HopMetadataProperty private String outputConnectionField;
  @HopMetadataProperty private String outputTargetFileField;
  @HopMetadataProperty private String outputDatabaseSchemaField;
  @HopMetadataProperty private boolean createLogFile;
  @HopMetadataProperty private String logDirectory;
  @HopMetadataProperty private String outputLogFilePathField;

  @Override
  public void setDefault() {
    flavor = "ILI2GPKG";
    function = "IMPORT";

    connectionName = "";
    gpkgFilePath = "";
    schemaName = "";

    importSourceMode = "STATIC_PATH";
    importFilePath = "";
    importFileField = "";

    modelName = "";
    modelDir = "";
    defaultSrsCode = "2056";
    implicitSchemaImport = false;
    strokeArcs = true;
    nameByTopic = true;
    disableValidation = false;

    serializedOptions = "";

    datasetMode = "STATIC";
    datasetName = "";
    datasetField = "";

    failOnError = false;

    outputSuccessField = "ili2db_success";
    outputMessageField = "ili2db_message";
    outputDatasetField = "ili2db_dataset_effective";
    outputFunctionField = "ili2db_function";
    outputFlavorField = "ili2db_flavor";
    outputTargetTypeField = "ili2db_target_type";
    outputTargetIdField = "ili2db_target_id";
    outputTargetJdbcUrlField = "ili2db_target_jdbc_url";
    outputConnectionField = "ili2db_connection";
    outputTargetFileField = "ili2db_target_file";
    outputDatabaseSchemaField = "ili2db_database_schema";
    createLogFile = false;
    logDirectory = "";
    outputLogFilePathField = "log_file_path";
  }

  @Override
  public void getFields(
      IRowMeta rowMeta,
      String origin,
      IRowMeta[] info,
      TransformMeta nextTransform,
      IVariables variables,
      IHopMetadataProvider metadataProvider)
      throws HopTransformException {
    rowMeta.addValueMeta(new ValueMetaBoolean(resolveFieldName(outputSuccessField, "ili2db_success")));
    rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputMessageField, "ili2db_message")));
    rowMeta.addValueMeta(
        new ValueMetaString(resolveFieldName(outputDatasetField, "ili2db_dataset_effective")));
    rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputFunctionField, "ili2db_function")));
    rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputFlavorField, "ili2db_flavor")));
    rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputTargetTypeField, "ili2db_target_type")));
    rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputTargetIdField, "ili2db_target_id")));
    rowMeta.addValueMeta(
        new ValueMetaString(resolveFieldName(outputTargetJdbcUrlField, "ili2db_target_jdbc_url")));
    rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputConnectionField, "ili2db_connection")));
    rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputTargetFileField, "ili2db_target_file")));
    rowMeta.addValueMeta(
        new ValueMetaString(resolveFieldName(outputDatabaseSchemaField, "ili2db_database_schema")));
    if (createLogFile && logDirectory != null && !logDirectory.isBlank()) {
      rowMeta.addValueMeta(new ValueMetaString(resolveFieldName(outputLogFilePathField, "log_file_path")));
    }
  }

  private String resolveFieldName(String fieldName, String defaultName) {
    return fieldName == null || fieldName.isBlank() ? defaultName : fieldName;
  }

  @Override
  public void check(
      List<ICheckResult> remarks,
      PipelineMeta pipelineMeta,
      TransformMeta transformMeta,
      IRowMeta prev,
      String[] input,
      String[] output,
      IRowMeta info,
      IVariables variables,
      IHopMetadataProvider metadataProvider) {

    if ("ILI2PG".equalsIgnoreCase(flavor)
        && (connectionName == null || connectionName.isBlank())) {
      remarks.add(
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "Ili2dbMeta.CheckResult.ConnectionMissing"),
              transformMeta));
      return;
    }

    if ("ILI2GPKG".equalsIgnoreCase(flavor)
        && (gpkgFilePath == null || gpkgFilePath.isBlank())) {
      remarks.add(
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "Ili2dbMeta.CheckResult.GpkgTargetMissing"),
              transformMeta));
      return;
    }

    if ("FIELD".equalsIgnoreCase(importSourceMode)
        && (importFileField == null || importFileField.isBlank())) {
      remarks.add(
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "Ili2dbMeta.CheckResult.ImportFieldMissing"),
              transformMeta));
      return;
    }

    if (!"SCHEMA_IMPORT".equalsIgnoreCase(function)
        && "STATIC_PATH".equalsIgnoreCase(importSourceMode)
        && (importFilePath == null || importFilePath.isBlank())) {
      remarks.add(
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "Ili2dbMeta.CheckResult.ImportPathMissing"),
              transformMeta));
      return;
    }

    if ("FIELD".equalsIgnoreCase(datasetMode)
        && (datasetField == null || datasetField.isBlank())) {
      remarks.add(
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "Ili2dbMeta.CheckResult.DatasetFieldMissing"),
              transformMeta));
      return;
    }

    if (createLogFile && (logDirectory == null || logDirectory.isBlank())) {
      remarks.add(
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "Ili2dbMeta.CheckResult.LogDirectoryMissing"),
              transformMeta));
      return;
    }

    remarks.add(
        new CheckResult(
            ICheckResult.TYPE_RESULT_OK,
            BaseMessages.getString(PKG, "Ili2dbMeta.CheckResult.Ok"),
            transformMeta));
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

  public String getImportSourceMode() {
    return importSourceMode;
  }

  public void setImportSourceMode(String importSourceMode) {
    this.importSourceMode = importSourceMode;
  }

  public String getImportFilePath() {
    return importFilePath;
  }

  public void setImportFilePath(String importFilePath) {
    this.importFilePath = importFilePath;
  }

  public String getImportFileField() {
    return importFileField;
  }

  public void setImportFileField(String importFileField) {
    this.importFileField = importFileField;
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

  public String getDatasetMode() {
    return datasetMode;
  }

  public void setDatasetMode(String datasetMode) {
    this.datasetMode = datasetMode;
  }

  public String getDatasetName() {
    return datasetName;
  }

  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  public String getDatasetField() {
    return datasetField;
  }

  public void setDatasetField(String datasetField) {
    this.datasetField = datasetField;
  }

  public boolean isFailOnError() {
    return failOnError;
  }

  public void setFailOnError(boolean failOnError) {
    this.failOnError = failOnError;
  }

  public String getOutputSuccessField() {
    return outputSuccessField;
  }

  public void setOutputSuccessField(String outputSuccessField) {
    this.outputSuccessField = outputSuccessField;
  }

  public String getOutputMessageField() {
    return outputMessageField;
  }

  public void setOutputMessageField(String outputMessageField) {
    this.outputMessageField = outputMessageField;
  }

  public String getOutputDatasetField() {
    return outputDatasetField;
  }

  public void setOutputDatasetField(String outputDatasetField) {
    this.outputDatasetField = outputDatasetField;
  }

  public String getOutputFunctionField() {
    return outputFunctionField;
  }

  public void setOutputFunctionField(String outputFunctionField) {
    this.outputFunctionField = outputFunctionField;
  }

  public String getOutputFlavorField() {
    return outputFlavorField;
  }

  public void setOutputFlavorField(String outputFlavorField) {
    this.outputFlavorField = outputFlavorField;
  }

  public String getOutputTargetTypeField() {
    return outputTargetTypeField;
  }

  public void setOutputTargetTypeField(String outputTargetTypeField) {
    this.outputTargetTypeField = outputTargetTypeField;
  }

  public String getOutputTargetIdField() {
    return outputTargetIdField;
  }

  public void setOutputTargetIdField(String outputTargetIdField) {
    this.outputTargetIdField = outputTargetIdField;
  }

  public String getOutputTargetJdbcUrlField() {
    return outputTargetJdbcUrlField;
  }

  public void setOutputTargetJdbcUrlField(String outputTargetJdbcUrlField) {
    this.outputTargetJdbcUrlField = outputTargetJdbcUrlField;
  }

  public String getOutputConnectionField() {
    return outputConnectionField;
  }

  public void setOutputConnectionField(String outputConnectionField) {
    this.outputConnectionField = outputConnectionField;
  }

  public String getOutputTargetFileField() {
    return outputTargetFileField;
  }

  public void setOutputTargetFileField(String outputTargetFileField) {
    this.outputTargetFileField = outputTargetFileField;
  }

  public String getOutputDatabaseSchemaField() {
    return outputDatabaseSchemaField;
  }

  public void setOutputDatabaseSchemaField(String outputDatabaseSchemaField) {
    this.outputDatabaseSchemaField = outputDatabaseSchemaField;
  }

  public boolean isCreateLogFile() {
    return createLogFile;
  }

  public void setCreateLogFile(boolean createLogFile) {
    this.createLogFile = createLogFile;
  }

  public String getLogDirectory() {
    return logDirectory;
  }

  public void setLogDirectory(String logDirectory) {
    this.logDirectory = logDirectory;
  }

  public String getOutputLogFilePathField() {
    return outputLogFilePathField;
  }

  public void setOutputLogFilePathField(String outputLogFilePathField) {
    this.outputLogFilePathField = outputLogFilePathField;
  }
}
