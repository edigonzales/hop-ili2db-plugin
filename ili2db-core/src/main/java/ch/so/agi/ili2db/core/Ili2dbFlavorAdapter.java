package ch.so.agi.ili2db.core;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.Config;

public interface Ili2dbFlavorAdapter {

  Ili2dbFlavor flavor();

  void initConfig(Config config);

  DbUrlConverter getDbUrlConverter();

  void applyTarget(Config config, Ili2dbExecutionRequest request);
}
