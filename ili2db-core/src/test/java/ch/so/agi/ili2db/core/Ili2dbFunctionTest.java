package ch.so.agi.ili2db.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ehi.ili2db.gui.Config;
import org.junit.jupiter.api.Test;

class Ili2dbFunctionTest {

  @Test
  void shouldMapSchemaImportFunction() {
    assertEquals(Config.FC_SCHEMAIMPORT, Ili2dbFunction.SCHEMA_IMPORT.getConfigFunction());
  }

  @Test
  void shouldMapImportFunction() {
    assertEquals(Config.FC_IMPORT, Ili2dbFunction.IMPORT.getConfigFunction());
  }

  @Test
  void shouldMapReplaceFunction() {
    assertEquals(Config.FC_REPLACE, Ili2dbFunction.REPLACE.getConfigFunction());
  }

  @Test
  void shouldMapUpdateFunction() {
    assertEquals(Config.FC_UPDATE, Ili2dbFunction.UPDATE.getConfigFunction());
  }

  @Test
  void shouldMapValidateFunction() {
    assertEquals(Config.FC_VALIDATE, Ili2dbFunction.VALIDATE.getConfigFunction());
  }
}
