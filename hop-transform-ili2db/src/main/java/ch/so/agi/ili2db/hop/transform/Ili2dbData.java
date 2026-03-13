package ch.so.agi.ili2db.hop.transform;

import ch.so.agi.ili2db.core.Ili2dbExecutor;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.pipeline.transform.BaseTransformData;
import org.apache.hop.pipeline.transform.ITransformData;

public class Ili2dbData extends BaseTransformData implements ITransformData {

  IRowMeta outputRowMeta;

  int inputImportFileIndex = -1;
  int inputGpkgFileIndex = -1;
  int inputDatasetIndex = -1;

  int outputSuccessIndex = -1;
  int outputMessageIndex = -1;
  int outputDatasetIndex = -1;
  int outputFunctionIndex = -1;
  int outputFlavorIndex = -1;
  int outputTargetTypeIndex = -1;
  int outputTargetIdIndex = -1;
  int outputTargetJdbcUrlIndex = -1;
  int outputConnectionIndex = -1;
  int outputTargetFileIndex = -1;
  int outputDatabaseSchemaIndex = -1;
  int outputLogFilePathIndex = -1;

  Ili2dbExecutor executor;

  boolean initialized;
  boolean emittedSingleStaticRow;
}
