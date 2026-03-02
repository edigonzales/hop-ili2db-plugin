package ch.so.agi.ili2db.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ehi.ili2db.gui.Config;
import org.junit.jupiter.api.Test;

class Ili2gpkgFlavorAdapterTest {

  @Test
  void shouldApplyGpkgTarget() {
    Ili2gpkgFlavorAdapter adapter = new Ili2gpkgFlavorAdapter();
    Config config = new Config();
    adapter.initConfig(config);

    Ili2dbExecutionRequest request = new Ili2dbExecutionRequest();
    request.setGpkgFile("/tmp/data.gpkg");

    adapter.applyTarget(config, request);

    assertEquals("/tmp/data.gpkg", config.getDbfile());
  }
}
