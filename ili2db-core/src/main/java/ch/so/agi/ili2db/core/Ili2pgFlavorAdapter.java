package ch.so.agi.ili2db.core;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2pg.PgMain;

public class Ili2pgFlavorAdapter implements Ili2dbFlavorAdapter {

  private final PgMain main = new PgMain();

  @Override
  public Ili2dbFlavor flavor() {
    return Ili2dbFlavor.ILI2PG;
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
    config.setDbhost(request.getPgHost());
    config.setDbport(request.getPgPort());
    config.setDbdatabase(request.getPgDatabase());
    config.setDbusr(request.getPgUser());
    config.setDbpwd(request.getPgPassword());
    if (request.getSchemaName() != null && !request.getSchemaName().isBlank()) {
      config.setDbschema(request.getSchemaName());
    }
  }
}
