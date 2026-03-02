package ch.so.agi.ili2db.core;

import java.util.ArrayList;
import java.util.List;

public class Ili2dbExecutionRequest {

  private Ili2dbFlavor flavor;
  private Ili2dbFunction function;

  private String modelName;
  private String modelDir;
  private String schemaName;
  private String defaultSrsCode = "2056";
  private boolean implicitSchemaImport;
  private boolean strokeArcs = true;
  private boolean nameByTopic = true;
  private boolean disableValidation;

  private String importFile;
  private String datasetName;

  private String pgHost;
  private String pgPort;
  private String pgDatabase;
  private String pgUser;
  private String pgPassword;

  private String gpkgFile;

  private List<Ili2dbOptionEntry> optionEntries = new ArrayList<>();

  public Ili2dbFlavor getFlavor() {
    return flavor;
  }

  public void setFlavor(Ili2dbFlavor flavor) {
    this.flavor = flavor;
  }

  public Ili2dbFunction getFunction() {
    return function;
  }

  public void setFunction(Ili2dbFunction function) {
    this.function = function;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getModelDir() {
    return modelDir;
  }

  public void setModelDir(String modelDir) {
    this.modelDir = modelDir;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getDefaultSrsCode() {
    return defaultSrsCode;
  }

  public void setDefaultSrsCode(String defaultSrsCode) {
    this.defaultSrsCode = defaultSrsCode;
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

  public String getImportFile() {
    return importFile;
  }

  public void setImportFile(String importFile) {
    this.importFile = importFile;
  }

  public String getDatasetName() {
    return datasetName;
  }

  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  public String getPgHost() {
    return pgHost;
  }

  public void setPgHost(String pgHost) {
    this.pgHost = pgHost;
  }

  public String getPgPort() {
    return pgPort;
  }

  public void setPgPort(String pgPort) {
    this.pgPort = pgPort;
  }

  public String getPgDatabase() {
    return pgDatabase;
  }

  public void setPgDatabase(String pgDatabase) {
    this.pgDatabase = pgDatabase;
  }

  public String getPgUser() {
    return pgUser;
  }

  public void setPgUser(String pgUser) {
    this.pgUser = pgUser;
  }

  public String getPgPassword() {
    return pgPassword;
  }

  public void setPgPassword(String pgPassword) {
    this.pgPassword = pgPassword;
  }

  public String getGpkgFile() {
    return gpkgFile;
  }

  public void setGpkgFile(String gpkgFile) {
    this.gpkgFile = gpkgFile;
  }

  public List<Ili2dbOptionEntry> getOptionEntries() {
    return optionEntries;
  }

  public void setOptionEntries(List<Ili2dbOptionEntry> optionEntries) {
    this.optionEntries = optionEntries == null ? new ArrayList<>() : optionEntries;
  }
}
