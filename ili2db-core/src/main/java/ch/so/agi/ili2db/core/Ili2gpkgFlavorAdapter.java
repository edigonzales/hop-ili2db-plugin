package ch.so.agi.ili2db.core;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2gpkg.GpkgMain;

public class Ili2gpkgFlavorAdapter implements Ili2dbFlavorAdapter {

  private final GpkgMain main = new GpkgMain();

  @Override
  public Ili2dbFlavor flavor() {
    return Ili2dbFlavor.ILI2GPKG;
  }

  @Override
  public void initConfig(Config config) {
    main.initConfig(config);
  }

  @Override
  public DbUrlConverter getDbUrlConverter() {
    return main.getDbUrlConverter();
  }

  @Override
  public void applyTarget(Config config, Ili2dbExecutionRequest request) {
    config.setDbfile(request.getGpkgFile());
    if (request.getSchemaName() != null && !request.getSchemaName().isBlank()) {
      config.setDbschema(request.getSchemaName());
    }
  }
}
