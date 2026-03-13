package ch.so.agi.ili2db.hop.action;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;
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

  @Test
  void shouldExposeIlidataAwareImportLabelInResources() {
    ResourceBundle englishMessages =
        ResourceBundle.getBundle("ch.so.agi.ili2db.hop.action.messages.messages", Locale.US);
    ResourceBundle germanMessages =
        ResourceBundle.getBundle(
            "ch.so.agi.ili2db.hop.action.messages.messages", Locale.GERMANY);

    assertEquals(
        "Import file path / ilidata-ID",
        englishMessages.getString("ActionIli2dbDialog.ImportFilePath.Label"));
    assertEquals(
        "Importdatei-Pfad / ilidata-ID",
        germanMessages.getString("ActionIli2dbDialog.ImportFilePath.Label"));
  }
}
