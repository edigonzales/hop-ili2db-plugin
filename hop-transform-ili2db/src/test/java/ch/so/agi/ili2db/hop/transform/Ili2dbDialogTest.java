package ch.so.agi.ili2db.hop.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import ch.so.agi.hop.commons.core.SourceMode;
import ch.so.agi.hop.commons.core.ValueOrField;
import ch.so.agi.hop.commons.ui.ValueOrFieldControl;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hop.core.HopClientEnvironment;
import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.plugins.TransformPluginType;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.core.xml.XmlHandler;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Ili2dbDialogTest {

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void sourceChoicesReachPipelineXmlOnlyAfterOk(boolean accept) throws Exception {
    HopClientEnvironment.init();
    PluginRegistry.getInstance().registerPluginClass(
        Ili2dbMeta.class.getName(), TransformPluginType.class, Transform.class);
    var meta = new Ili2dbMeta();
    meta.setDefault();
    meta.setGpkgFilePath("${TARGET}");
    meta.setGpkgFileField("target_path");
    meta.setImportFilePath("${IMPORT}");
    meta.setImportFileField("source_path");
    meta.setImportSourceMode(SourceMode.FIELD);
    meta.setDatasetName("${DATASET}");
    meta.setDatasetField("dataset");
    meta.setChanged(false);
    String before = meta.getXml();

    var pipeline = new PipelineMeta();
    pipeline.addTransform(new TransformMeta("Load", meta));
    Display display = Display.getDefault();
    Shell parent = new Shell(display);
    try {
      var dialog = new Ili2dbDialog(parent, new Variables(), meta, pipeline);
      var failure = new AtomicReference<Throwable>();
      display.asyncExec(() -> {
        Shell active = Arrays.stream(display.getShells())
            .filter(shell -> shell.getParent() == parent)
            .findFirst()
            .orElse(null);
        try {
          selectMode(dialog, "wGpkgTarget", SourceMode.FIELD, "${TARGET}", "target_path");
          selectMode(dialog, "wImportSource", SourceMode.CONFIGURED, "${IMPORT}", "source_path");
          selectMode(dialog, "wDataset", SourceMode.FIELD, "${DATASET}", "dataset");
          assertEquals(before, meta.getXml());
          String button = BaseMessages.getString(
              Ili2dbMeta.class, accept ? "System.Button.OK" : "System.Button.Cancel");
          Arrays.stream(active.getChildren())
              .filter(Button.class::isInstance)
              .map(Button.class::cast)
              .filter(child -> button.equals(child.getText()))
              .findFirst()
              .orElseThrow()
              .notifyListeners(SWT.Selection, new Event());
        } catch (Throwable error) {
          failure.set(error);
          if (active != null && !active.isDisposed()) {
            active.close();
          }
        }
      });

      String name = dialog.open();
      if (failure.get() != null) {
        throw new AssertionError("ili2db dialog interaction failed", failure.get());
      }
      if (accept) {
        assertEquals("Load", name);
        var saved = new Ili2dbMeta();
        saved.loadXml(
            XmlHandler.loadXmlString("<transform>" + meta.getXml() + "</transform>", "transform"),
            null);
        assertEquals(SourceMode.FIELD, saved.getGpkgTargetMode());
        assertEquals("${TARGET}", saved.getGpkgFilePath());
        assertEquals("target_path", saved.getGpkgFileField());
        assertEquals(SourceMode.CONFIGURED, saved.getImportSourceMode());
        assertEquals("${IMPORT}", saved.getImportFilePath());
        assertEquals("source_path", saved.getImportFileField());
        assertEquals(SourceMode.FIELD, saved.getDatasetMode());
        assertEquals("${DATASET}", saved.getDatasetName());
        assertEquals("dataset", saved.getDatasetField());
      } else {
        assertNull(name);
        assertFalse(meta.hasChanged());
        assertEquals(before, meta.getXml());
      }
    } finally {
      parent.dispose();
    }
  }

  private static void selectMode(
      Ili2dbDialog dialog, String widget, SourceMode expected, String value, String field)
      throws ReflectiveOperationException {
    var member = Ili2dbDialog.class.getDeclaredField(widget);
    member.setAccessible(true);
    var control = (ValueOrFieldControl) member.get(dialog);
    assertEquals(expected == SourceMode.FIELD ? SourceMode.CONFIGURED : SourceMode.FIELD,
        control.getValue().mode());
    Combo selector = Arrays.stream(control.getChildren())
        .filter(Combo.class::isInstance)
        .map(Combo.class::cast)
        .findFirst()
        .orElseThrow();
    selector.select(expected == SourceMode.FIELD ? 1 : 0);
    selector.notifyListeners(SWT.Selection, new Event());
    assertEquals(new ValueOrField(expected, value, field), control.getValue());
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
        "Importdatei-Pfad / ilidata-ID",
        germanMessages.getString("Ili2dbDialog.ImportFilePath.Label"));
  }
}
