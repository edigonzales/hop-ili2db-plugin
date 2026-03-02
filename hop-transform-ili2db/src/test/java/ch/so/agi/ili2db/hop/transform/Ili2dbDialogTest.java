package ch.so.agi.ili2db.hop.transform;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class Ili2dbDialogTest {

  @Test
  void shouldContainOptionsTableAndDatasetFieldWidgets() throws Exception {
    Class<?> clazz = Ili2dbDialog.class;

    Field options = clazz.getDeclaredField("wOptions");
    Field datasetField = clazz.getDeclaredField("wDatasetField");
    Field flavor = clazz.getDeclaredField("wFlavor");

    assertNotNull(options);
    assertNotNull(datasetField);
    assertNotNull(flavor);
  }
}
