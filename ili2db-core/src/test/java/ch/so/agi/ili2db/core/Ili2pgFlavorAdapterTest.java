package ch.so.agi.ili2db.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ehi.ili2db.gui.Config;
import org.junit.jupiter.api.Test;

class Ili2pgFlavorAdapterTest {

  @Test
  void shouldApplyPostgresqlTarget() {
    Ili2pgFlavorAdapter adapter = new Ili2pgFlavorAdapter();
    Config config = new Config();
    adapter.initConfig(config);

    Ili2dbExecutionRequest request = new Ili2dbExecutionRequest();
    request.setPgHost("localhost");
    request.setPgPort("5432");
    request.setPgDatabase("gis");
    request.setPgUser("hop");
    request.setPgPassword("secret");
    request.setSchemaName("demo");

    adapter.applyTarget(config, request);

    assertEquals("localhost", config.getDbhost());
    assertEquals("5432", config.getDbport());
    assertEquals("gis", config.getDbdatabase());
    assertEquals("hop", config.getDbusr());
    assertEquals("secret", config.getDbpwd());
    assertEquals("demo", config.getDbschema());
  }
}
