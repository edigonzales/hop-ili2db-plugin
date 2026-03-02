package ch.so.agi.ili2db.hop.transform;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.hop.core.row.RowMeta;
import org.junit.jupiter.api.Test;

class Ili2dbMetaTest {

  @Test
  void shouldAddConfiguredOutputFields() throws Exception {
    Ili2dbMeta meta = new Ili2dbMeta();
    meta.setDefault();

    RowMeta rowMeta = new RowMeta();
    meta.getFields(rowMeta, "origin", null, null, null, null);

    assertTrue(rowMeta.indexOfValue("ili2db_success") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_message") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_dataset_effective") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_function") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_flavor") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_target_type") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_target_id") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_target_jdbc_url") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_connection") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_target_file") >= 0);
    assertTrue(rowMeta.indexOfValue("ili2db_database_schema") >= 0);
  }
}
