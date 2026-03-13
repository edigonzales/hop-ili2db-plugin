package ch.so.agi.ili2db.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class DefaultIli2dbExecutorTest {

  @Test
  void shouldReportMissingImportSourceWithNeutralMessage() {
    Ili2dbExecutionRequest request = new Ili2dbExecutionRequest();
    request.setFlavor(Ili2dbFlavor.ILI2GPKG);
    request.setFunction(Ili2dbFunction.IMPORT);
    request.setGpkgFile("target/test.gpkg");

    Ili2dbExecutionResult result = new DefaultIli2dbExecutor().execute(request);

    assertFalse(result.isSuccess());
    assertEquals("Import source is required for function IMPORT", result.getMessage());
  }
}
