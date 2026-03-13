package ch.so.agi.ili2db.hop.transform;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;

class Ili2dbDialogTest {

  @Test
  void shouldContainOptionsTableAndDatasetFieldWidgets() throws Exception {
    Class<?> clazz = Ili2dbDialog.class;

    Field options = clazz.getDeclaredField("wOptions");
    Field datasetField = clazz.getDeclaredField("wDatasetField");
    Field flavor = clazz.getDeclaredField("wFlavor");
    Field gpkgTargetMode = clazz.getDeclaredField("wGpkgTargetMode");
    Field gpkgFileField = clazz.getDeclaredField("wGpkgFileField");
    Field outputConnectionField = clazz.getDeclaredField("wOutputConnectionField");
    Field outputTargetFileField = clazz.getDeclaredField("wOutputTargetFileField");
    Field outputDatabaseSchemaField = clazz.getDeclaredField("wOutputDatabaseSchemaField");

    assertNotNull(options);
    assertNotNull(datasetField);
    assertNotNull(flavor);
    assertNotNull(gpkgTargetMode);
    assertNotNull(gpkgFileField);
    assertNotNull(outputConnectionField);
    assertNotNull(outputTargetFileField);
    assertNotNull(outputDatabaseSchemaField);
  }

  @Test
  void shouldExposeIlidataAwareImportLabelsInResources() {
    ResourceBundle englishMessages =
        ResourceBundle.getBundle("ch.so.agi.ili2db.hop.transform.messages.messages", Locale.US);
    ResourceBundle germanMessages =
        ResourceBundle.getBundle(
            "ch.so.agi.ili2db.hop.transform.messages.messages", Locale.GERMANY);

    assertEquals(
        "Import file path / ilidata-ID",
        englishMessages.getString("Ili2dbDialog.ImportFilePath.Label"));
    assertEquals(
        "Import source field",
        englishMessages.getString("Ili2dbDialog.ImportFileField.Label"));
    assertEquals(
        "Importdatei-Pfad / ilidata-ID",
        germanMessages.getString("Ili2dbDialog.ImportFilePath.Label"));
    assertEquals(
        "Feld fuer Importquelle",
        germanMessages.getString("Ili2dbDialog.ImportFileField.Label"));
  }
}
