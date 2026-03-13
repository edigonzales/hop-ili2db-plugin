package ch.so.agi.ili2db.hop.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.so.agi.ili2db.core.Ili2dbExecutionRequest;
import ch.so.agi.ili2db.core.Ili2dbFlavor;
import ch.so.agi.ili2db.core.Ili2dbFunction;
import org.junit.jupiter.api.Test;

class Ili2dbLogFileNameTest {

  @Test
  void shouldBuildLogFileNameFromUnixImportPath() {
    Ili2dbExecutionRequest request = createRequest("/tmp/parcel.xtf");

    String fileName = Ili2db.buildLogFileName(request);

    assertEquals("parcel.xtf.log", fileName);
  }

  @Test
  void shouldBuildLogFileNameFromWindowsImportPath() {
    Ili2dbExecutionRequest request = createRequest("C:\\data\\parcel.xtf");

    String fileName = Ili2db.buildLogFileName(request);

    assertEquals("parcel.xtf.log", fileName);
  }

  @Test
  void shouldBuildLogFileNameFromIlidataId() {
    Ili2dbExecutionRequest request = createRequest("ilidata:ch.so.agi.mopublic");

    String fileName = Ili2db.buildLogFileName(request);

    assertEquals("ch.so.agi.mopublic.log", fileName);
  }

  @Test
  void shouldUseFallbackWhenImportFileIsMissing() {
    Ili2dbExecutionRequest request = createRequest(null);

    String fileName = Ili2db.buildLogFileName(request);

    assertTrue(fileName.matches("^ili2db-ili2gpkg-import-\\d+-\\d+\\.log$"));
  }

  @Test
  void shouldUseFallbackWhenImportFileEndsWithSeparator() {
    Ili2dbExecutionRequest request = createRequest("/tmp/data/");

    String fileName = Ili2db.buildLogFileName(request);

    assertTrue(fileName.matches("^ili2db-ili2gpkg-import-\\d+-\\d+\\.log$"));
  }

  @Test
  void shouldUseFallbackWhenMultipleImportSourcesAreConfigured() {
    Ili2dbExecutionRequest request =
        createRequest("ilidata:ch.so.agi.mopublic;ilidata:ch.so.agi.other");

    String fileName = Ili2db.buildLogFileName(request);

    assertTrue(fileName.matches("^ili2db-ili2gpkg-import-\\d+-\\d+\\.log$"));
  }

  private Ili2dbExecutionRequest createRequest(String importFile) {
    Ili2dbExecutionRequest request = new Ili2dbExecutionRequest();
    request.setFlavor(Ili2dbFlavor.ILI2GPKG);
    request.setFunction(Ili2dbFunction.IMPORT);
    request.setImportFile(importFile);
    return request;
  }
}
