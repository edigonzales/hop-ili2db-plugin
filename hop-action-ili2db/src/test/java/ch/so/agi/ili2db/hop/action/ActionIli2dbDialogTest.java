package ch.so.agi.ili2db.hop.action;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class ActionIli2dbDialogTest {

  @Test
  void shouldContainOptionsAndDatasetWidgets() throws Exception {
    Class<?> clazz = ActionIli2dbDialog.class;

    Field options = clazz.getDeclaredField("wOptions");
    Field datasetName = clazz.getDeclaredField("wDatasetName");
    Field flavor = clazz.getDeclaredField("wFlavor");

    assertNotNull(options);
    assertNotNull(datasetName);
    assertNotNull(flavor);
  }
}
